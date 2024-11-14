package com.logant.BookingSystem.Service;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.logant.BookingSystem.Repository.UserPackageRepository;
import com.logant.BookingSystem.Repository.UserRepository;
import com.logant.BookingSystem.Repository.WaitlistRepository;

import java.time.LocalDateTime;

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

        public String bookClass(Long userId, Long classScheduleId) throws Exception {
                // Fetch the class and user from the database
                ClassSchedule classSchedule = classScheduleRepository.findById(classScheduleId)
                                .orElseThrow(() -> new Exception("Class not found"));

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new Exception("User not found"));

                // Check if the user has already booked this class
                if (bookingRepository.existsByUserAndClassSchedule(user, classSchedule)) {
                        return "User have already booked this class.";
                }

                // Ensure that the user's package is in the same country
                Country classCountry = classSchedule.getCountry();
                UserPackage userPackage = userPackageRepository.findByUserIdAndPkg_Country(userId, classCountry)
                                .orElseThrow(() -> new Exception(
                                                "User does not have a valid package for this country"));

                // Check if the user has enough credits in the package
                if (userPackage.getCredits() < classSchedule.getRequiredCredits()) {
                        throw new Exception("Not enough credits in the package");
                }

                // Check if there are available slots
                if (classSchedule.getAvailableSlots() > 0) {
                        // Create a new booking
                        Booking booking = new Booking();
                        booking.setUser(user);
                        booking.setClassSchedule(classSchedule);
                        booking.setStatus(BookingStatus.BOOKED);
                        booking.setCreditsUsed(classSchedule.getRequiredCredits());
                        booking.setBookingDate(LocalDateTime.now());
                        booking.setCheckedIn(false);

                        // Save the booking
                        bookingRepository.save(booking);

                        // Deduct credits from the user's package
                        userPackage.setCredits(userPackage.getCredits() - classSchedule.getRequiredCredits());
                        userPackageRepository.save(userPackage);

                        // Decrement available slots for the class
                        classSchedule.setAvailableSlots(classSchedule.getAvailableSlots() - 1);
                        classScheduleRepository.save(classSchedule);

                        return "Book Class Successfully";
                } else {
                        // Add user to the waitlist
                        Waitlist waitlist = new Waitlist();
                        waitlist.setUser(user);
                        waitlist.setClassSchedule(classSchedule);

                        // Set the waitlist position
                        int waitlistPosition = waitlistRepository.countByClassSchedule(classSchedule) + 1;
                        waitlist.setPosition(waitlistPosition);
                        waitlist.setAddedDate(LocalDateTime.now());

                        // Save the waitlist entry
                        waitlistRepository.save(waitlist);

                        // Deduct credits from the user's package
                        userPackage.setCredits(userPackage.getCredits() - classSchedule.getRequiredCredits());
                        userPackageRepository.save(userPackage);

                        return "Class is full. Added to the waitlist at position " + waitlistPosition;
                }
        }



        // -----  to cancel booking and return credits to the package ------
        public void cancelBooking(Long bookingId) throws Exception {
                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new Exception("Booking not found"));

                // Refund credits to the user's package
                Country classCountry = booking.getClassSchedule().getCountry();
                UserPackage userPackage = userPackageRepository.findByUserIdAndPkg_Country(
                                booking.getUser().getId(),
                                classCountry).orElseThrow(() -> new Exception("Package not found for user"));

                userPackage.setCredits(userPackage.getCredits() + booking.getCreditsUsed());
                userPackageRepository.save(userPackage);

                // Revert available slots for the class
                ClassSchedule classSchedule = booking.getClassSchedule();
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
                booking.setCancellationDate(LocalDateTime.now());
                bookingRepository.save(booking);
        }

}
