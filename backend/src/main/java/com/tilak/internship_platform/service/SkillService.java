package com.tilak.internship_platform.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class SkillService {

    private final String[] skills = {
            // Computer Science, Software Engineering & IT
            "Java", "Python", "C", "C++", "C#", "Go", "Rust", "JavaScript", "TypeScript",
            "React", "Angular", "Vue", "Node.js", "Spring Boot", "Django", "Flask", "FastAPI",
            "HTML", "CSS", "SQL", "MySQL", "PostgreSQL", "MongoDB", "Redis", "Docker",
            "Kubernetes", "AWS", "Azure", "GCP", "Git", "GitHub", "Linux", "REST API",
            "GraphQL", "Microservices", "Data Structures", "Algorithms",

            // AI, Machine Learning, Data Science
            "Machine Learning", "Artificial Intelligence", "Deep Learning", "NLP", "Natural Language Processing",
            "Computer Vision", "Generative AI", "LLM", "TensorFlow", "PyTorch", "Scikit-learn", "Pandas",
            "NumPy", "Keras", "OpenCV", "Hugging Face", "Data Science", "Data Analytics", "Power BI",
            "Tableau", "Statistics",

            // Electronics & Communication (ECE) & Electrical (EEE)
            "VLSI", "Verilog", "VHDL", "SystemVerilog", "Embedded Systems", "Microcontrollers",
            "Arduino", "Raspberry Pi", "PCB Design", "FPGA", "MATLAB", "Simulink", "IoT",
            "Internet of Things", "Signal Processing", "Digital Electronics", "Analog Electronics",
            "Power Systems", "Power Electronics", "Circuit Design", "KiCAD",

            // Mechanical Engineering, Robotics & Mechatronics
            "AutoCAD", "SolidWorks", "CATIA", "ANSYS", "Creo", "Thermodynamics",
            "Fluid Mechanics", "CAD", "CAM", "CNC", "Mechatronics", "Robotics",
            "Automation", "Manufacturing", "Finite Element Analysis", "FEA",
            "Vehicle Dynamics", "EV Systems",

            // Civil Engineering & Infrastructure
            "STAAD.Pro", "Revit", "ETABS", "GIS", "Surveying", "Structural Analysis",
            "BIM", "Civil 3D", "Construction Management", "Structural Design",

            // Chemical Engineering & Biotechnology
            "Aspen Plus", "Process Simulation", "Chemical Engineering", "Biotechnology",
            "Chromatography", "Quality Assurance", "Pharmaceutical", "Materials Science",

            // Management & General Tech
            "Agile", "Scrum", "Product Management", "Business Analysis", "Project Management"
    };

    public List<String> extractSkills(String text) {
        List<String> foundSkills = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return foundSkills;
        }

        for (String skill : skills) {
            String regex = "(?i)\\b" + Pattern.quote(skill) + "\\b";
            if (Pattern.compile(regex).matcher(text).find()) {
                if (!foundSkills.contains(skill)) {
                    foundSkills.add(skill);
                }
            }
        }

        return foundSkills;
    }
}