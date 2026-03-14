package com.dataanalyst.controller;

import com.dataanalyst.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest req) {
        Map<String, Object> result = authService.register(req.getName(), req.getEmail(), req.getPassword());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest req) {
        Map<String, Object> result = authService.login(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(result);
    }

    // ── DTOs ──────────────────────────────────────────────────────────────────

    public static class RegisterRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @Email(message = "Valid email is required")
        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        public RegisterRequest() {}

        public String getName()           { return name; }
        public void setName(String name)  { this.name = name; }
        public String getEmail()          { return email; }
        public void setEmail(String email){ this.email = email; }
        public String getPassword()       { return password; }
        public void setPassword(String p) { this.password = p; }
    }

    public static class LoginRequest {
        @Email(message = "Valid email is required")
        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

        public LoginRequest() {}

        public String getEmail()          { return email; }
        public void setEmail(String email){ this.email = email; }
        public String getPassword()       { return password; }
        public void setPassword(String p) { this.password = p; }
    }
}
