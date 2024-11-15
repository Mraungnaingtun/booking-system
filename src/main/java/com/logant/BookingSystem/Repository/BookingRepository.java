package com.logant.BookingSystem.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logant.BookingSystem.Entity.Booking;
import com.logant.BookingSystem.Entity.ClassSchedule;
import com.logant.BookingSystem.Entity.User;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    Optional<Booking> findByUserIdAndClassScheduleId(Long userId, Long classScheduleId);
    boolean existsByUserAndClassSchedule(User user, ClassSchedule classSchedule);
}