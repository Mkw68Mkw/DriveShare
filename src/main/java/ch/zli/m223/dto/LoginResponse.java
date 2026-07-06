package ch.zli.m223.dto;

import ch.zli.m223.entity.enums.UserRole;

public class LoginResponse {

    public String message;
    public Long userId;
    public UserRole role;

    public LoginResponse(String message, Long userId, UserRole role) {
        this.message = message;
        this.userId = userId;
        this.role = role;
    }
}
