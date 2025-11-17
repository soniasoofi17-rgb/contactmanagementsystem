package com.example.contact_manager.controller;

import com.example.contact_manager.dto.AuthRequest;
import com.example.contact_manager.dto.AuthResponse;
import com.example.contact_manager.entity.User;
import com.example.contact_manager.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User u) {
        try {
            String plain = u.getPassword();
            if (plain == null || plain.isBlank()) return ResponseEntity.badRequest().body("Password required");
            // clear plain password from entity before saving (AuthService will hash and save)
            u.setPassword(null);
            authService.register(u, plain);
            log.info("Registered user {}", u.getEmail());
            return ResponseEntity.ok("Registered");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            log.error("Register error", e);
            return ResponseEntity.status(500).body("Server error");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            String token = authService.login(req);
            log.info("Login success for {}", req.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(401).body(ex.getMessage());
        } catch (Exception e) {
            log.error("Login error", e);
            return ResponseEntity.status(500).body("Server error");
        }
    }
}
