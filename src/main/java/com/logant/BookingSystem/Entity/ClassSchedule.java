package com.logant.BookingSystem.Entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class ClassSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    // private String country;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    private int requiredCredits;
    private int availableSlots;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonIgnore
    @OneToMany(mappedBy = "classSchedule")
    private List<Booking> bookings;

    @JsonIgnore
    @OneToMany(mappedBy = "classSchedule")
    private List<Waitlist> waitlistEntries;

}
