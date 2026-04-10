package com.Edutrack.enrollments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EnrollmentsApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnrollmentsApplication.class, args);
	}

}
