package com.logant.BookingSystem.Entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long countryId;

    private String name;  // e.g. USA, India, etc.

    @JsonIgnore
    @OneToMany(mappedBy = "country")
    private List<ClassSchedule> classSchedules;

    @JsonIgnore
    @OneToMany(mappedBy = "country")
    private List<Package> packages;
}
