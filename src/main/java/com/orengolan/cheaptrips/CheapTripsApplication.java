package com.orengolan.cheaptrips;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@SpringBootApplication
public class CheapTripsApplication {
	private static String baseUrl;  // Make it static

	@Value("${base.url}")
	public void setBaseUrl(String baseUrl) {
		CheapTripsApplication.baseUrl = baseUrl;
	}

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
		System.out.println("CheapTrips application has started successfully.");

		// Print the main endpoint URL
		System.out.println("Main Endpoint: " + baseUrl);

		// Log a message after the application has started
		System.out.println("Access the Swagger UI at: " + baseUrl + "swagger-ui.html");
	}

	@Controller
	static class RedirectController {
		@GetMapping({"/"})
		public RedirectView redirectToSwagger() {
			// Assuming Swagger UI is available at /swagger-ui.html
			return new RedirectView("swagger-ui.html");
		}
	}
}
