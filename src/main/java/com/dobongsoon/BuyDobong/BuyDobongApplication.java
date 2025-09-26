package com.dobongsoon.BuyDobong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BuyDobongApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuyDobongApplication.class, args);
	}

}
