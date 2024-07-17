package com.springbootproject.springbootproject.Jwt;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // Method called when authentication fails
     @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        // Set HTTP status code to 401 (Unauthorized)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // Set content type to application/json
        response.setContentType("application/json");
        // Create a response body as a map
        var responseBody = new java.util.HashMap<String, String>();
        responseBody.put("status", "Error");
        responseBody.put("statusCode", "" + HttpStatus.UNAUTHORIZED.value());
        responseBody.put("statusMessage", "Access Denied !! " + authException.getMessage());
        
        // responseEntity=new ResponseEntity<>(new ApiResponse("Error",HttpStatus.UNAUTHORIZED.value(),"Access Denied !! " + authException.getMessage() , null), HttpStatus.UNAUTHORIZED);
        
        // Write the JSON response to the response body
        PrintWriter writer = response.getWriter();
        new ObjectMapper().writeValue(writer, responseBody);
        writer.flush();
    }
}
