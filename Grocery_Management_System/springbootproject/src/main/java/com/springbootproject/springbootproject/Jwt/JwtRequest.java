package com.springbootproject.springbootproject.Jwt;

import lombok.Data;

@Data
public class JwtRequest {

    // Username provided in the authentication request
    private String username;
    
    // Password provided in the authentication request
    private String password;
}
