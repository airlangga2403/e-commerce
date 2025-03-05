package com.ecommerce.productservice.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {

    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String role;

    private OffsetDateTime deletedAt;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}
