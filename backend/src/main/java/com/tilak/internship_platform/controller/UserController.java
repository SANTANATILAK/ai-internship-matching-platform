package com.tilak.internship_platform.controller;

import com.tilak.internship_platform.dto.request.ProfileUpdateRequest;
import com.tilak.internship_platform.dto.response.ApiResponse;
import com.tilak.internship_platform.dto.response.UserProfileResponse;
import com.tilak.internship_platform.security.UserPrincipal;
import com.tilak.internship_platform.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserProfileResponse profile = userService.getUserProfile(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                         @RequestBody ProfileUpdateRequest request) {
        UserProfileResponse updated = userService.updateProfile(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }
}
