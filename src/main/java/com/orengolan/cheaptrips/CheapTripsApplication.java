package com.orengolan.cheaptrips;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CheapTripsApplication {
	public static void main(String[] args) {
		// Get the PORT environment variable from Heroku
		String port = System.getenv("PORT");
		if (port == null) {
			port = "8080"; // Use a default port for local development
		}

		// Set the port for the embedded web server
		System.setProperty("server.port", port);

		// Start the Spring Boot application
		SpringApplication.run(CheapTripsApplication.class, args);

		// Log a message after the application has started
		System.out.println("CheapTrips application started on port: " + port);
	}
}
