package dev.minechase.core.rest.model.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public static class RegisterRequest {

        @NotBlank
        public String username, email, password;

    }

    public static class LoginRequest {
        @NotBlank
        public String email;
        @NotBlank
        public String password;
    }

    public static class MeRequest {
        @NotBlank
        public String jwt;

    }

}
