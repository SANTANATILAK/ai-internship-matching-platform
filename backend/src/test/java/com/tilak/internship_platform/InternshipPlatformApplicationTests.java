package com.tilak.internship_platform;

import com.tilak.internship_platform.entity.Opportunity;
import com.tilak.internship_platform.entity.OpportunityType;
import com.tilak.internship_platform.entity.User;
import com.tilak.internship_platform.entity.VerificationStatus;
import com.tilak.internship_platform.matcher.EligibilityEngine;
import com.tilak.internship_platform.matcher.SkillNormalizer;
import com.tilak.internship_platform.parser.ResumeAnalyzer;
import com.tilak.internship_platform.verification.FraudDetectorService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InternshipPlatformApplicationTests {

    private final SkillNormalizer skillNormalizer = new SkillNormalizer();
    private final EligibilityEngine eligibilityEngine = new EligibilityEngine();
    private final FraudDetectorService fraudDetectorService = new FraudDetectorService();
    private final ResumeAnalyzer resumeAnalyzer = new ResumeAnalyzer(skillNormalizer);

    @Test
    void testSkillNormalization() {
        assertEquals("Artificial Intelligence", skillNormalizer.normalize("AI"));
        assertEquals("Machine Learning", skillNormalizer.normalize("ml"));
        assertEquals("React", skillNormalizer.normalize("ReactJS"));
        assertEquals("Spring Boot", skillNormalizer.normalize("SpringBoot"));
        assertEquals("PostgreSQL", skillNormalizer.normalize("postgres"));
        assertTrue(skillNormalizer.areEquivalent("JS", "JavaScript"));
    }

    @Test
    void testFraudDetection() {
        Opportunity scamOpp = Opportunity.builder()
                .title("Data Entry - Pay registration fee 500")
                .description("Guaranteed job after training charge. Send money to proceed.")
                .applyUrl("http://bit.ly/scam-job")
                .build();

        FraudDetectorService.FraudCheckResult check = fraudDetectorService.inspect(scamOpp);
        assertTrue(check.isSuspicious());
        assertEquals(VerificationStatus.REJECTED, check.getRecommendedStatus());
    }

    @Test
    void testEligibilityEngine() {
        User student = User.builder()
                .graduationYear(2027)
                .branch("Computer Science")
                .build();

        Opportunity summerInternship = Opportunity.builder()
                .type(OpportunityType.INTERNSHIP)
                .graduationYears("2026,2027,2028")
                .branchRequirements("CSE, IT")
                .build();

        assertTrue(eligibilityEngine.isEligible(student, summerInternship));
    }

    @Test
    void testResumeAnalyzer() {
        String sampleResume = "Tilak Kumar\n" +
                "tilak.kumar@example.com | +91 9876543210\n" +
                "Education: B.Tech in Computer Science, 2026\n" +
                "Skills: Java, Python, Spring Boot, React, SQL, Git, Docker, Machine Learning\n" +
                "Projects:\n" +
                "- Built AI Internship Matching Platform using Spring Boot and React (increased search speed by 40%)\n";

        ResumeAnalyzer.AnalyzedResumeData data = resumeAnalyzer.analyze(sampleResume);
        assertEquals("tilak.kumar@example.com", data.getEmail());
        assertTrue(data.getSkills().contains("Java"));
        assertTrue(data.getSkills().contains("Python"));
        assertTrue(data.getSkills().contains("Spring Boot"));
        assertEquals(2026, data.getGraduationYear());
    }
}
