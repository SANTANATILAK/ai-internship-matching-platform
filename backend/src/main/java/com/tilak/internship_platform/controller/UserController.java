package com.tilak.internship_platform.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.security.Principal;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tilak.internship_platform.entity.User;
import com.tilak.internship_platform.repository.UserRepository;
import com.tilak.internship_platform.security.JwtService;

@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "http://localhost:5175",
        "http://127.0.0.1:5173",
        "http://127.0.0.1:5174",
        "http://127.0.0.1:5175"
})
@RestController
@RequestMapping({"/api/users", "/api/auth"})
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final com.tilak.internship_platform.repository.ResumeRepository resumeRepository;

    public UserController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this(userRepository, passwordEncoder, jwtService, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public UserController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            com.tilak.internship_platform.repository.ResumeRepository resumeRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.resumeRepository = resumeRepository;
    }

    @jakarta.annotation.PostConstruct
    public void initAdmin() {
        if (userRepository.findByEmail("admin@internmatch.com").isEmpty()) {
            User admin = new User();
            admin.setName("Platform Administrator");
            admin.setEmail("admin@internmatch.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole("ADMIN");
            admin.setBranch("Computer Science");
            admin.setGraduationYear(2025);
            admin.setCollege("Admin Console");
            userRepository.save(admin);
        }
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Email is required"
            );
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Password must be at least 6 characters"
            );
        }

        String normalizedEmail = user.getEmail().trim().toLowerCase();
        Optional<User> existingUser = userRepository.findByEmail(normalizedEmail);

        if (existingUser.isPresent()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONFLICT,
                    "An account with this email already exists. Please sign in instead."
            );
        }

        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("STUDENT");
        }

        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole()
        );

        Map<String, Object> data = new HashMap<>();
        data.put("id", savedUser.getId());
        data.put("name", savedUser.getName());
        data.put("email", savedUser.getEmail());
        data.put("role", savedUser.getRole());
        data.put("branch", savedUser.getBranch());
        data.put("graduationYear", savedUser.getGraduationYear());
        data.put("college", savedUser.getCollege());
        data.put("phone", savedUser.getPhone());
        data.put("hasResume", false);
        data.put("token", token);

        Map<String, Object> response = new HashMap<>(data);
        response.put("success", true);
        response.put("data", data);
        response.put("message", "Registration successful");

        return response;
    }

    @PostMapping("/login")
    public Map<String, Object> login(
            @RequestBody User loginUser
    ) {
        if (loginUser.getEmail() == null || loginUser.getPassword() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Email and password are required"
            );
        }

        Optional<User> existingUser =
                userRepository.findByEmail(loginUser.getEmail().trim().toLowerCase());

        if (existingUser.isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid email or password"
            );
        }

        User user = existingUser.get();

        boolean passwordMatch = false;
        if (passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            passwordMatch = true;
        } else if (user.getPassword() != null && user.getPassword().equals(loginUser.getPassword())) {
            // Migrating legacy plaintext password to BCrypt securely
            passwordMatch = true;
            user.setPassword(passwordEncoder.encode(loginUser.getPassword()));
            userRepository.save(user);
        }

        if (!passwordMatch) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );

        boolean hasResume = resumeRepository != null && resumeRepository.findAll().stream()
                .anyMatch(r -> user.getId().equals(r.getUserId()));

        Map<String, Object> response =
                new HashMap<>();

        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("branch", user.getBranch());
        response.put("graduationYear", user.getGraduationYear());
        response.put("college", user.getCollege());
        response.put("phone", user.getPhone());
        response.put("hasResume", hasResume);
        response.put("token", token);
        response.put("success", true);
        response.put("data", new HashMap<>(response));

        return response;
    }

    @GetMapping({"/{id}", "/me"})
    public User getUser(@PathVariable(required = false) Long id, Principal principal) {
        Long targetId = id;
        if (targetId == null && principal != null) {
            try {
                targetId = Long.valueOf(principal.getName());
            } catch (Exception ignored) {}
        }
        if (targetId == null) {
            throw new RuntimeException("User not authenticated or id missing");
        }

        User user =
                userRepository.findById(targetId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                ));

        user.setPassword(null);

        return user;
    }

    @PutMapping({"/{id}/profile", "/profile"})
    public Map<String, Object> updateProfile(
            @PathVariable(required = false) Long id,
            @RequestBody Map<String, Object> profile,
            Principal principal) {

        Long targetId = id;
        if (targetId == null && principal != null) {
            try {
                targetId = Long.valueOf(principal.getName());
            } catch (Exception ignored) {}
        }

        if (principal == null || targetId == null || !targetId.toString().equals(principal.getName())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You can only update your own profile.");
        }

                Object branchValue = profile.get("branch");
                Object graduationYearValue = profile.get("graduationYear");
                String branch = branchValue == null ? "" : branchValue.toString().trim();
                Integer graduationYear = graduationYearValue instanceof Number number
                                ? number.intValue()
                                : parseGraduationYear(graduationYearValue);

                if (branch.isBlank() || graduationYear == null
                                || graduationYear < 2020 || graduationYear > 2100) {
                        throw new IllegalArgumentException(
                                        "Enter your branch and a valid graduation year between 2020 and 2100.");
                }

                User user = userRepository.findById(targetId)
                                .orElseThrow(() -> new IllegalArgumentException("User not found."));
                user.setBranch(branch);
                user.setGraduationYear(graduationYear);
                if (profile.containsKey("college") && profile.get("college") != null) {
                    user.setCollege(profile.get("college").toString().trim());
                }
                if (profile.containsKey("phone") && profile.get("phone") != null) {
                    user.setPhone(profile.get("phone").toString().trim());
                }
                if (profile.containsKey("name") && profile.get("name") != null && !profile.get("name").toString().isBlank()) {
                    user.setName(profile.get("name").toString().trim());
                }
                userRepository.save(user);

                Map<String, Object> resp = new HashMap<>();
                resp.put("id", id);
                resp.put("name", user.getName());
                resp.put("branch", user.getBranch());
                resp.put("graduationYear", user.getGraduationYear());
                resp.put("college", user.getCollege());
                resp.put("phone", user.getPhone());
                return resp;
        }

        private Integer parseGraduationYear(Object value) {
                if (value == null) {
                        return null;
                }
                try {
                        return Integer.valueOf(value.toString());
                } catch (NumberFormatException exception) {
                        return null;
                }
        }
}