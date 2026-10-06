package com.tilak.internship_platform.matcher;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SkillNormalizer {

    private static final Map<String, String> SYNONYM_MAP = new HashMap<>();

    static {
        // AI / ML Synonyms
        SYNONYM_MAP.put("ai", "Artificial Intelligence");
        SYNONYM_MAP.put("artificial intelligence", "Artificial Intelligence");
        SYNONYM_MAP.put("ml", "Machine Learning");
        SYNONYM_MAP.put("machine learning", "Machine Learning");
        SYNONYM_MAP.put("dl", "Deep Learning");
        SYNONYM_MAP.put("deep learning", "Deep Learning");
        SYNONYM_MAP.put("nlp", "Natural Language Processing");
        SYNONYM_MAP.put("natural language processing", "Natural Language Processing");
        SYNONYM_MAP.put("cv", "Computer Vision");
        SYNONYM_MAP.put("computer vision", "Computer Vision");
        SYNONYM_MAP.put("genai", "Generative AI");
        SYNONYM_MAP.put("generative ai", "Generative AI");
        SYNONYM_MAP.put("llm", "Large Language Models");
        SYNONYM_MAP.put("llms", "Large Language Models");
        SYNONYM_MAP.put("large language models", "Large Language Models");

        // Web & Programming
        SYNONYM_MAP.put("js", "JavaScript");
        SYNONYM_MAP.put("javascript", "JavaScript");
        SYNONYM_MAP.put("ts", "TypeScript");
        SYNONYM_MAP.put("typescript", "TypeScript");
        SYNONYM_MAP.put("react", "React");
        SYNONYM_MAP.put("reactjs", "React");
        SYNONYM_MAP.put("react.js", "React");
        SYNONYM_MAP.put("node", "Node.js");
        SYNONYM_MAP.put("nodejs", "Node.js");
        SYNONYM_MAP.put("node.js", "Node.js");
        SYNONYM_MAP.put("vue", "Vue.js");
        SYNONYM_MAP.put("vuejs", "Vue.js");
        SYNONYM_MAP.put("angular", "Angular");
        SYNONYM_MAP.put("angularjs", "Angular");
        SYNONYM_MAP.put("python", "Python");
        SYNONYM_MAP.put("py", "Python");
        SYNONYM_MAP.put("java", "Java");
        SYNONYM_MAP.put("c++", "C++");
        SYNONYM_MAP.put("cpp", "C++");
        SYNONYM_MAP.put("c#", "C#");
        SYNONYM_MAP.put("csharp", "C#");
        SYNONYM_MAP.put("golang", "Go");
        SYNONYM_MAP.put("go", "Go");
        SYNONYM_MAP.put("rust", "Rust");

        // Frameworks & Libraries
        SYNONYM_MAP.put("spring", "Spring Boot");
        SYNONYM_MAP.put("springboot", "Spring Boot");
        SYNONYM_MAP.put("spring boot", "Spring Boot");
        SYNONYM_MAP.put("django", "Django");
        SYNONYM_MAP.put("flask", "Flask");
        SYNONYM_MAP.put("fastapi", "FastAPI");
        SYNONYM_MAP.put("fast api", "FastAPI");
        SYNONYM_MAP.put("express", "Express.js");
        SYNONYM_MAP.put("expressjs", "Express.js");
        SYNONYM_MAP.put("sklearn", "Scikit-learn");
        SYNONYM_MAP.put("scikit learn", "Scikit-learn");
        SYNONYM_MAP.put("scikit-learn", "Scikit-learn");
        SYNONYM_MAP.put("tensorflow", "TensorFlow");
        SYNONYM_MAP.put("tf", "TensorFlow");
        SYNONYM_MAP.put("pytorch", "PyTorch");
        SYNONYM_MAP.put("pandas", "Pandas");
        SYNONYM_MAP.put("numpy", "NumPy");
        SYNONYM_MAP.put("keras", "Keras");
        SYNONYM_MAP.put("opencv", "OpenCV");

        // Databases & Cloud / DevOps
        SYNONYM_MAP.put("sql", "SQL");
        SYNONYM_MAP.put("mysql", "MySQL");
        SYNONYM_MAP.put("postgres", "PostgreSQL");
        SYNONYM_MAP.put("postgresql", "PostgreSQL");
        SYNONYM_MAP.put("mongodb", "MongoDB");
        SYNONYM_MAP.put("mongo", "MongoDB");
        SYNONYM_MAP.put("redis", "Redis");
        SYNONYM_MAP.put("aws", "AWS");
        SYNONYM_MAP.put("amazon web services", "AWS");
        SYNONYM_MAP.put("azure", "Azure");
        SYNONYM_MAP.put("microsoft azure", "Azure");
        SYNONYM_MAP.put("gcp", "GCP");
        SYNONYM_MAP.put("google cloud", "GCP");
        SYNONYM_MAP.put("google cloud platform", "GCP");
        SYNONYM_MAP.put("docker", "Docker");
        SYNONYM_MAP.put("k8s", "Kubernetes");
        SYNONYM_MAP.put("kubernetes", "Kubernetes");
        SYNONYM_MAP.put("git", "Git");
        SYNONYM_MAP.put("github", "GitHub");
        SYNONYM_MAP.put("ci/cd", "CI/CD");
        SYNONYM_MAP.put("cicd", "CI/CD");
        SYNONYM_MAP.put("linux", "Linux");
        SYNONYM_MAP.put("rest api", "REST APIs");
        SYNONYM_MAP.put("rest", "REST APIs");
        SYNONYM_MAP.put("restful apis", "REST APIs");
    }

    public String normalize(String skill) {
        if (skill == null || skill.trim().isEmpty()) {
            return "";
        }
        String clean = skill.trim().toLowerCase();
        return SYNONYM_MAP.getOrDefault(clean, capitalizeWords(skill.trim()));
    }

    public boolean areEquivalent(String skill1, String skill2) {
        if (skill1 == null || skill2 == null) return false;
        return normalize(skill1).equalsIgnoreCase(normalize(skill2));
    }

    public Set<String> normalizeAll(Collection<String> skills) {
        Set<String> normalizedSet = new LinkedHashSet<>();
        if (skills != null) {
            for (String s : skills) {
                String norm = normalize(s);
                if (!norm.isEmpty()) {
                    normalizedSet.add(norm);
                }
            }
        }
        return normalizedSet;
    }

    private String capitalizeWords(String str) {
        String[] words = str.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0)));
                if (w.length() > 1) {
                    sb.append(w.substring(1).toLowerCase());
                }
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }
}
