package com.tilak.internship_platform.controller;

import com.tilak.internship_platform.dto.request.LoginRequest;
import com.tilak.internship_platform.dto.request.RegisterRequest;
import com.tilak.internship_platform.dto.response.ApiResponse;
import com.tilak.internship_platform.dto.response.AuthResponse;
import com.tilak.internship_platform.entity.User;
import com.tilak.internship_platform.security.JwtTokenProvider;
import com.tilak.internship_platform.security.UserPrincipal;
import com.tilak.internship_platform.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider tokenProvider,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.registerUser(request);

        // Auto authenticate after registration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().trim().toLowerCase(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        AuthResponse authResponse = AuthResponse.builder()
                .token(jwt)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        return ResponseEntity.ok(ApiResponse.success("User registered successfully", authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().trim().toLowerCase(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        AuthResponse authResponse = AuthResponse.builder()
                .token(jwt)
                .userId(userPrincipal.getId())
                .name(userPrincipal.getName())
                .email(userPrincipal.getUsername())
                .role(userPrincipal.getRole())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }

        AuthResponse res = AuthResponse.builder()
                .userId(userPrincipal.getId())
                .name(userPrincipal.getName())
                .email(userPrincipal.getUsername())
                .role(userPrincipal.getRole())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Authenticated user details", res));
    }
}
