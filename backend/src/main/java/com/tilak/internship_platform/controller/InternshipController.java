package com.tilak.internship_platform.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tilak.internship_platform.entity.Internship;
import com.tilak.internship_platform.repository.InternshipRepository;

@RestController
@RequestMapping("/api/internships")
public class InternshipController {

    private final InternshipRepository repository;

    public InternshipController(InternshipRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Internship> getAllInternships() {
        return repository.findAll();
    }

    @PostMapping
    public Internship createInternship(@RequestBody Internship internship) {
        return repository.save(internship);
    }
}