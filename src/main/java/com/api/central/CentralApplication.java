package com.api.central;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CentralApplication {

	public static void main(String[] args) {

		SpringApplication.run(CentralApplication.class, args);
		System.out.println("database name: "+ System.getenv("DATABASE_NAME"));
	}

}
