package com.logant.BookingSystem.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.logant.BookingSystem.Service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // ------- book a class using user's package -----------
    @PostMapping("/{userId}/{classScheduleId}")
    public ResponseEntity<?> bookClass(@PathVariable Long userId, @PathVariable Long classScheduleId) {
        try {
            String booking = bookingService.bookClass(userId, classScheduleId);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage()); 
        }
    }

    // ---------- cancel a booking -----------------
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok("Booking cancelled successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());  // Return error message if any
        }
    }
}
