package com.logant.BookingSystem.Controller;

import com.logant.BookingSystem.Dto.ResponseWrapper;
import com.logant.BookingSystem.Entity.Package;
import com.logant.BookingSystem.Service.PackageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
@Tag(name = "Package Module", description = "Check Available Packages / Buy Package / See Own Packages")
public class PackageController {

    @Autowired
    private PackageService packageService;

    @Operation(summary = "Get Available Packages", description = "To get all available packages")
    @GetMapping("/available")
    public ResponseEntity<?> getAvailablePackages() {
        try {
            List<Package> availablePackages = packageService.getAvailablePackages();
            return ResponseWrapper.success(availablePackages);
        } catch (Exception e) {
            return ResponseWrapper.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    @Operation(summary = "Purchase New Package", description = "To purchase a new package with userid and package id")
    @PostMapping("/buy/{userId}/{packageId}")
    public ResponseEntity<?> buyPackage(@PathVariable Long userId, @PathVariable Long packageId) {
        try {

            return ResponseWrapper.success(packageService.buyPackage(userId, packageId));

        } catch (IllegalStateException e) {

            return ResponseWrapper.error(HttpStatus.BAD_REQUEST, e.getMessage());

        } catch (IllegalArgumentException e) {
            
            return ResponseWrapper.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Get User Own Package", description = "To get all packages with userid")
    @GetMapping("/user/{userId}/purchased")
    public ResponseEntity<?> getUserPurchasedPackages(@PathVariable Long userId) {
        try {
            List<Package> userPackages = packageService.getUserPurchasedPackages(userId);
            return ResponseWrapper.success(userPackages);
        } catch (Exception e) {
            return ResponseWrapper.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Create New Package(Admin)", description = "To create new package by an admin or manager")
    @PostMapping
    public ResponseEntity<?> createPackage(@RequestBody Package pkg) {
        try {
            Package savedPackage = packageService.createPackage(pkg);
            return ResponseWrapper.success(savedPackage);
        } catch (Exception e) {
            return ResponseWrapper.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
