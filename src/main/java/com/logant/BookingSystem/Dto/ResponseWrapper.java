package com.logant.BookingSystem.Dto;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseWrapper {


     public static ResponseEntity<?> success(Object data) {
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", data
        ));
    }

    public static ResponseEntity<?> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "status", "error",
                "message", message
        ));
    }
}
