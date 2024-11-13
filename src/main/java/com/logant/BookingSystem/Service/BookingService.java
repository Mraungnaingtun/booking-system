package com.logant.BookingSystem.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logant.BookingSystem.Entity.Booking;
import com.logant.BookingSystem.Entity.ClassSchedule;
import com.logant.BookingSystem.Entity.Country;
import com.logant.BookingSystem.Entity.User;
import com.logant.BookingSystem.Entity.UserPackage;
import com.logant.BookingSystem.Repository.BookingRepository;
import com.logant.BookingSystem.Repository.ClassScheduleRepository;
import com.logant.BookingSystem.Repository.UserPackageRepository;
import com.logant.BookingSystem.Repository.UserRepository;

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

        // Book a class using the user's package credits
        public String bookClass(Long userId, Long classScheduleId) throws Exception {
                // Fetch the class and user from the database
                ClassSchedule classSchedule = classScheduleRepository.findById(classScheduleId)
                                .orElseThrow(() -> new Exception("Class not found"));

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new Exception("User not found"));

                // Ensure that the user's package is in the same country
                Country classCountry = classSchedule.getCountry();
                UserPackage userPackage = userPackageRepository.findByUserIdAndPkg_Country(userId, classCountry)
                                .orElseThrow(() -> new Exception(
                                                "User does not have a valid package for this country"));

                // Check if the user has enough credits in the package
                if (userPackage.getCredits() < classSchedule.getRequiredCredits()) {
                        throw new Exception("Not enough credits in the package");
                }

                // Create a new booking
                Booking booking = new Booking();
                booking.setUser(user);
                booking.setClassSchedule(classSchedule);
                booking.setStatus("Booked");
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
        }

        // Method to cancel booking and return credits to the package
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
                classScheduleRepository.save(classSchedule);

                // Update the booking status to cancelled
                booking.setStatus("Cancelled");
                booking.setCancellationDate(LocalDateTime.now());
                bookingRepository.save(booking);
        }
}
