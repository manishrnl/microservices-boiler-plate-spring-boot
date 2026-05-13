package com.boilerplate.platform.security.controller;

import com.boilerplate.platform.security.dto.AuthResponse;
import com.boilerplate.platform.security.dto.LoginRequest;
import com.boilerplate.platform.security.dto.SessionResponse;
import com.boilerplate.platform.security.dto.SignupRequest;
import com.boilerplate.platform.security.service.AuthService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("service", "security-services", "status", "UP");
    }

    @PostMapping("/signup")
    public AuthResponse signup(@Valid @RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/sessions")
    public List<SessionResponse> listSessions(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return authService.listSessions(extractBearerToken(authorization));
    }

    @DeleteMapping("/sessions/current")
    public void logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        authService.logout(extractBearerToken(authorization));
    }

    private String extractBearerToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header must use Bearer token");
        }
        return authorization.substring(7);
    }
}
