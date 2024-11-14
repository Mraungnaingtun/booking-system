package com.logant.BookingSystem.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.logant.BookingSystem.Entity.ClassSchedule;
import com.logant.BookingSystem.Service.BookingService;
import com.logant.BookingSystem.Service.ClassScheduleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/class-schedules")
@Tag(name = "Schedule Module", description = "Operations related to book and cancel class")
public class ClassScheduleController {

    @Autowired
    private ClassScheduleService classScheduleService;

    @Autowired
    private BookingService bookingService;

    @Operation(summary = "Get All Available Classes", description = "Get all classes which user can book")
    @PreAuthorize("hasAnyAuthority('SCOPE_READ')")
    @GetMapping
    public List<ClassSchedule> getAllClassSchedules() {
        return classScheduleService.getAllClassSchedules();
    }



    @Operation(summary = "Book A Class", description = "To book a class using related country package")
    @PreAuthorize("hasAnyAuthority('SCOPE_READ')")
    @PostMapping("book/{userId}/{classScheduleId}")
    public ResponseEntity<?> bookClass(@PathVariable Long userId, @PathVariable Long classScheduleId) {
        try {
            String bookingStatus = bookingService.bookClass(userId, classScheduleId);
            return ResponseEntity.ok(Map.of("status", "success", "data", bookingStatus));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }



    @Operation(summary = "Cancel Booking", description = "To cancel a booking and refund credit back")
    @PreAuthorize("hasAnyAuthority('SCOPE_READ')")
    @PostMapping("cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Booking cancelled successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }



    @Operation(summary = "Create New Class(ADMIN)", description = "To create a new class schedule by Admin or Manger")
    @PreAuthorize("hasAnyAuthority('SCOPE_WRITE')")
    @PostMapping
    public ResponseEntity<ClassSchedule> createClassSchedule(@RequestBody ClassSchedule classSchedule) {
        ClassSchedule savedClassSchedule = classScheduleService.saveClassSchedule(classSchedule);
        return new ResponseEntity<>(savedClassSchedule, HttpStatus.CREATED);
    }


}
