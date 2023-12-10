package com.orengolan.cheaptrips.config;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static springfox.documentation.builders.PathSelectors.regex;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String DEFAULT_INCLUDE_PATTERN = "/.*";

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

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("CheapTrips API Documentation")
                .description(
                        "###**About**\n\n"
                        + "**CheapTrips is designed to simplify trip planning, offering:**\n"
                        + "- **Flights:** Search for trips by month or specific dates.\n"
                        + "- **Local News:** Retrieve destination-based news with sentiment scores (1-5).\n"
                        + "- **Places to Travel:** Discover places based on coordinates, count, and category.\n\n"

                        + "###**How to Use - New User**\n\n"
                        + "1. **Generate Token and Authorize (JWT Authentication):**\n"
                        + "     - Use the /user endpoint to sign in and obtain a JWT token.\n"
                        + "     - Click on 'Authorize' in the top left corner of the screen.\n"
                        + "     - Use the token that received insert format: <Bearer YOUR_TOKEN> and click Authorize.\n\n"


                        + "2. **Register as a New User (User Management):**\n"
                        + "     - Use the /app/userinfo/create-specific-user-info endpoint to register as a new user.\n"
                        + "     - Insert the user information: username, first name, surname, phone.\n\n"

                        + "3. **Search for Trip at (CheapTripsApp):**\n"
                        + "     - Choose either the /app/cheap-trip/generate-monthly-trip or /app/cheap-trip/generate-trip-by-dates endpoints.\n"
                        + "     - Provide the necessary details in the request body for generating trips.\n\n"

                        + "###**Coming Soon:**\n\n"
                        + "Enhancements to elevate your experience:\n"
                        + "- Real-time Weather: Instant updates on destination weather.\n"
                        + "- Holiday Insights: Details about upcoming holidays. Stay tuned for an enriched travel experience with CheapTrips!\n\n"

                        + "###For code review,examples and README.md file, visit my GitHub repository:\n"
                        + "\uD83D\uDD17 **GitHub Repository**: [https://github.com/OrenGolanProjects/JAVACheapTrips#readme/](https://github.com/OrenGolanProjects/JAVACheapTrips#readme/)\n"
                        + "###Designed & Developed by: Oren Golan."
                )
                .version("1.0")
                .build();
    }
    private ApiKey apiKey() {
        return new ApiKey("Authorization", AUTHORIZATION_HEADER, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(regex(DEFAULT_INCLUDE_PATTERN))
                .build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(
                new SecurityReference("Authorization", authorizationScopes));
    }
}