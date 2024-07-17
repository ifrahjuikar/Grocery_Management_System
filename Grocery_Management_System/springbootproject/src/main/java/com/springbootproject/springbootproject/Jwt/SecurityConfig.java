package com.springbootproject.springbootproject.Jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    // Autowiring JwtAuthenticationEntryPoint handles authentication failure
    @Autowired
    private JwtAuthenticationEntryPoint point;
    
    // Autowiring JwtAuthenticationFilter perform jwt authentication
    @Autowired
    private JwtAuthenticationFilter filter;

    // Define SecurityFilterChain bean for security setting
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configure HttpSecurity
        http.csrf(csrf -> csrf.disable()) // Disable CSRF protection
            .authorizeRequests(requests -> requests
                .antMatchers("/authenticate").permitAll() // Allow access to "/authenticate" endpoint without authentication to all
                .anyRequest().authenticated()) // Require authentication for any other endpoint
            .exceptionHandling(ex -> ex.authenticationEntryPoint(point)) // Set custom authentication entry point to handle error renponse
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Set session management to STATELESS
        
        // Add JwtAuthenticationFilter-it perform first before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        
        // Build and return SecurityFilterChain- security setting
        return http.build();
    }
}















