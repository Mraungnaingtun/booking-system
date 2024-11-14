package com.logant.BookingSystem.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logant.BookingSystem.Entity.Country;
import com.logant.BookingSystem.Entity.Package;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {
    Package findByCountry(Country country);
}
