package com.logant.BookingSystem.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.logant.BookingSystem.Entity.Booking;
import com.logant.BookingSystem.Entity.ClassSchedule;
import com.logant.BookingSystem.Entity.Country;
import com.logant.BookingSystem.Entity.User;
import com.logant.BookingSystem.Entity.UserPackage;
import com.logant.BookingSystem.Entity.Waitlist;
import com.logant.BookingSystem.Enum.BookingStatus;
import com.logant.BookingSystem.Repository.BookingRepository;
import com.logant.BookingSystem.Repository.ClassScheduleRepository;
import com.logant.BookingSystem.Repository.PackageRepository;
import com.logant.BookingSystem.Repository.UserPackageRepository;
import com.logant.BookingSystem.Repository.UserRepository;
import com.logant.BookingSystem.Repository.WaitlistRepository;

import jakarta.transaction.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

        @Autowired
        private BookingRepository bookingRepository;

        @Autowired
        private ClassScheduleRepository classScheduleRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private UserPackageRepository userPackageRepository;

        @Autowired
        private WaitlistRepository waitlistRepository;

        @Autowired
        PackageRepository packageRepository;

        @Autowired
        private RedisTemplate<String, String> redisTemplate;

        // ----------- book a class ----------

        public String bookClass(Long userId, Long classScheduleId) throws Exception {
                ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
                String lockKey = "lock:classSchedule:" + classScheduleId;
                String slotsKey = "slots:classSchedule:" + classScheduleId;

                // Acquire lock with a timeout of 5 seconds
                Boolean lockAcquired = valueOps.setIfAbsent(lockKey, "1", Duration.ofSeconds(5));

                if (!Boolean.TRUE.equals(lockAcquired)) {
                        return "Booking in progress. Please try again shortly.";
                }

                try {
                        // Fetch the class and user from the database
                        ClassSchedule classSchedule = classScheduleRepository.findById(classScheduleId)
                                        .orElseThrow(() -> new Exception("Class not found"));
                        User user = userRepository.findById(userId)
                                        .orElseThrow(() -> new Exception("User not found"));

                                if (bookingRepository.existsByUserAndClassSchedule(user, classSchedule)) {
                                return "User has already booked this class.";
                        }

                        // Load or initialize slots count in Redis
                        String slotsValue = String.valueOf(valueOps.get(slotsKey));
                        Integer availableSlots;
                        if (slotsValue == null) {
                                availableSlots = classSchedule.getAvailableSlots();
                                valueOps.set(slotsKey, String.valueOf(availableSlots));
                        } else {
                                availableSlots = Integer.parseInt(slotsValue);
                        }

                        if (availableSlots > 0) {
                                // Proceed with booking logic
                                Booking booking = new Booking();
                                booking.setUser(user);
                                booking.setClassSchedule(classSchedule);
                                booking.setStatus(BookingStatus.BOOKED);
                                booking.setCreditsUsed(classSchedule.getRequiredCredits());
                                booking.setBookingDate(LocalDateTime.now());
                                booking.setCheckedIn(false);

                                bookingRepository.save(booking);

                                // Deduct credits from the user's package
                                UserPackage userPackage = userPackageRepository
                                                .findByUserIdAndPkg_Country(userId, classSchedule.getCountry())
                                                .orElseThrow(() -> new Exception(
                                                                "User does not have a valid package for this country"));
                                userPackage.setCredits(userPackage.getCredits() - classSchedule.getRequiredCredits());
                                userPackageRepository.save(userPackage);

                                availableSlots -= 1;
                                valueOps.set(slotsKey, String.valueOf(availableSlots));
                                classSchedule.setAvailableSlots(availableSlots);
                                classScheduleRepository.save(classSchedule);

                                return "Book Class Successfully";
                        } else {
                                // ------ added to waitlist ------------
                                Waitlist waitlist = new Waitlist();
                                waitlist.setUser(user);
                                waitlist.setClassSchedule(classSchedule);
                                int waitlistPosition = waitlistRepository.countByClassSchedule(classSchedule) + 1;
                                waitlist.setPosition(waitlistPosition);
                                waitlist.setAddedDate(LocalDateTime.now());
                                waitlistRepository.save(waitlist);

                                return "Class is full. Added to the waitlist at position " + waitlistPosition;
                        }
                } finally {
                        // Release the Redis lock
                        redisTemplate.delete(lockKey);
                }
        }

        // ----- to cancel booking and return credits to the user package ------
        public void cancelBooking(Long bookingId) throws Exception {
                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new Exception("Booking not found"));

                ClassSchedule classSchedule = booking.getClassSchedule();
                LocalDateTime classStartTime = classSchedule.getStartTime();
                LocalDateTime currentTime = LocalDateTime.now();

                // Check if cancellation is within 4 hours of class start time
                long hoursUntilClass = Duration.between(currentTime, classStartTime).toHours();

                if (hoursUntilClass >= 4) {
                        // Refund credits to the user's package
                        Country classCountry = classSchedule.getCountry();
                        UserPackage userPackage = userPackageRepository.findByUserIdAndPkg_Country(
                                        booking.getUser().getId(),
                                        classCountry).orElseThrow(() -> new Exception("Package not found for user"));

                        userPackage.setCredits(userPackage.getCredits() + booking.getCreditsUsed());
                        userPackageRepository.save(userPackage);
                }

                // Revert available slots for the class
                classSchedule.setAvailableSlots(classSchedule.getAvailableSlots() + 1);

                // Check if there are any users in the waitlist for this class
                Waitlist waitlistUser = waitlistRepository.findFirstByClassScheduleOrderByPositionAsc(classSchedule);
                if (waitlistUser != null) {
                        // Book the first waitlist user for the class
                        User waitlistedUser = waitlistUser.getUser();

                        // Create a new booking for the waitlisted user
                        Booking newBooking = new Booking();
                        newBooking.setUser(waitlistedUser);
                        newBooking.setClassSchedule(classSchedule);
                        newBooking.setStatus(BookingStatus.BOOKED);
                        newBooking.setCreditsUsed(classSchedule.getRequiredCredits());
                        newBooking.setBookingDate(LocalDateTime.now());
                        newBooking.setCheckedIn(false);

                        // Save the new booking
                        bookingRepository.save(newBooking);

                        // Remove the user from the waitlist
                        waitlistRepository.delete(waitlistUser);

                        // Adjust available slots as one waitlisted user took the canceled spot
                        classSchedule.setAvailableSlots(classSchedule.getAvailableSlots() - 1);
                }

                classScheduleRepository.save(classSchedule);

                // Update the original booking status to cancelled
                booking.setStatus(BookingStatus.CANCELLED);
                booking.setCancellationDate(currentTime);
                bookingRepository.save(booking);
        }

        // ----- get ended classes
        public List<ClassSchedule> getEndedClasses() {
                return classScheduleRepository.findByEndTimeBefore(LocalDateTime.now());
        }

        // ---- get waitlist users for a specific class
        public List<Waitlist> getWaitlist(ClassSchedule classSchedule) {
                return waitlistRepository.findByClassSchedule(classSchedule);
        }

        @Transactional
        public void refundCreditsToWaitlistUser(Waitlist waitlistUser, ClassSchedule endedClass) throws Exception {

                User user = waitlistUser.getUser();
                UserPackage userPackage = userPackageRepository
                                .findByUserIdAndPkg_Country(user.getId(), endedClass.getCountry())
                                .orElseThrow(() -> new Exception("User Purchased Package not found!"));

                int creditsToRefund = endedClass.getRequiredCredits();
                userPackage.setCredits(userPackage.getCredits() + creditsToRefund);
                userPackageRepository.save(userPackage);

                // finally delete user from wait list
                waitlistRepository.delete(waitlistUser);
        }
}
