package com.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import com.blog.security.CustomUserDetailService;
import com.blog.security.JwtAuthenticationEntryPoint;
import com.blog.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    public static final String[] PUBLIC_URLS = {
            "/api/v1/auth/**",  // Authentication end points
            "/v3/api-docs",   // OpenAPI/Swagger documentation end point
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/webjars/**"
          };
    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Configure HTTP security
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .disable())  // Disable CSRF protection (for stateless authentication)
                .authorizeHttpRequests()
                    .requestMatchers(PUBLIC_URLS).permitAll()  // Allow public URLs without authentication
                    .requestMatchers(HttpMethod.GET, "/v3/api-docs").permitAll()  // Allow GET to /v3/api-docs
                    .requestMatchers(HttpMethod.GET).permitAll()  // Allow all GET requests without authentication (optional)
                    .anyRequest().authenticated()  // Require authentication for all other requests
                .and()
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(this.jwtAuthenticationEntryPoint))  // Set up authentication entry point for errors
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));  // Use stateless sessions (JWT)
        
        // Add JWT filter before the default user name/password authentication filter
        http.addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // Define AuthenticationManager bean
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailService)  // Custom user details service
                                    .passwordEncoder(passwordEncoder());  // Use BCrypt password encoder       
        return authenticationManagerBuilder.build();  // Return the AuthenticationManager
    }

    // Define PasswordEncoder bean to hash passwords (BCrypt in this case)
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Create and return BCrypt password encoder
    }
}
