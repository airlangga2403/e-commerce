package com.ecommerce.userservice.service;

import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.repository.UserRepository;
import com.ecommerce.userservice.security.JwtService;
import com.ecommerce.userservice.security.UserInfoDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UsersService implements UserDetailsService {

    @Autowired
    private JwtService jwtService;

    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()){
            return user.map(UserInfoDetails::new)
                    .orElseThrow(() -> new UsernameNotFoundException("Email " + email + " not found"));
        }

        throw new UsernameNotFoundException("User not found");
    }

    // TODO
//    1. AUTH LOGIN
//    2 AUTH REGISTER
//    public ResponseEntity<BaseResponse> login(LoginRequest loginRequest) throws NoSuchAlgorithmException {
//        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());
//        if (user.isPresent()) {
//            Boolean checkPassword = PasswordHash.checkPin(loginRequest.getPassword(), user.get().getPassword());
//            if (checkPassword) {
//                LoginResponse loginResponse = new LoginResponse();
//                loginResponse.setAccessToken(jwtService.generateToken(user.get().getId(), user.get().getEmail()));
//                loginResponse.setRole(user.get().getRole());
//
//                return Response.success(loginResponse);
//            } else {
//                return Response.Unauthorized("Email or password is incorrect. Please try again.");
//            }
//        }
//        return GenericMessageResponse.failedResponse("Data Not Found");
//    }

}
