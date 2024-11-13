package com.logant.BookingSystem.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.logant.BookingSystem.Entity.Package;
import com.logant.BookingSystem.Entity.UserPackage;
import com.logant.BookingSystem.Service.PackageService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/packages")
public class PackageController {

    @Autowired
    private PackageService packageService;

    // --------- to fetch available packages -------------
    @GetMapping("/available")
    public ResponseEntity<List<Package>> getAvailablePackages() {
        List<Package> availablePackages = packageService.getAvailablePackages();
        return ResponseEntity.ok(availablePackages); // Return the list of available packages
    }

    // --------- buy a package -------------
    @PostMapping("/buy/{userId}/{packageId}")
    public ResponseEntity<String> buyPackage(@PathVariable Long userId, @PathVariable Long packageId) {
        
        try {
            UserPackage userPackage = packageService.buyPackage(userId, packageId);
            return ResponseEntity.ok("Package purchased successfully! Package ID: " + userPackage.getUserPackageId());

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //--- purchased packages ----------
    @GetMapping("/user/{userId}/purchased")
    public ResponseEntity<List<Package>> getUserPurchasedPackages(@PathVariable Long userId) {
        List<Package> userPackages = packageService.getUserPurchasedPackages(userId);
        if (userPackages.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(userPackages);
    }

    // Get all packages
    @GetMapping
    public ResponseEntity<List<Package>> getAllPackages() {
        List<Package> packages = packageService.getAllPackages();
        return new ResponseEntity<>(packages, HttpStatus.OK);
    }

    // Get package by ID
    @GetMapping("/{packageId}")
    public ResponseEntity<Package> getPackageById(@PathVariable Long packageId) {
        Optional<Package> pkg = packageService.getPackageById(packageId);
        return pkg.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create or update a package
    @PostMapping
    public ResponseEntity<Package> createPackage(@RequestBody Package pkg) {
        Package savedPackage = packageService.createPackage(pkg);
        return new ResponseEntity<>(savedPackage, HttpStatus.CREATED);
    }

    // Delete package by ID
    @DeleteMapping("/{packageId}")
    public ResponseEntity<Void> deletePackage(@PathVariable Long packageId) {
        packageService.deletePackage(packageId);
        return ResponseEntity.noContent().build();
    }
}
