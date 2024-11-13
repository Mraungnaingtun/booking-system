package com.logant.BookingSystem.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logant.BookingSystem.Entity.ClassSchedule;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {}