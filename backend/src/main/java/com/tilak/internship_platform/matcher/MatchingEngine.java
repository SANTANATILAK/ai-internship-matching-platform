package com.tilak.internship_platform.matcher;

import com.tilak.internship_platform.dto.response.OpportunityMatchResponse;
import com.tilak.internship_platform.entity.Company;
import com.tilak.internship_platform.entity.Opportunity;
import com.tilak.internship_platform.entity.User;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MatchingEngine {

    private final SkillNormalizer skillNormalizer;
    private final EligibilityEngine eligibilityEngine;

    public MatchingEngine(SkillNormalizer skillNormalizer, EligibilityEngine eligibilityEngine) {
        this.skillNormalizer = skillNormalizer;
        this.eligibilityEngine = eligibilityEngine;
    }

    public OpportunityMatchResponse match(User user, Set<String> userSkills, Opportunity opp) {
        Company comp = opp.getCompany();

        // 1. Normalize required skills
        Set<String> requiredSkills = new LinkedHashSet<>();
        if (opp.getRequiredSkills() != null && !opp.getRequiredSkills().trim().isEmpty()) {
            String[] splits = opp.getRequiredSkills().split("[,;|]");
            for (String s : splits) {
                if (!s.trim().isEmpty()) {
                    requiredSkills.add(skillNormalizer.normalize(s.trim()));
                }
            }
        }

        // 2. Normalize student skills
        Set<String> normalizedUserSkills = skillNormalizer.normalizeAll(userSkills);

        // 3. Find matched and missing skills
        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String req : requiredSkills) {
            boolean found = false;
            for (String usr : normalizedUserSkills) {
                if (skillNormalizer.areEquivalent(req, usr)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                matched.add(req);
            } else {
                missing.add(req);
            }
        }

        // 4. Calculate skill score (60 points)
        double skillRatio = requiredSkills.isEmpty() ? 1.0 : (double) matched.size() / requiredSkills.size();
        double skillScore = skillRatio * 60.0;

        // 5. Eligibility score (15 points)
        boolean eligible = eligibilityEngine.isEligible(user, opp);
        double eligibilityScore = eligible ? 15.0 : 0.0;

        // 6. Role Preference Match (15 points)
        double roleScore = 0.0;
        if (user.getRolePreference() != null && !user.getRolePreference().trim().isEmpty()) {
            String userRole = user.getRolePreference().toLowerCase();
            String oppTitle = opp.getTitle().toLowerCase();
            if (oppTitle.contains(userRole) || userRole.contains(oppTitle)) {
                roleScore = 15.0;
            } else {
                // Partial keyword overlap
                String[] words = userRole.split("\\s+");
                for (String w : words) {
                    if (w.length() > 2 && oppTitle.contains(w)) {
                        roleScore = 10.0;
                        break;
                    }
                }
            }
        } else {
            roleScore = 10.0; // default points if not set
        }

        // 7. Work Type & Location Preference (10 points)
        double locScore = 0.0;
        if (user.getPreferredWorkType() != null && opp.getWorkType() != null) {
            if (user.getPreferredWorkType().equalsIgnoreCase(opp.getWorkType().name())) {
                locScore += 5.0;
            }
        } else {
            locScore += 3.0;
        }

        if (user.getPreferredLocation() != null && opp.getLocation() != null) {
            if (opp.getLocation().toLowerCase().contains(user.getPreferredLocation().toLowerCase())) {
                locScore += 5.0;
            }
        } else {
            locScore += 3.0;
        }

        int totalPercentage = (int) Math.round(skillScore + eligibilityScore + roleScore + locScore);
        totalPercentage = Math.max(10, Math.min(100, totalPercentage));

        // 8. Match level classification
        String matchLevel;
        if (totalPercentage >= 90) {
            matchLevel = "Excellent Match";
        } else if (totalPercentage >= 80) {
            matchLevel = "Strong Match";
        } else if (totalPercentage >= 60) {
            matchLevel = "Good Match";
        } else if (totalPercentage >= 40) {
            matchLevel = "Partial Match";
        } else {
            matchLevel = "Not Recommended";
        }

        // 9. Match reasons
        List<String> reasons = new ArrayList<>();
        if (!requiredSkills.isEmpty()) {
            reasons.add("Matched " + matched.size() + " of " + requiredSkills.size() + " core skills");
        }
        if (eligible && user.getGraduationYear() != null) {
            reasons.add("Batch " + user.getGraduationYear() + " is eligible for this " + opp.getType().name().replace('_', ' '));
        }
        if (roleScore >= 10.0) {
            reasons.add("Aligns with preferred role: " + opp.getTitle());
        }
        if (locScore >= 7.0) {
            reasons.add("Work arrangement matches location preference (" + opp.getWorkType().name() + ")");
        }
        if (comp != null && comp.isVerified()) {
            reasons.add("Verified official career opening");
        }

        return OpportunityMatchResponse.builder()
                .opportunityId(opp.getId())
                .companyName(comp != null ? comp.getName() : "Unknown")
                .companyDomain(comp != null ? comp.getOfficialDomain() : "")
                .companyVerified(comp != null && comp.isVerified())
                .title(opp.getTitle())
                .location(opp.getLocation())
                .workType(opp.getWorkType())
                .type(opp.getType())
                .stipend(opp.getStipend())
                .salary(opp.getSalary())
                .applyUrl(opp.getApplyUrl())
                .postedDate(opp.getPostedDate())
                .lastUpdated(opp.getLastUpdated())
                .verificationStatus(opp.getVerificationStatus())
                .matchPercentage(totalPercentage)
                .matchLevel(matchLevel)
                .matchedSkills(matched)
                .missingSkills(missing)
                .totalRequiredSkills(requiredSkills.size())
                .eligible(eligible)
                .matchingReasons(reasons)
                .build();
    }
}
