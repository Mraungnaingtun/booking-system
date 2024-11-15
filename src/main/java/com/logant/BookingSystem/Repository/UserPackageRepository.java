package com.logant.BookingSystem.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logant.BookingSystem.Entity.Country;
import com.logant.BookingSystem.Entity.Package;
import com.logant.BookingSystem.Entity.UserPackage;


@Repository
public interface UserPackageRepository extends JpaRepository<UserPackage, Long> {
    List<UserPackage> findByUserId(Long userId);
    UserPackage findByPkg(Package pkg);
    boolean existsByUserIdAndPkg_Id(Long userId, Long pkgId);
    Optional<UserPackage> findByUserIdAndPkg_Country(Long userId, Country pkgCountry);
    UserPackage findByUserIdAndPkg_Id(Long userId, Long pkgId);
}
