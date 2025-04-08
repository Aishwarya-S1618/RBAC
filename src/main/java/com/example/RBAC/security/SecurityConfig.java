package com.example.RBAC.security;

import com.example.RBAC.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    public SecurityConfig(UserDetailsService userDetailsService, JwtUtil jwtUtil, TokenService tokenService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        System.out.println("Creating JWT Authentication Filter");
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService,tokenService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/secure-endpoint").hasRole("USER")
                .requestMatchers("/dashboard").permitAll()
                .requestMatchers("/dashboard/user").hasRole("USER")
                .requestMatchers("/dashboard/admin").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // Add JWT filter before UsernamePasswordAuthenticationFilter


        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
