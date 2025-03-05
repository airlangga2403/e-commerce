package com.ecommerce.userservice.dto.response;


import lombok.Data;

@Data
public class LoginResponse {

    private String accessToken;
    private String role;

}
