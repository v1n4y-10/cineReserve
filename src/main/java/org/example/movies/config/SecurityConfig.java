package org.example.movies.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.http.HttpMethod;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )


                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // Public registration and login
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/users",
                                "/api/users/login"
                        ).permitAll()

                        // User profile updates require authentication
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/users/**"
                        ).authenticated()

                        // User deletion is ADMIN only
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/users/**"
                        ).hasRole("ADMIN")

                        // Public browsing
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/movies/**",
                                "/api/theater/**",
                                "/api/screens/**",
                                "/api/shows/**",
                                "/api/seats/**",
                                "/api/show-seats/**"
                        ).permitAll()

                        // Only ADMIN can modify movie-related data
                        .requestMatchers(
                                "/api/movies/**",
                                "/api/theater/**",
                                "/api/screens/**",
                                "/api/shows/**",
                                "/api/seats/**",
                                "/api/show-seats/**",
                                "/api/admin/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users/**"
                        ).hasRole("ADMIN")

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception

                        // User is not authenticated
                        .authenticationEntryPoint((request, response, authException) -> {

                            response.setStatus(401);
                            response.setContentType("application/json");

                            response.getWriter().write(
                                    "{\"error\":\"Authentication required or token is invalid\"}"
                            );
                        })

                        // User is authenticated but lacks permission
                        .accessDeniedHandler((request, response, accessDeniedException) -> {

                            response.setStatus(403);
                            response.setContentType("application/json");

                            response.getWriter().write(
                                    "{\"error\":\"You do not have permission to access this resource\"}"
                            );
                        })
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
