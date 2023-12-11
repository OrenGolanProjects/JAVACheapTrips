package com.orengolan.cheaptrips.config;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import java.util.List;
import static springfox.documentation.builders.PathSelectors.regex;


/**
 * The {@code SwaggerConfig} class is a Spring configuration class responsible for setting up
 * Swagger documentation for the CheapTrips API. It uses the Swagger 2 framework along with
 * springfox libraries to generate API documentation that is accessible through a Swagger UI.
 *
 * The class is annotated with {@code @Configuration} and {@code @EnableSwagger2} to enable
 * Swagger support in the Spring application. It defines a {@code Docket} bean, which is a
 * builder for the Swagger API documentation. The generated documentation includes information
 * about available API endpoints, request and response formats, security configurations, etc.
 *
 * The class also includes methods to customize the API information, security context, and API key
 * used for authentication. It provides a clear and interactive documentation interface to help
 * developers understand and test the available API operations.
 *
 * Example:
 * - Access the Swagger UI at http://localhost:8080/swagger-ui.html
 * - View the API documentation in JSON format at http://localhost:8080/v2/api-docs
 *
 * Additionally, the class defines an {@code ApiInfo} bean, which contains metadata about the API,
 * including title, version, terms of service, and description. The API description provides details
 * about the CheapTrips application, its features, and how to use the API.
 *
 * The class uses Guava's {@code Predicates} for path selection and configuration.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * Header name for API key authentication.
     */
    public static final String AUTHORIZATION_HEADER = "Authorization";
    /**
     * Default include pattern for Swagger paths.
     */
    public static final String DEFAULT_INCLUDE_PATTERN = "/.*";

    /**
     * Configures the Swagger API documentation using Docket.
     *
     * @return Docket instance for configuring Swagger.
     */
    @Bean
    public Docket swaggerSpringfoxDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.orengolan.cheaptrips"))
                .paths(regex(DEFAULT_INCLUDE_PATTERN))
                .build()
                .securityContexts(Lists.newArrayList(securityContext()))
                .securitySchemes(Lists.newArrayList(apiKey()))
                .useDefaultResponseMessages(false);
    }

    /**
     * Creates an {@code ApiInfo} instance with metadata about the API.
     *
     * @return ApiInfo instance with API metadata.
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("CheapTrips API Documentation")
                .description(
                        "# CheapTrips API Documentation\n\n"
                                + "**About**\n\n"
                                + "CheapTrips is designed to simplify trip planning, offering:\n"
                                + "- **Flights:** Search for trips by month or specific dates.\n"
                                + "- **Local News:** Retrieve destination-based news with sentiment scores (1-5).\n"
                                + "- **Places to Travel:** Discover places based on coordinates, count, and category.\n\n"

                                + "## Getting Started with CheapTrips\n\n"
                                + "Explore the world of CheapTrips and simplify your travel experience with the following steps:\n"

                                + "### 1. Authentication (JWT)\n"
                                + "- **Generate Token and Authorize:**\n"
                                + "- For **New** User Access the `/user` endpoint to sign in and obtain a JWT token.\n"
                                + "- For **Existing** User Access the `/authentication` endpoint to obtain a JWT token.\n"
                                + "- Click 'Authorize' in the top left corner, and insert the token in the format: `<Bearer YOUR_TOKEN>`.\n"
                                + "- \uD83D\uDD10 For more details, check [Obtain / Authorize Authentication Token](https://github.com/OrenGolanProjects/JAVACheapTrips#step-1-obtain-authentication-token).\n\n"

                                + "### 2. User Registration (One-Time Setup)\n"
                                + "- **Register as a New User:**\n"
                                + "  - Use the `/app/userinfo/create-specific-user-info` endpoint to register as a new user.\n"
                                + "  - Insert the user information: username, first name, surname, phone.\n"
                                + "  - \uD83D\uDCDD For more details, see [Create New User Information (If New User)](https://github.com/OrenGolanProjects/JAVACheapTrips#step-3-create-new-user-information-if-new-user).\n\n"

                                + "### 3. Trip Search on CheapTripsApp\n"
                                + "- **Search for Trips:**\n"
                                + "  - Choose either `/app/cheap-trip/generate-monthly-trip` or `/app/cheap-trip/generate-trip-by-dates` endpoints.\n"
                                + "  - Provide necessary details in the request body for trip generation.\n"
                                + "  - For city IATA code & details, use the `/cheap-trip/city-search` endpoint.\n"
                                + "  - \uD83C\uDF0D For more details, refer to [Usage Examples](https://github.com/OrenGolanProjects/JAVACheapTrips#usage).\n\n\n"

                                + "## Coming Soon\n\n"
                                + "Exciting enhancements are on the horizon:\n\n"
                                + "- Real-time Weather: Stay updated on destination weather.\n"
                                + "- Holiday Insights: Discover details about upcoming holidays.\n\n\n"

                                + "## Code Repository and More\n"
                                + "Explore examples, review code, and access the README.md on GitHub:\n\n"
                                + "- \uD83D\uDCC2 **GitHub Repository**: [https://github.com/OrenGolanProjects/JAVACheapTrips#readme/](https://github.com/OrenGolanProjects/JAVACheapTrips#readme/)\n"
                                + "- Designed & Developed by: Oren Golan \uD83D\uDEE0\uFE0F."
                )
                .version("1.0")
                .build();
    }
    /**
     * Configures the API key used for authentication.
     *
     * @return ApiKey instance for API key authentication.
     */
    private ApiKey apiKey() {
        return new ApiKey("Authorization", AUTHORIZATION_HEADER, "header");
    }

    /**
     * Configures the security context for Swagger documentation.
     *
     * @return SecurityContext instance for Swagger documentation.
     */
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(regex(DEFAULT_INCLUDE_PATTERN))
                .build();
    }

    /**
     * Defines default security references.
     *
     * @return List of SecurityReference instances.
     */
    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(
                new SecurityReference("Authorization", authorizationScopes));
    }
}