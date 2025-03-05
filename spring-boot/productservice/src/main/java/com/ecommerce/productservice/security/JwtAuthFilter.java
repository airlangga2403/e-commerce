package com.ecommerce.productservice.security;

import com.ecommerce.productservice.dto.response.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                Claims claims = jwtService.extractAllClaims(token);

                if (claims.getExpiration().before(new Date())) {
                    sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token Expired");
                    return;
                }

                String username = claims.getSubject();
                List<String> roles = claims.get("roles", List.class);

                UserDetails userDetails = User.builder()
                        .username(username)
                        .password("")
                        .authorities(roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .toList())
                        .build();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid JWT token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus httpStatus, String message) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        BaseResponse errorResponse = BaseResponse.builder()
                .status(httpStatus.value())
                .message(message)
                .build();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.value());
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
