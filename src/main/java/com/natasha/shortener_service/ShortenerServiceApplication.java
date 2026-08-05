package com.natasha.shortener_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ShortenerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShortenerServiceApplication.class, args);
	}

}
