package com.mytoshika.ecommerceoms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ECommerceOmsApplication {
	public static
	void main(String[] args) {
		SpringApplication.run(ECommerceOmsApplication.class, args);
	}
}
