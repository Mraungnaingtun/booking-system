package com.logant.BookingSystem.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logant.BookingSystem.Entity.ClassSchedule;
import com.logant.BookingSystem.Repository.ClassScheduleRepository;

import jakarta.transaction.Transactional;

@Service
public class ClassScheduleService {

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    // Create or Update a ClassSchedule
    @Transactional
    public ClassSchedule saveClassSchedule(ClassSchedule classSchedule) {
        classSchedule.setCreatedAt(LocalDateTime.now());
        return classScheduleRepository.save(classSchedule);
    }

    public List<ClassSchedule> getAllClassSchedules() {
        List<ClassSchedule> allClassSchedules = classScheduleRepository.findAll();

        // Filter out expired classes based on endTime
        LocalDateTime now = LocalDateTime.now();
        return allClassSchedules.stream()
                .filter(classSchedule -> classSchedule.getEndTime().isAfter(now))
                .collect(Collectors.toList());
    }

    // Get a ClassSchedule by ID
    public Optional<ClassSchedule> getClassScheduleById(Long classId) {
        return classScheduleRepository.findById(classId);
    }

    // Delete a ClassSchedule by ID
    public void deleteClassSchedule(Long classId) {
        classScheduleRepository.deleteById(classId);
    }

    // Update a ClassSchedule
    @Transactional
    public ClassSchedule updateClassSchedule(Long classId, ClassSchedule updatedClassSchedule) {
        if (classScheduleRepository.existsById(classId)) {
            updatedClassSchedule.setId(classId);
            return classScheduleRepository.save(updatedClassSchedule);
        }
        return null;
    }
}
