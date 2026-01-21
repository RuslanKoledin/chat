package com.company.messenger.service;

import com.company.messenger.dto.AuthRequest;
import com.company.messenger.dto.AuthResponse;
import com.company.messenger.dto.UserDto;
import com.company.messenger.entity.User;
import com.company.messenger.repository.UserRepository;
import com.company.messenger.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = jwtUtil.generateToken(request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        return new AuthResponse(token, UserDto.fromEntity(user));
    }

    public UserDto register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        if (user.getRole() == null) {
            user.setRole(User.UserRole.EMPLOYEE);
        }

        User savedUser = userRepository.save(user);
        return UserDto.fromEntity(savedUser);
    }
}
