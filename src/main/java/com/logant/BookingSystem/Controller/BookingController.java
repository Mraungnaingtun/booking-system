package com.logant.BookingSystem.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.logant.BookingSystem.Service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // ------- book a class using user's package -----------
    @PreAuthorize("hasAnyAuthority('SCOPE_READ')")
    @PostMapping("/{userId}/{classScheduleId}")
    public ResponseEntity<?> bookClass(@PathVariable Long userId, @PathVariable Long classScheduleId) {
        try {
            String bookingStatus = bookingService.bookClass(userId, classScheduleId);
            return ResponseEntity.ok(Map.of("status", "success", "data", bookingStatus));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    // ---------- cancel a booking -----------------
    @PreAuthorize("hasAnyAuthority('SCOPE_DELETE')")
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Booking cancelled successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

}
