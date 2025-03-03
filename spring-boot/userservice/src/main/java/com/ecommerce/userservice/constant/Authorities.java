package com.ecommerce.userservice.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Authorities {

    // TODO AUTHORITIES
    // PUBLIC -> can view products
    // BUYER -> can buy products
    // SELLER -> can sell products
    // ADMIN -> can do anything


    PUBLIC("PUBLIC",
            Arrays.asList("VIEW_PRODUCT")),
    BUYER("BUYER",
            Arrays.asList("VIEW_PRODUCT", "BUY_PRODUCT")),
    SELLER("SELLER",
            Arrays.asList("VIEW_PRODUCT", "SELL_PRODUCT", "BUY_PRODUCT")),
    ADMIN("ADMIN",
            Arrays.asList("BUY_PRODUCT", "SELL_PRODUCT"));


    private final String authority;
    private final List<String> permissions;

    Authorities(String authority, List<String> permissions) {
        this.authority = authority;
        this.permissions = permissions;
    }

    public String getAuthority() {
        return authority;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public static List<String> getAuthoritiesAndPermissionByRole(String role) {
        try {
            Authorities authorities = Authorities.valueOf(role.toUpperCase().replace(" ", "_"));
            return Stream.concat(
                    Stream.of(authorities.getAuthority()),
                    authorities.getPermissions().stream()
            ).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return Collections.singletonList("DEFAULT");
        }
    }

}
