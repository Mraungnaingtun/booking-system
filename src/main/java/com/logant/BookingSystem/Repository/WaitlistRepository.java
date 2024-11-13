package com.logant.BookingSystem.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logant.BookingSystem.Entity.Waitlist;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {}