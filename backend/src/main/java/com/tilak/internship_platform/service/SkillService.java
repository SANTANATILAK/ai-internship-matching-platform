package com.tilak.internship_platform.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SkillService {

    private final String[] skills = {
            "Java", "Python", "C", "C++", "SQL", "MySQL",
            "Machine Learning", "Artificial Intelligence", "Deep Learning",
            "React", "JavaScript", "HTML", "CSS", "Spring Boot", "Git",
            "GitHub", "Docker", "AWS", "TensorFlow", "PyTorch", "Pandas",
            "NumPy", "Scikit-learn"
    };

    public List<String> extractSkills(String text) {
        List<String> foundSkills = new ArrayList<>();
        String lowerText = text.toLowerCase();

        for (String skill : skills) {
            if (lowerText.contains(skill.toLowerCase())) {
                foundSkills.add(skill);
            }
        }

        return foundSkills;
    }
}