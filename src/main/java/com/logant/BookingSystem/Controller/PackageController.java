package com.logant.BookingSystem.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.logant.BookingSystem.Entity.Package;
import com.logant.BookingSystem.Entity.UserPackage;
import com.logant.BookingSystem.Service.PackageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
@Tag(name = "Package Module", description = "Check Available Packages / Buy Package / See Own Packages")
public class PackageController {

    @Autowired
    private PackageService packageService;

    @Operation(summary = "Get Available Packages", description = "To get all available packages")
    @PreAuthorize("hasAnyAuthority('SCOPE_READ')")
    @GetMapping("/available")
    public ResponseEntity<List<Package>> getAvailablePackages() {
        List<Package> availablePackages = packageService.getAvailablePackages();
        return ResponseEntity.ok(availablePackages); // Return the list of available packages
    }

    @Operation(summary = "Purchase New Package", description = "To purchase a new package with userid and package id")
    @PreAuthorize("hasAnyAuthority('SCOPE_READ')")
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


    @Operation(summary = "Get User Own Package", description = "To get all packages with userid")
    @PreAuthorize("hasAnyAuthority('SCOPE_READ')")
    @GetMapping("/user/{userId}/purchased")
    public ResponseEntity<List<Package>> getUserPurchasedPackages(@PathVariable Long userId) {
        List<Package> userPackages = packageService.getUserPurchasedPackages(userId);
        if (userPackages.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(userPackages);
    }



    @Operation(summary = "Create New Package(Admin)", description = "To create new pakage by a admin or manager")
    @PreAuthorize("hasAnyAuthority('SCOPE_WRITE')")
    @PostMapping
    public ResponseEntity<Package> createPackage(@RequestBody Package pkg) {
        Package savedPackage = packageService.createPackage(pkg);
        return new ResponseEntity<>(savedPackage, HttpStatus.CREATED);
    }
}
