package com.checkpointBlog.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.checkpointBlog.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    RequestFilter requestFilter;

    @Bean
    UserService userDetailsService() {
        return new UserService();
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(requests -> requests
        	    .requestMatchers(HttpMethod.POST, "/login", "/register").permitAll()
        	    .requestMatchers(HttpMethod.GET, 
        	        "/article", 
        	        "/article/search/**", 
        	        "/article/category/**", 
        	        "/article/sorted/**",
        	        "/user",
        	        "/user/**",
        	        "/images/**",
        	        "/api/**",
        	        "/category",
        	        "/category/**",
        	        "/swagger-ui/**",
        	        "/v3/api-docs/**").permitAll()
        	    .requestMatchers("/article/**", "/likes/**", "/article/*/report", "/article/drafts").authenticated()
                .requestMatchers(HttpMethod.POST, "/article", "/api/images/upload/**").hasAnyAuthority("ADMIN", "EDITOR")
                .requestMatchers(HttpMethod.PUT, "/article/**").hasAnyAuthority("ADMIN", "EDITOR")
                .requestMatchers(HttpMethod.DELETE, "/article/**", "/api/images/**").hasAnyAuthority("ADMIN", "EDITOR")
                .requestMatchers("/article/*/unreport", "/article/reported").hasRole("ADMIN")
                .anyRequest().denyAll()
            )
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
        		"https://checkpoint-blog-psi.vercel.app",
        		"https://api-2425-dluqueru-javaclean.onrender.com",
        		"http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}