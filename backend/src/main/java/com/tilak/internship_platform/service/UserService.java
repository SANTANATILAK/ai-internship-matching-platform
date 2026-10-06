package com.tilak.internship_platform.service;

import com.tilak.internship_platform.dto.request.ProfileUpdateRequest;
import com.tilak.internship_platform.dto.request.RegisterRequest;
import com.tilak.internship_platform.dto.response.UserProfileResponse;
import com.tilak.internship_platform.entity.Resume;
import com.tilak.internship_platform.entity.Role;
import com.tilak.internship_platform.entity.User;
import com.tilak.internship_platform.exception.BadRequestException;
import com.tilak.internship_platform.exception.ResourceNotFoundException;
import com.tilak.internship_platform.repository.ResumeRepository;
import com.tilak.internship_platform.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       ResumeRepository resumeRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new BadRequestException("An account with email " + request.getEmail() + " already exists");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .branch(request.getBranch())
                .college(request.getCollege())
                .graduationYear(request.getGraduationYear())
                .location(request.getLocation())
                .preferredLocation(request.getPreferredLocation())
                .preferredWorkType(request.getPreferredWorkType())
                .rolePreference(request.getRolePreference())
                .role(Role.STUDENT)
                .enabled(true)
                .build();

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        User user = getUserById(userId);
        Optional<Resume> latestResume = resumeRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);

        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .branch(user.getBranch())
                .college(user.getCollege())
                .graduationYear(user.getGraduationYear())
                .location(user.getLocation())
                .preferredLocation(user.getPreferredLocation())
                .preferredWorkType(user.getPreferredWorkType())
                .rolePreference(user.getRolePreference())
                .role(user.getRole())
                .hasResume(latestResume.isPresent())
                .latestAtsScore(latestResume.map(Resume::getAtsScore).orElse(null))
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = getUserById(userId);

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName().trim());
        }
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getBranch() != null) user.setBranch(request.getBranch());
        if (request.getCollege() != null) user.setCollege(request.getCollege());
        if (request.getGraduationYear() != null) user.setGraduationYear(request.getGraduationYear());
        if (request.getLocation() != null) user.setLocation(request.getLocation());
        if (request.getPreferredLocation() != null) user.setPreferredLocation(request.getPreferredLocation());
        if (request.getPreferredWorkType() != null) user.setPreferredWorkType(request.getPreferredWorkType());
        if (request.getRolePreference() != null) user.setRolePreference(request.getRolePreference());

        userRepository.save(user);
        return getUserProfile(userId);
    }
}
