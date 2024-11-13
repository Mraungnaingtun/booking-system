package com.logant.BookingSystem.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.logant.BookingSystem.Entity.ClassSchedule;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/class-schedules")
public class ClassScheduleController {

    @Autowired
    private ClassScheduleService classScheduleService;

    // Create a new ClassSchedule
    @PostMapping
    public ResponseEntity<ClassSchedule> createClassSchedule(@RequestBody ClassSchedule classSchedule) {
        ClassSchedule savedClassSchedule = classScheduleService.saveClassSchedule(classSchedule);
        return new ResponseEntity<>(savedClassSchedule, HttpStatus.CREATED);
    }

    // Get all ClassSchedules
    @GetMapping
    public List<ClassSchedule> getAllClassSchedules() {
        return classScheduleService.getAllClassSchedules();
    }

    // Get a ClassSchedule by ID
    @GetMapping("/{classId}")
    public ResponseEntity<ClassSchedule> getClassScheduleById(@PathVariable Long classId) {
        Optional<ClassSchedule> classSchedule = classScheduleService.getClassScheduleById(classId);
        return classSchedule.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update a ClassSchedule
    @PutMapping("/{classId}")
    public ResponseEntity<ClassSchedule> updateClassSchedule(
            @PathVariable Long classId, @RequestBody ClassSchedule classSchedule) {
        ClassSchedule updatedClassSchedule = classScheduleService.updateClassSchedule(classId, classSchedule);
        return updatedClassSchedule != null
                ? new ResponseEntity<>(updatedClassSchedule, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Delete a ClassSchedule by ID
    @DeleteMapping("/{classId}")
    public ResponseEntity<Void> deleteClassSchedule(@PathVariable Long classId) {
        classScheduleService.deleteClassSchedule(classId);
        return ResponseEntity.noContent().build();
    }
}
