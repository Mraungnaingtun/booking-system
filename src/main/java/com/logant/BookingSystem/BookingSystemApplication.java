package com.logant.BookingSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logant.BookingSystem.config.RSAKeyRecord;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;



@EnableConfigurationProperties(RSAKeyRecord.class)
@SpringBootApplication
@EnableScheduling
public class BookingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingSystemApplication.class, args);
	}

}

@RestController
@RequestMapping("test/user")
class TestingController{

	@GetMapping
	public String postMethodName() {
		return "Hello";
	}
	
}