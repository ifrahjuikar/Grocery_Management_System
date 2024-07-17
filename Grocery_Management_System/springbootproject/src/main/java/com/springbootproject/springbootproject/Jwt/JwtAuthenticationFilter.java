package com.springbootproject.springbootproject.Jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Logger for logging messages
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // Autowired components for JWT handling and user details service
    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    // Method to filter each incoming request
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String username = null;
        // Extract token from request header
        String token = request.getHeader("Authorization");

        // String requestHeader = request.getHeader("Authorization");
        // if (requestHeader != null && requestHeader.startsWith("Bearer")) {
        // Extract token from Authorization header
        // token = requestHeader.substring(7);

        // Check if token is present in the request header
        if (token != null) {
            try {
                // Extract username from token
                username = this.jwtHelper.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                // Log error if an illegal argument is encountered while fetching the username
                logger.error("Illegal Argument while fetching the username !!");
            } catch (ExpiredJwtException e) {
                // Log error if the token is expired
                logger.error("Given jwt token is expired !!");
            } catch (MalformedJwtException e) {
                // Log error if the token is malformed
                logger.error("Some changes have been made in the token !! Invalid Token");
            } catch (Exception e) {
                // Log error for any other exceptions
                logger.error("Internal Server Problem");
            }
        } else {
            // Log info if no token is found in the request header
            logger.error("Invalid Header Value !! ");
        }

        // Check if username is not null and no authentication has been set
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Fetch user details from username
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            // Validate token
            Boolean validateToken = this.jwtHelper.validateToken(token, userDetails);
            if (validateToken) {
                // Set the authentication if the token is valid
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // Log info if token validation fails
                logger.error("Validation fails !!");
            }
        }
        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
