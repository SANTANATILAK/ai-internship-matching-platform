package com.tilak.internship_platform.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.tilak.internship_platform.entity.Opportunity;

@Component
public class ISTIInternshipSource implements OpportunitySource {

        private static final String URL = "https://www.indiascienceandtechnology.gov.in/listingpage/internships";
        private String lastFetchFailureMessage;

        @Override
        public String getSourceName() {
                return "India Science Technology Innovation Portal";
        }

        @Override
        public String getLastFetchFailureMessage() {
                return lastFetchFailureMessage;
        }

        @Override
        public List<Opportunity> fetchOpportunities() {

                List<Opportunity> opportunities = new ArrayList<>();
                lastFetchFailureMessage = null;

                try {

                        Document listingPage = Jsoup.connect(URL)
                                        .userAgent("Mozilla/5.0")
                                        .timeout(15000)
                                        .get();

                        Elements links = listingPage.select(
                                        "a[href*='/internships/']");

                        for (Element link : links) {

                                String title = link.text().trim();
                                String detailUrl = link.absUrl("href");

                                if (title.isBlank() || detailUrl.isBlank()) {
                                        continue;
                                }

                                if (title.equalsIgnoreCase("Internships")) {
                                        continue;
                                }

                                try {

                                        Document detailPage = Jsoup.connect(detailUrl)
                                                        .userAgent("Mozilla/5.0")
                                                        .timeout(15000)
                                                        .get();

                                        Opportunity opportunity = buildOpportunity(
                                                        detailPage,
                                                        title,
                                                        detailUrl);

                                        opportunities.add(opportunity);

                                        System.out.println(
                                                        "Collected ISTI internship: " + title);

                                } catch (Exception detailException) {

                                        lastFetchFailureMessage = "Some ISTI detail pages failed: "
                                                        + detailException.getClass().getSimpleName();

                                        System.out.println(
                                                        "Could not read ISTI detail page: "
                                                                        + detailUrl);

                                        Opportunity opportunity = new Opportunity();

                                        opportunity.setCompany(
                                                        "India Science, Technology & Innovation Portal");

                                        opportunity.setTitle(title);
                                        opportunity.setLocation("India");
                                        opportunity.setCountry("India");
                                        opportunity.setType("Internship");

                                        opportunity.setSourceName(
                                                        "India Science Technology Innovation Portal");

                                        opportunity.setSourceUrl(detailUrl);
                                        opportunity.setApplyUrl(detailUrl);
                                        opportunity.setIsActive(true);

                                        opportunities.add(opportunity);
                                }
                        }

                        System.out.println(
                                        "ISTI internships found: "
                                                        + opportunities.size());

                } catch (Exception e) {

                        lastFetchFailureMessage = e.getClass().getSimpleName() + ": " + e.getMessage();
                        System.out.println("ISTI collection failed.");
                        System.out.println(e.getMessage());
                }

                return opportunities;
        }

        private Opportunity buildOpportunity(
                        Document document,
                        String title,
                        String detailUrl) {

                Opportunity opportunity = new Opportunity();

                String text = getCleanPageText(document);

                opportunity.setCompany(
                                "India Science, Technology & Innovation Portal");

                opportunity.setTitle(title);

                opportunity.setCountry("India");

                opportunity.setType("Internship");

                String eligibility = extractField(
                                text,
                                "Eligibility",
                                "Area",
                                "Target Group");

                String area = extractField(
                                text,
                                "Area",
                                "Target Group",
                                "Duration");

                String stipend = extractField(
                                text,
                                "Stipend",
                                "Institute",
                                "Application Timeline");

                String duration = extractField(
                                text,
                                "Duration",
                                "Stipend",
                                "Institute");

                String institute = extractField(
                                text,
                                "Institute",
                                "Application Timeline",
                                "Know More");

                String status = extractField(
                                text,
                                "Status",
                                "Application Timeline",
                                "Know More");

                String location = extractLocation(institute);

                opportunity.setLocation(location);

                /*
                 * Branch should contain the actual subject/area,
                 * not the complete eligibility text.
                 */
                opportunity.setBranch(
                                cleanArea(area));

                /*
                 * Skills are extracted separately from the area.
                 */
                opportunity.setSkills(
                                extractSkills(area));

                opportunity.setStipend(
                                cleanValue(stipend));

                opportunity.setDescription(
                                buildDescription(
                                                title,
                                                eligibility,
                                                area,
                                                duration,
                                                institute,
                                                status));

                opportunity.setGraduationYears(
                                extractRelevantYears(eligibility));

                opportunity.setApplyUrl(detailUrl);

                opportunity.setSourceUrl(detailUrl);

                opportunity.setSourceName(
                                "India Science Technology Innovation Portal");

                if (status != null
                                && status.toLowerCase().contains("closed")) {

                        opportunity.setIsActive(false);

                } else {

                        opportunity.setIsActive(true);
                }

                if (location != null
                                && location.toLowerCase().contains("pakistan")) {

                        opportunity.setCountry("Pakistan");
                        opportunity.setIsActive(false);
                }

                return opportunity;
        }

