package com.orengolan.cheaptrips.config;

import com.orengolan.cheaptrips.jwt.JwtAuthenticationEntryPoint;
import com.orengolan.cheaptrips.jwt.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * The {@code WebSecurityConfig} class is a Spring configuration class responsible for
 * configuring security settings for the CheapTrips web application. It extends
 * {@link org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter}
 * to provide custom security configurations.
 *
 * The class includes settings for JWT-based authentication and authorization using
 * Spring Security. It configures authentication, password encoding, and authorization rules
 * for different API endpoints.
 *
 * Security-related beans and filters, such as {@link JwtAuthenticationEntryPoint},
 * {@link JwtRequestFilter}, and {@link PasswordEncoder}, are configured within this class.
 *
 * Key Configurations:
 * - {@code configureGlobal}: Configures the global {@code AuthenticationManager} with user
 *   details service and password encoder.
 * - {@code passwordEncoder}: Defines the password encoder (BCryptPasswordEncoder) for secure
 *   password storage.
 * - {@code authenticationManagerBean}: Exposes the authentication manager bean.
 * - {@code configure}: Configures HTTP security settings, including authentication exceptions,
 *   authorization rules, and session management. It also adds a filter to validate JWT tokens
 *   with every request.
 *
 * The class allows public access to certain endpoints such as authentication, Actuator endpoints,
 * and Swagger UI documentation. It denies access to other API endpoints by default, ensuring that
 * users must be authenticated to access protected resources.
 *
 * Example:
 * - An unauthenticated user can access /authenticate, /user, Actuator endpoints, and Swagger UI.
 * - Access to other API endpoints (/api/**) is denied without proper authentication.
 * - JWT tokens are validated with every request using the {@code JwtRequestFilter}.
 *
 * Note: This class is critical for securing the application and controlling access to various parts
 * of the API based on user authentication and authorization.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private UserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    /**
     * Configures global authentication manager with user details service and password encoder.
     *
     * @param auth AuthenticationManagerBuilder instance.
     * @throws Exception If an error occurs during configuration.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // Use BCryptPasswordEncoder
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * Defines the password encoder (BCryptPasswordEncoder) for secure password storage.
     *
     * @return PasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the authentication manager bean.
     *
     * @return AuthenticationManager bean.
     * @throws Exception If an error occurs during bean creation.
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Configures HTTP security settings, including authentication exceptions, authorization rules,
     * and session management. It also adds a filter to validate JWT tokens with every request.
     *
     * @param httpSecurity HttpSecurity instance.
     * @throws Exception If an error occurs during configuration.
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .ignoringAntMatchers("/authenticate", "/api/**","/user")  // Disable CSRF for API and authentication
                .and()
                .authorizeRequests()
                .antMatchers(
                        "/", "/login", "/signup", "/welcome",
                        "/style/**", "/js/**", "/images/**", "/css/**", "/fonts/**",
                        "/fragments/**", "/footer", "/header", "/loadingOverlay", "/modal",
                        "/base", "/index",
                        "/pages/**",
                        "/actuator/**",
                        "/swagger-ui.html",
                        "/api/swagger-ui.html",
                        "/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/webjars/**"
                ).permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Add a filter to validate the tokens with every request
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}