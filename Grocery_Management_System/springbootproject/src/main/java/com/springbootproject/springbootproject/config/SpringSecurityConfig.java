
package com.springbootproject.springbootproject.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SpringSecurityConfig {

    // Define a bean for password encoder to hash passwords
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // Define a bean for user details service to provide user information for authentication
    @Bean
    public UserDetailsService userDetailService(){
        // Create UserDetails for admin user
        UserDetails adminUser = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("1234")) // Encode password using the password encoder
            .roles("ADMIN") // Has admin role for role-based authentication
            .build();

        // Create UserDetails for normal user
        UserDetails normalUser = User.builder()
            .username("ifrah")
            .password(passwordEncoder().encode("5678")) // Encode password using the password encoder
            .roles("USER") // Has user role for role-based authentication
            .build();

        // Return an InMemoryUserDetailsManager containing the admin and normal users
        return new InMemoryUserDetailsManager(adminUser, normalUser);
    }

    // Define a bean for authentication manager to authenticate users
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }    
}