        private String getCleanPageText(Document document) {

                Document copy = document.clone();

                copy.select(
                                "header, footer, nav, script, style, noscript, "
                                                + ".region-header, .region-footer, "
                                                + ".breadcrumb, .menu, .navigation")
                                .remove();

                Element main = copy.selectFirst(
                                "main, article, .field--name-body");

                if (main != null) {
                        return main.text().trim();
                }

                Element body = copy.body();

                if (body != null) {
                        return body.text().trim();
                }

                return "";
        }

        private String extractField(
                        String text,
                        String startLabel,
                        String endLabel1,
                        String endLabel2) {

                if (text == null || text.isBlank()) {
                        return null;
                }

                String lowerText = text.toLowerCase();

                String start = startLabel.toLowerCase();

                int startIndex = lowerText.indexOf(start);

                if (startIndex == -1) {
                        return null;
                }

                int valueStart = startIndex + start.length();

                while (valueStart < text.length()
                                && (text.charAt(valueStart) == ':'
                                                || Character.isWhitespace(
                                                                text.charAt(valueStart)))) {

                        valueStart++;
                }

                int endIndex = text.length();

                String[] endLabels = {
                                endLabel1,
                                endLabel2
                };

                for (String endLabel : endLabels) {

                        if (endLabel == null) {
                                continue;
                        }

                        int index = lowerText.indexOf(
                                        endLabel.toLowerCase(),
                                        valueStart);

                        if (index != -1
                                        && index < endIndex) {

                                endIndex = index;
                        }
                }

                if (valueStart >= endIndex) {
                        return null;
                }

                String value = text.substring(
                                valueStart,
                                endIndex).trim();

                return cleanValue(value);
        }

        private String cleanValue(String value) {

                if (value == null) {
                        return null;
                }

                value = value
                                .replaceAll("\\s+", " ")
                                .trim();

                if (value.isBlank()) {
                        return null;
                }

                return value;
        }

        private String cleanArea(String area) {

                if (area == null || area.isBlank()) {
                        return null;
                }

                String value = cleanValue(area);

                if (value == null) {
                        return null;
                }

                int eligibilityIndex = value.toLowerCase().indexOf("eligibility:");

                if (eligibilityIndex != -1) {
                        value = value.substring(
                                        0,
                                        eligibilityIndex).trim();
                }

                return value;
        }

        private String extractSkills(String area) {

                if (area == null || area.isBlank()) {
                        return null;
                }

                String value = cleanArea(area);

                if (value == null) {
                        return null;
                }

                String lower = value.toLowerCase();

                if (lower.contains("data science")
                                || lower.contains("data analytics")
                                || lower.contains("artificial intelligence")
                                || lower.contains("information technology")) {

                        List<String> skills = new ArrayList<>();

                        addSkillIfPresent(
                                        value,
                                        "Data Science",
                                        skills);

                        addSkillIfPresent(
                                        value,
                                        "Data Analytics",
                                        skills);

                        addSkillIfPresent(
                                        value,
                                        "Artificial Intelligence",
                                        skills);

                        addSkillIfPresent(
                                        value,
                                        "Information Technology",
                                        skills);

                        if (!skills.isEmpty()) {
                                return String.join(", ", skills);
                        }
                }

                if (lower.contains("science")
                                || lower.contains("technology")) {

                        return value;
                }

                return value;
        }

