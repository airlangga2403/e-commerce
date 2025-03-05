package com.ecommerce.userservice.service;

import Utils.PasswordHash;
import com.ecommerce.userservice.constant.Authorities;
import com.ecommerce.userservice.dto.request.LoginRequest;
import com.ecommerce.userservice.dto.request.RegisterRequest;
import com.ecommerce.userservice.dto.response.BaseResponse;
import com.ecommerce.userservice.dto.response.GenericMessageResponse;
import com.ecommerce.userservice.dto.response.LoginResponse;
import com.ecommerce.userservice.model.Role;
import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.repository.RoleRepository;
import com.ecommerce.userservice.repository.UserRepository;
import com.ecommerce.userservice.security.JwtService;
import com.ecommerce.userservice.security.UserInfoDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
@Slf4j
public class UsersService implements UserDetailsService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading user by email: {}", email);
        return userRepository.findByEmail(email)
                .map(UserInfoDetails::new)
                .orElseThrow(() -> {
                    log.error("Email {} not found", email);
                    return new UsernameNotFoundException("Email " + email + " not found");
                });
    }

//    TODO DISINI NANTI BUAT REGISTER UNTUK BEBERAPA ROLE
//    secara default yang di create maka akan role BUYER


    public ResponseEntity<BaseResponse> register(RegisterRequest registerRequest) throws NoSuchAlgorithmException {
        log.info("Register attempt for email: {}", registerRequest.getEmail());
        Optional<User> userOptional = userRepository.findByEmail(registerRequest.getEmail());
        if (userOptional.isPresent()) {
            log.warn("Email {} already registered", registerRequest.getEmail());
            return ResponseEntity.badRequest().body(
                    GenericMessageResponse.failedResponse("Email already registered")
            );
        }
        String hashedPassword = PasswordHash.hashPin(registerRequest.getPassword());

        String roleToLookup = (registerRequest.getRole() != null && !registerRequest.getRole().trim().isEmpty())
                ? registerRequest.getRole()
                : Authorities.BUYER.getAuthority();

        Optional<Role> roleOptional = roleRepository.findByRole(roleToLookup);
        if (!roleOptional.isPresent()) {
            log.warn("Role {} not found, using default role BUYER", roleToLookup);
            roleOptional = roleRepository.findByRole(Authorities.BUYER.getAuthority());
            if (!roleOptional.isPresent()) {
                return ResponseEntity.badRequest().body(
                        GenericMessageResponse.failedResponse("Default role not found")
                );
            }
        }
        Role role = roleOptional.get();

        User newUser = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(hashedPassword)
                .role(role.getRole())
                .build();

        userRepository.save(newUser);
        log.info("User registered successfully: {}", newUser.getEmail());
        return ResponseEntity.ok(GenericMessageResponse.successfulResponse("User registered successfully", null));
    }

    public ResponseEntity<BaseResponse> login(LoginRequest loginRequest) throws NoSuchAlgorithmException {
        log.info("Login attempt for email: {}", loginRequest.getEmail());
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            boolean isPasswordValid = PasswordHash.checkPin(loginRequest.getPassword(), user.getPassword());
            if (isPasswordValid) {
                LoginResponse loginResponse = new LoginResponse();
                loginResponse.setAccessToken(jwtService.generateToken(user));
                loginResponse.setRole(user.getRole());
                log.info("Login successful for user: {}", user.getEmail());
                return ResponseEntity.ok(
                        GenericMessageResponse.successfulResponse("Login successful", loginResponse)
                );
            } else {
                log.warn("Invalid password for email: {}", loginRequest.getEmail());
                return ResponseEntity.badRequest().body(
                        GenericMessageResponse.failedResponse("Email or password is incorrect. Please try again.")
                );
            }
        }
        log.warn("User not found for email: {}", loginRequest.getEmail());
        return ResponseEntity.badRequest().body(
                GenericMessageResponse.failedResponse("Data Not Found")
        );
    }
}
