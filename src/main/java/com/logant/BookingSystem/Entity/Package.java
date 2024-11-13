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
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Data
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String name;
    
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    private int credits;
    private double price;
    private int validityDays;
    private LocalDateTime createdAt = LocalDateTime.now();


    @Transient
    private String status;

    @JsonIgnore
    @OneToMany(mappedBy = "pkg")
    private List<UserPackage> userPackages;
}
