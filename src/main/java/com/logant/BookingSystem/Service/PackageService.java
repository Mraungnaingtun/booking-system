package com.logant.BookingSystem.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logant.BookingSystem.Repository.CountryRepository;
import com.logant.BookingSystem.Repository.PackageRepository;
import com.logant.BookingSystem.Repository.UserPackageRepository;
import com.logant.BookingSystem.Entity.User;

import jakarta.transaction.Transactional;
import com.logant.BookingSystem.Repository.UserRepository;
import com.logant.BookingSystem.Entity.Country;
import com.logant.BookingSystem.Entity.Package;
import com.logant.BookingSystem.Entity.UserPackage;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Service
public class PackageService {

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPackageRepository userPackageRepository;

    @Autowired
    private CountryRepository countryRepository;

    // Get all packages
    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }

    // Check if a package is available for purchase
    public boolean isPackageAvailable(Package pkg) {
        LocalDate currentDate = LocalDate.now();
        LocalDate expirationDate = pkg.getCreatedAt().toLocalDate().plusDays(pkg.getValidityDays());
        return !currentDate.isAfter(expirationDate); // Package is available if it's not expired
    }

    public List<Package> getUserPurchasedPackages(Long userId) {
        // Fetch user purchased packages by userId
        List<UserPackage> userPackages = userPackageRepository.findByUserId(userId);

        // Set the 'status' field for each package based on its validity
        for (UserPackage userPackage : userPackages) {
            Package pkg = userPackage.getPkg();
            LocalDate packageExpirationDate = pkg.getCreatedAt().toLocalDate().plusDays(pkg.getValidityDays());
            if (packageExpirationDate.isBefore(LocalDate.now())) {
                pkg.setStatus("expired");
            } else {
                pkg.setStatus("active");
            }
        }

        // Extract the Package objects from the UserPackage list
        return userPackages.stream()
                .map(UserPackage::getPkg)
                .toList();
    }

    // Fetch all available packages that the user can buy
    public List<Package> getAvailablePackages() {
        List<Package> availablePackages = packageRepository.findAll().stream()
                .filter(pkg -> isPackageAvailable(pkg))
                .peek(pkg -> pkg.setStatus("Available"))
                .collect(Collectors.toList());
        return availablePackages;
    }

    @Transactional
    public UserPackage buyPackage(Long userId, Long packageId) {
        // Fetch the package and user from the database
        Optional<Package> optionalPackage = packageRepository.findById(packageId);
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalPackage.isPresent() && optionalUser.isPresent()) {
            Package pkg = optionalPackage.get();
            User user = optionalUser.get();

            // Check if the user already has this package
            boolean isPackageAlreadyPurchased = userPackageRepository.existsByUserIdAndPkg_Id(userId, packageId);
            if (isPackageAlreadyPurchased) {
                throw new IllegalStateException("User has already purchased this package.");
            }

            // Check if package is available for purchase
            if (!isPackageAvailable(pkg)) {
                throw new IllegalStateException("Package is not available for purchase.");
            }

            // Create a new UserPackage and save it
            UserPackage userPackage = new UserPackage();
            userPackage.setUser(user);
            userPackage.setPkg(pkg);
            userPackage.setCredits(pkg.getCredits());
            userPackage.setExpirationDate(pkg.getCreatedAt().toLocalDate().plusDays(pkg.getValidityDays()));
            userPackage.setPurchaseDate(LocalDateTime.now());

            return userPackageRepository.save(userPackage);
        } else {
            throw new IllegalArgumentException("User or Package not found.");
        }
    }

    // Get package by ID
    public Optional<Package> getPackageById(Long packageId) {
        return packageRepository.findById(packageId);
    }

    public Package createPackage(Package pkg) {
        // Get the Country from the Package
        Country country = pkg.getCountry();

        // Check if the Country exists in the database by its ID
        if (country.getCountryId() == null || !countryRepository.existsById(country.getCountryId())) {
            // Throw an exception or return an error message if the country doesn't exist
            throw new IllegalArgumentException("Country with ID " + country.getCountryId() + " does not exist.");
        }

        // The Country exists, so proceed with saving the Package
        pkg.setCountry(country);  // Ensure the country is properly set before saving the package

        return packageRepository.save(pkg);  // Save the package
    }

    // Delete package by ID
    public void deletePackage(Long packageId) {
        packageRepository.deleteById(packageId);
    }
}
