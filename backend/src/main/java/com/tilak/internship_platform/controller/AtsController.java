package com.tilak.internship_platform.controller;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tilak.internship_platform.entity.User;
import com.tilak.internship_platform.repository.UserRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ats")
public class AtsController {

    private final ResumeController resumeController;
    private final UserRepository userRepository;

    public AtsController(ResumeController resumeController, UserRepository userRepository) {
        this.resumeController = resumeController;
        this.userRepository = userRepository;
    }

    @GetMapping("/latest")
    public Map<String, Object> getLatestAts(Principal principal) {
        return resumeController.getLatestResumeFromPrincipal(principal);
    }

    @GetMapping("/history")
    public Map<String, Object> getAtsHistory(Principal principal) {
        return resumeController.getLatestResumeFromPrincipal(principal);
    }
}
