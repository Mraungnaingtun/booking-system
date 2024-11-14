package com.logant.BookingSystem.Entity;

import java.time.LocalDateTime;

import com.logant.BookingSystem.Enum.BookingStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "class_schedule_id")
    private ClassSchedule classSchedule;

    // private String status;
    private int creditsUsed;
    private LocalDateTime bookingDate;
    private LocalDateTime cancellationDate;
    private Boolean checkedIn;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