        private void addSkillIfPresent(
                        String text,
                        String skill,
                        List<String> skills) {

                if (text.toLowerCase()
                                .contains(skill.toLowerCase())
                                && !skills.contains(skill)) {

                        skills.add(skill);
                }
        }

        private String extractLocation(String institute) {

                if (institute == null
                                || institute.isBlank()) {

                        return "India";
                }

                String value = institute
                                .replaceAll("\\s+", " ")
                                .trim();

                String lower = value.toLowerCase();

                if (lower.contains("islamabad")
                                || lower.contains("pakistan")) {

                        return "Islamabad, Pakistan";
                }

                if (lower.contains("new delhi")
                                || lower.contains("delhi")) {

                        return "New Delhi, India";
                }

                if (lower.contains("mumbai")
                                || lower.contains("bombay")) {

                        return "Mumbai, India";
                }

                if (lower.contains("hyderabad")) {

                        return "Hyderabad, India";
                }

                if (lower.contains("bengaluru")
                                || lower.contains("bangalore")) {

                        return "Bengaluru, India";
                }

                if (lower.contains("chennai")
                                || lower.contains("madras")) {

                        return "Chennai, India";
                }

                if (lower.contains("pune")) {

                        return "Pune, India";
                }

                if (lower.contains("kolkata")
                                || lower.contains("calcutta")) {

                        return "Kolkata, India";
                }

                if (lower.contains("ahmedabad")) {

                        return "Ahmedabad, India";
                }

                if (lower.contains("noida")) {

                        return "Noida, India";
                }

                if (lower.contains("gurugram")
                                || lower.contains("gurgaon")) {

                        return "Gurugram, India";
                }

                if (lower.contains("lucknow")) {

                        return "Lucknow, India";
                }

                if (lower.contains("jaipur")) {

                        return "Jaipur, India";
                }

                if (lower.contains("kochi")
                                || lower.contains("cochin")) {

                        return "Kochi, India";
                }

                if (lower.contains("thiruvananthapuram")) {

                        return "Thiruvananthapuram, India";
                }

                if (lower.contains("chandigarh")) {

                        return "Chandigarh, India";
                }

                if (lower.contains("indore")) {

                        return "Indore, India";
                }

                if (lower.contains("bhubaneswar")) {

                        return "Bhubaneswar, India";
                }

                if (lower.contains("visakhapatnam")
                                || lower.contains("vizag")) {

                        return "Visakhapatnam, India";
                }

                return "India";
        }

        private String buildDescription(
                        String title,
                        String eligibility,
                        String area,
                        String duration,
                        String institute,
                        String status) {

                StringBuilder description = new StringBuilder();

                description.append(title);

                if (eligibility != null
                                && !eligibility.isBlank()) {

                        description.append(
                                        ". Eligibility: ").append(
                                                        cleanEligibility(eligibility));
                }

                if (area != null
                                && !area.isBlank()) {

                        description.append(
                                        ". Area: ").append(
                                                        cleanArea(area));
                }

                if (duration != null
                                && !duration.isBlank()) {

                        description.append(
                                        ". Duration: ").append(
                                                        cleanValue(duration));
                }

                if (institute != null
                                && !institute.isBlank()) {

                        description.append(
                                        ". Institute: ").append(
                                                        cleanValue(institute));
                }

                if (status != null
                                && !status.isBlank()) {

                        description.append(
                                        ". Status: ").append(
                                                        cleanValue(status));
                }

                return description.toString();
        }

        private String cleanEligibility(
                        String eligibility) {

                String value = cleanValue(eligibility);

                if (value == null) {
                        return null;
                }

                value = value.replaceAll(
                                "\\s*Area:.*$",
                                "").trim();

                return value;
        }

        private String extractRelevantYears(
                        String eligibility) {

                if (eligibility == null
                                || eligibility.isBlank()) {

                        return null;
                }

                List<String> years = new ArrayList<>();

                Pattern pattern = Pattern.compile(
                                "\\b20(2[6-9]|3[0-5])\\b");

                Matcher matcher = pattern.matcher(eligibility);

                while (matcher.find()) {

                        String year = matcher.group();

                        if (!years.contains(year)) {
                                years.add(year);
                        }
                }

                if (years.isEmpty()) {
                        return null;
                }

                return String.join(",", years);
        }
}