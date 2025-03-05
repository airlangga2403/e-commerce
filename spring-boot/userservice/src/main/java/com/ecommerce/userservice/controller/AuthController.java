package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.constant.Authorities;
import com.ecommerce.userservice.constant.ResourceURL;
import com.ecommerce.userservice.dto.request.LoginRequest;
import com.ecommerce.userservice.dto.request.RegisterRequest;
import com.ecommerce.userservice.dto.response.BaseResponse;
import com.ecommerce.userservice.model.Role;
import com.ecommerce.userservice.repository.RoleRepository;
import com.ecommerce.userservice.service.UsersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping(ResourceURL.BASE_URL)
public class AuthController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private RoleRepository roleRepository;


    @PostMapping(value = ResourceURL.LOGIN)
    public ResponseEntity<BaseResponse> login(@Valid @RequestBody LoginRequest request)
            throws NoSuchAlgorithmException {
        return usersService.login(request);
    }

    @PostMapping(value = ResourceURL.REGISTER)
    public ResponseEntity<BaseResponse> register(@Valid @RequestBody RegisterRequest request) throws NoSuchAlgorithmException {
        return usersService.register(request);
    }

    // For seeder
    @GetMapping("/seed")
    public void seedRoles() {
        for (Authorities authority : Authorities.values()) {
            Role role = new Role();
            role.setRole(authority.getAuthority());
            role.setPermission(String.join(",", authority.getPermissions()));
            roleRepository.save(role);
        }
    }

}
