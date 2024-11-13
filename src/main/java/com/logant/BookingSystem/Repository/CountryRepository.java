package com.logant.BookingSystem.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logant.BookingSystem.Entity.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {
     Country findByName(String name);
}
