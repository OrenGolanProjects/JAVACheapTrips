package com.orengolan.cheaptrips;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;
import springfox.documentation.annotations.ApiIgnore;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * The {@code CheapTripsApplication} class is the entry point for the CheapTrips Spring Boot application.
 * It includes the main method for starting the application, and additional configuration for handling
 * the embedded web server's port. The class also defines a SwaggerRedirectController for redirecting
 * the root URL to the Swagger UI documentation.
 * Key Features:
 * - Configuration for setting the web server port, allowing flexibility for deployment.
 * - Integration with Spring Boot to streamline the setup and deployment of the application.
 * - Definition of a SwaggerRedirectController to redirect the root URL to the Swagger UI documentation.
 * Example Usage:
 * The main method is executed to start the Spring Boot application. The embedded web server's port
 * is configured based on the environment, with a default port used for local development.
 * The SwaggerRedirectController ensures that accessing the root URL redirects to the Swagger UI documentation.
 */
@SpringBootApplication
public class CheapTripsApplication {
	private static String baseUrl;  // Make it static

	@Value("${base.url}")
	public void setBaseUrl(String baseUrl) {
		CheapTripsApplication.baseUrl = baseUrl;
	}

	public static void main(String[] args) {

		// Get the PORT environment variable from Heroku or use the default port
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

		// Print the local URL
		try {
			InetAddress localhost = InetAddress.getLocalHost();
			String localUrl = "http://" + localhost.getHostAddress() + ":" + port + "/swagger-ui.html";
			System.out.println("Local URL: " + localUrl);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		// Print the main endpoint URL
		System.out.println("Main Endpoint: " + baseUrl);

		// Log a message after the application has started
		System.out.println("Access the Swagger UI at: " + baseUrl + "swagger-ui.html");
	}

	@Controller
	@ApiIgnore
	static class RedirectController {
		@GetMapping({"/"})
		public RedirectView redirectToSwagger() {
			// Assuming Swagger UI is available at /swagger-ui.html
			return new RedirectView("swagger-ui.html");
		}
	}
}
