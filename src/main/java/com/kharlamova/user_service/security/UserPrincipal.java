package com.kharlamova.user_service.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPrincipal {
    private Long userId;

    private String role;

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
