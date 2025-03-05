package com.ecommerce.userservice.security;


import com.ecommerce.userservice.constant.ResourceURL;
import com.ecommerce.userservice.dto.response.BaseResponse;
import com.ecommerce.userservice.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
@Slf4j
public class SecurityConfig {
//
    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public UserDetailsService userDetailService(){
        return new UsersService();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests()
                .requestMatchers(
                        ResourceURL.BASE_URL + ResourceURL.LOGIN,
                        ResourceURL.BASE_URL + ResourceURL.REGISTER
                )
                .permitAll()
                .and()
//                ENDPOINT AUTHORITY ACCESS TODO
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider()).exceptionHandling(exceptionHandler -> exceptionHandler
                        .accessDeniedHandler((request,response,accessDeniedException) ->
                                        authenticationErrorHandler(response,accessDeniedException, HttpStatus.FORBIDDEN)
                                )
                        .authenticationEntryPoint((request,response,authenticationException) ->
                                        authenticationErrorHandler(response,authenticationException, HttpStatus.UNAUTHORIZED))
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    private void authenticationErrorHandler(HttpServletResponse response, Exception exception, HttpStatus status) throws IOException {
        BaseResponse errorResponse = BaseResponse.builder()
                .status(status.value())
                .message(exception.getMessage())
                .build();

        log.error("Authentication Error: {}", errorResponse);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", configuration);
        return urlBasedCorsConfigurationSource;
    }

}
