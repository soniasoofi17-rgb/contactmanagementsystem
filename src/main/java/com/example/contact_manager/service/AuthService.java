package com.example.contact_manager.service;

import com.example.contact_manager.dto.AuthRequest;
import com.example.contact_manager.entity.User;
import com.example.contact_manager.repository.UserRepository;
import com.example.contact_manager.security.JwtTokenUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public void register(User user, String plainPassword) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        user.setPassword(encoder.encode(plainPassword));
        userRepository.save(user);
    }

    public String login(AuthRequest req) {
        var maybe = userRepository.findByEmail(req.getUsername());
        if (maybe.isEmpty()) throw new IllegalArgumentException("Invalid credentials");
        User u = maybe.get();
        if (!encoder.matches(req.getPassword(), u.getPassword())) throw new IllegalArgumentException("Invalid credentials");
        return jwtTokenUtil.generateToken(u.getEmail());
    }
}
