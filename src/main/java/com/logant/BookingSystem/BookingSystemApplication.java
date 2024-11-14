package com.logant.BookingSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.logant.BookingSystem.config.RSAKeyRecord;



@EnableConfigurationProperties(RSAKeyRecord.class)
@SpringBootApplication
@EnableScheduling
public class BookingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingSystemApplication.class, args);
	}

}
