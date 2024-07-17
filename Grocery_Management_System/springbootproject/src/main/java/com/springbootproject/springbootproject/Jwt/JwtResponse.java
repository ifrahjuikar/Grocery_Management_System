package com.springbootproject.springbootproject.Jwt;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {

    //Status of token generated
    private String status;


    // Username associated with the JWT token
    private String username;
    
    // JWT token generated for the user
    private String jwtToken;
   
    // Constructor to initialize username and JWT token
    public JwtResponse(String status,String username, String jwtToken) {
        this.status=status;
        this.username = username;
        this.jwtToken = jwtToken;
    }
}
