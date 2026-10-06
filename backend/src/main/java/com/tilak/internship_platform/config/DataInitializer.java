package com.tilak.internship_platform.config;

import com.tilak.internship_platform.collector.OpportunityCollectorService;
import com.tilak.internship_platform.entity.*;
import com.tilak.internship_platform.matcher.SkillNormalizer;
import com.tilak.internship_platform.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final OpportunityRepository opportunityRepository;
    private final SkillRepository skillRepository;
    private final PasswordEncoder passwordEncoder;
    private final SkillNormalizer skillNormalizer;
    private final OpportunityCollectorService collectorService;

    public DataInitializer(UserRepository userRepository,
                           CompanyRepository companyRepository,
                           OpportunityRepository opportunityRepository,
                           SkillRepository skillRepository,
                           PasswordEncoder passwordEncoder,
                           SkillNormalizer skillNormalizer,
                           OpportunityCollectorService collectorService) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.opportunityRepository = opportunityRepository;
        this.skillRepository = skillRepository;
        this.passwordEncoder = passwordEncoder;
        this.skillNormalizer = skillNormalizer;
        this.collectorService = collectorService;
    }

    @Override
    public void run(String... args) {
        logger.info("Initializing system baseline data...");

        // 1. Seed Skills Dictionary
        seedSkills();

        // 2. Seed Admin User
        seedAdmin();

        // 3. Seed Verified Partner Companies
        seedCompanies();

        // 4. Seed Verified Opportunities
        seedOpportunities();

        // 5. Trigger initial collection cycle
        try {
            collectorService.runCollectionCycle();
        } catch (Exception e) {
            logger.warn("Initial collection cycle notification: {}", e.getMessage());
        }

        logger.info("Baseline data initialization finished successfully.");
    }

    private void seedSkills() {
        if (skillRepository.count() == 0) {
            List<String> coreSkills = Arrays.asList(
                    "Python", "Java", "C++", "C", "C#", "JavaScript", "TypeScript",
                    "React", "Angular", "Vue", "Node.js", "Spring Boot", "Django", "FastAPI",
                    "SQL", "MySQL", "PostgreSQL", "MongoDB", "Redis",
                    "Machine Learning", "Deep Learning", "Artificial Intelligence",
                    "TensorFlow", "PyTorch", "Scikit-learn", "Pandas", "NumPy", "OpenCV",
                    "Git", "GitHub", "Docker", "Kubernetes", "AWS", "Azure", "GCP",
                    "REST APIs", "Microservices", "Data Structures", "Algorithms"
            );

            for (String s : coreSkills) {
                skillRepository.save(Skill.builder()
                        .name(s)
                        .normalizedName(skillNormalizer.normalize(s))
                        .category("TECHNICAL")
                        .build());
            }
            logger.info("Seeded {} baseline technical skills", coreSkills.size());
        }
    }

    private void seedAdmin() {
        if (!userRepository.existsByEmail("admin@internmatch.com")) {
            User admin = User.builder()
                    .name("Platform Admin")
                    .email("admin@internmatch.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .phone("+91-9876543210")
                    .branch("Administration")
                    .college("InternMatch HQ")
                    .graduationYear(2024)
                    .location("Bengaluru")
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build();
            userRepository.save(admin);
            logger.info("Created default administrator: admin@internmatch.com / Admin@123");
        }
    }

    private void seedCompanies() {
        if (companyRepository.count() == 0) {
            Company msft = Company.builder()
                    .name("Microsoft")
                    .officialDomain("microsoft.com")
                    .careerUrl("https://careers.microsoft.com")
                    .description("Global leader in software, cloud computing, artificial intelligence, and operating systems.")
                    .industry("Information Technology / Cloud")
                    .location("Bengaluru / Hyderabad")
                    .verified(true)
                    .verificationStatus(VerificationStatus.VERIFIED)
                    .trustScore(99)
                    .build();

            Company goog = Company.builder()
                    .name("Google")
                    .officialDomain("google.com")
                    .careerUrl("https://careers.google.com")
                    .description("World's premier search, cloud, AI, and digital consumer ecosystem.")
                    .industry("Internet / Cloud / AI")
                    .location("Bengaluru / Hyderabad / Gurugram")
                    .verified(true)
                    .verificationStatus(VerificationStatus.VERIFIED)
                    .trustScore(99)
                    .build();

            Company amzn = Company.builder()
                    .name("Amazon")
                    .officialDomain("amazon.jobs")
                    .careerUrl("https://amazon.jobs")
                    .description("E-commerce and cloud infrastructure leader via AWS.")
                    .industry("Cloud & E-Commerce")
                    .location("Bengaluru / Hyderabad / Chennai")
                    .verified(true)
                    .verificationStatus(VerificationStatus.VERIFIED)
                    .trustScore(98)
                    .build();

            Company atls = Company.builder()
                    .name("Atlassian")
                    .officialDomain("atlassian.com")
                    .careerUrl("https://www.atlassian.com/company/careers")
                    .description("Enterprise collaboration tools including Jira, Confluence, and Trello.")
                    .industry("Enterprise Software")
                    .location("Remote - India / Bengaluru")
                    .verified(true)
                    .verificationStatus(VerificationStatus.VERIFIED)
                    .trustScore(96)
                    .build();

            Company flpk = Company.builder()
                    .name("Flipkart")
                    .officialDomain("flipkartcareers.com")
                    .careerUrl("https://www.flipkartcareers.com")
                    .description("India's leading e-commerce marketplace and logistics innovator.")
                    .industry("E-Commerce & Supply Chain")
                    .location("Bengaluru")
                    .verified(true)
                    .verificationStatus(VerificationStatus.VERIFIED)
                    .trustScore(94)
                    .build();

            companyRepository.saveAll(Arrays.asList(msft, goog, amzn, atls, flpk));
            logger.info("Seeded 5 verified enterprise companies");
        }
    }

    private void seedOpportunities() {
        if (opportunityRepository.count() == 0) {
            companyRepository.findByNameIgnoreCase("Flipkart").ifPresent(flipkart -> {
                Opportunity opp = Opportunity.builder()
                        .company(flipkart)
                        .title("Software Development Engineer Intern - Backend (2026/2027)")
                        .description("Build high-throughput distributed microservices handling millions of transactions during festive sales.")
                        .location("Bengaluru")
                        .workType(WorkType.HYBRID)
                        .type(OpportunityType.INTERNSHIP)
                        .stipend("INR 80,000 / month")
                        .requiredSkills("Java, Spring Boot, MySQL, Redis, Git, Data Structures")
                        .graduationYears("2026, 2027")
                        .degreeRequirements("B.Tech / B.E.")
                        .branchRequirements("CSE, IT, ECE")
                        .experienceRequirement("Fresher")
                        .applyUrl("https://www.flipkartcareers.com/job/SDE-INTERN-2026")
                        .sourceUrl("https://www.flipkartcareers.com")
                        .postedDate(LocalDateTime.now().minusDays(1))
                        .expiryDate(LocalDateTime.now().plusDays(40))
                        .externalId("FK-SDE-INT-2026")
                        .sourceType("OFFICIAL_CAREERS")
                        .verificationStatus(VerificationStatus.VERIFIED)
                        .status(OpportunityStatus.ACTIVE)
                        .build();

                opportunityRepository.save(opp);
            });
        }
    }
}
