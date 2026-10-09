package com.tilak.internship_platform.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.tilak.internship_platform.entity.Opportunity;

@Component
public class GoogleJobsOpportunitySource implements OpportunitySource {

        private final WebClient webClient;

        @Value("${SERPAPI_KEY:}")
        private String apiKey;

        private final Set<String> seenJobs = new HashSet<>();
        private String lastFetchFailureMessage;

        private static final String[] SEARCHES = {
                        "AI ML internship India stipend",
                        "Machine Learning internship India stipend",
                        "Data Science internship India stipend",
                        "Software Engineer internship India stipend",
                        "Python internship India stipend",
                        "Generative AI internship India stipend",
                        "Data Analyst internship India stipend",
                        "Artificial Intelligence internship India stipend",
                        "Deep Learning internship India stipend"
        };

        private static final String[] CONSULTANCY_KEYWORDS = {
                        "consultancy",
                        "consultant",
                        "consultants",
                        "job consultancy",
                        "employment consultancy",
                        "placement consultancy",
                        "recruitment agency",
                        "recruitment consultancy",
                        "staffing agency",
                        "staffing solutions",
                        "manpower",
                        "placement agency",
                        "placement services",
                        "job placement",
                        "career consultancy",
                        "career solutions",
                        "hr solutions",
                        "employment services",
                        "recruitment services",
                        "job solutions",
                        "outsourcing services"
        };

        private static final String[] TRAINING_KEYWORDS = {
                        "training institute",
                        "training centre",
                        "training center",
                        "coaching institute",
                        "coaching center",
                        "coaching centre",
                        "academy",
                        "skill development institute",
                        "educational institute",
                        "education institute",
                        "learning institute",
                        "training program",
                        "training programme",
                        "certificate program",
                        "certification program",
                        "course provider"
        };

        private static final String[] UNPAID_KEYWORDS = {
                        "unpaid internship",
                        "unpaid intern",
                        "no stipend",
                        "without stipend",
                        "no salary",
                        "voluntary internship",
                        "volunteer internship",
                        "certificate only",
                        "certificate-only",
                        "certificate will be provided",
                        "experience certificate"
        };

        private static final String[] FOREIGN_COUNTRY_KEYWORDS = {
                        "united states",
                        "united states of america",
                        "usa",
                        "u.s.a",
                        "us",
                        "canada",
                        "united kingdom",
                        "uk",
                        "england",
                        "scotland",
                        "wales",
                        "australia",
                        "new zealand",
                        "singapore",
                        "malaysia",
                        "germany",
                        "france",
                        "italy",
                        "spain",
                        "netherlands",
                        "ireland",
                        "switzerland",
                        "sweden",
                        "norway",
                        "denmark",
                        "finland",
                        "poland",
                        "portugal",
                        "belgium",
                        "austria",
                        "uae",
                        "united arab emirates",
                        "dubai",
                        "qatar",
                        "saudi arabia",
                        "israel",
                        "japan",
                        "south korea",
                        "china",
                        "hong kong",
                        "indonesia",
                        "philippines",
                        "vietnam",
                        "brazil",
                        "mexico",
                        "south africa"
        };

        private static final String[] INDIA_LOCATION_KEYWORDS = {
                        "india",
                        "indian",
                        "hyderabad",
                        "bengaluru",
                        "bangalore",
                        "chennai",
                        "mumbai",
                        "pune",
                        "delhi",
                        "new delhi",
                        "gurugram",
                        "gurgaon",
                        "noida",
                        "kolkata",
                        "ahmedabad",
                        "jaipur",
                        "kochi",
                        "coimbatore",
                        "visakhapatnam",
                        "vijayawada",
                        "bhubaneswar",
                        "chandigarh",
                        "indore",
                        "nagpur",
                        "lucknow",
                        "surat",
                        "vadodara",
                        "thiruvananthapuram",
                        "remote - india",
                        "remote india",
                        "work from home india"
        };

        private static final String[] SKILL_KEYWORDS = {
                        "Python",
                        "Java",
                        "C",
                        "C++",
                        "C#",
                        "SQL",
                        "MySQL",
                        "PostgreSQL",
                        "MongoDB",
                        "Machine Learning",
                        "Deep Learning",
                        "Artificial Intelligence",
                        "AI",
                        "Generative AI",
                        "GenAI",
                        "LLM",
                        "NLP",
                        "Computer Vision",
                        "TensorFlow",
                        "PyTorch",
                        "Scikit-learn",
                        "Pandas",
                        "NumPy",
                        "Data Science",
                        "Data Analytics",
                        "Data Analysis",
                        "Power BI",
                        "Tableau",
                        "AWS",
                        "Azure",
                        "Google Cloud",
                        "GCP",
                        "Docker",
                        "Kubernetes",
                        "Git",
                        "GitHub",
                        "React",
                        "Angular",
                        "JavaScript",
                        "TypeScript",
                        "Node.js",
                        "Spring Boot",
                        "Cybersecurity",
                        "Cyber Security",
                        "Ethical Hacking",
                        "Network Security",
                        "Linux"
        };

        public GoogleJobsOpportunitySource(WebClient webClient) {
                this.webClient = webClient;
        }

        @Override
        public String getSourceName() {
                return "Google Jobs";
        }

        @Override
        public boolean isConfigured() {
                return apiKey != null && !apiKey.isBlank();
        }

        @Override
        public String getLastFetchFailureMessage() {
                return lastFetchFailureMessage;
        }

        @Override
        public List<Opportunity> fetchOpportunities() {

                List<Opportunity> opportunities = new ArrayList<>();
                lastFetchFailureMessage = null;

                if (!isConfigured()) {
                        System.out.println("SerpApi key is missing.");
                        return opportunities;
                }

                seenJobs.clear();
                int successfulSearches = 0;
                int failedSearches = 0;

                for (String search : SEARCHES) {

                        try {

                                Map<String, Object> response = webClient.get()
                                                .uri(uriBuilder -> uriBuilder
                                                                .scheme("https")
                                                                .host("serpapi.com")
                                                                .path("/search.json")
                                                                .queryParam("engine", "google_jobs")
                                                                .queryParam("q", search)
                                                                .queryParam("location", "India")
                                                                .queryParam("gl", "in")
                                                                .queryParam("hl", "en")
                                                                .queryParam("api_key", apiKey)
                                                                .build())
                                                .retrieve()
                                                .bodyToMono(Map.class)
                                                .block();

                                if (response == null) {
                                        failedSearches++;
                                        continue;
                                }

                                Object jobsObject = response.get("jobs_results");

                                if (!(jobsObject instanceof List<?> jobs)) {
                                        failedSearches++;
                                        continue;
                                }
                                successfulSearches++;

                                for (Object jobObject : jobs) {

                                        if (!(jobObject instanceof Map<?, ?> job)) {
                                                continue;
                                        }

                                        Opportunity opportunity = convertJob(job);

                                        if (opportunity == null) {
                                                continue;
                                        }

                                        String identity = opportunity.getSourceJobId() != null
                                                        ? opportunity.getSourceJobId()
                                                        : opportunity.getApplyUrl() != null
                                                                        ? opportunity.getApplyUrl()
                                                                        : opportunity.getTitle() + "|"
                                                                                        + opportunity.getLocation();
                                        String key = normalize(opportunity.getCompany()) + "|" + normalize(identity);

                                        if (seenJobs.contains(key)) {
                                                continue;
                                        }

                                        seenJobs.add(key);
                                        opportunities.add(opportunity);
                                }

                        } catch (Exception e) {
                                failedSearches++;
                                lastFetchFailureMessage = e.getClass().getSimpleName()
                                                + ": " + e.getMessage();
                        }
                }

                if (failedSearches > 0 && successfulSearches > 0) {
                        lastFetchFailureMessage = failedSearches + " of " + SEARCHES.length + " searches failed";
                } else if (successfulSearches == 0 && failedSearches > 0
                                && lastFetchFailureMessage == null) {
                        lastFetchFailureMessage = "All search requests failed";
                }

                System.out.println(
                                "Google Jobs paid Indian company internships found: "
                                                + opportunities.size());

                return opportunities;
        }

        private Opportunity convertJob(Map<?, ?> job) {

                String title = getString(job, "title");

                String company = getString(job, "company_name");

                String location = getString(job, "location");

                String description = getString(job, "description");

                String via = getString(job, "via");

                if (isBlank(title)
                                || isBlank(company)
                                || isBlank(location)) {

                        return null;
                }

                String combinedText = safe(title) + " "
                                + safe(company) + " "
                                + safe(location) + " "
                                + safe(description) + " "
                                + safe(via);

                String lowerText = combinedText.toLowerCase();

                /*
                 * 1. Internship check
                 */

                String jobType = classifyJobType(title, description);
                if (jobType == null) {
                        return null;
                }

                /*
                 * 2. India location check
                 */

                if (!isIndiaLocation(location, combinedText)) {
                        return null;
                }

                /*
                 * 3. Foreign location check
                 */

                if (containsForeignCountry(location)) {
                        return null;
                }

                /*
                 * 4. Consultancy / agency check
                 */

                if (isConsultancy(company, title, description)) {
                        return null;
                }

                /*
                 * 5. Training / institute check
                 */

                if (isTrainingProvider(company, title, description)) {
                        return null;
                }

                /*
                 * 6. Unpaid check
                 */

                if (containsAny(lowerText, UNPAID_KEYWORDS)) {
                        return null;
                }

                /*
                 * 7. Compensation extraction
                 */

                String compensation = extractCompensation(job, description);

                /*
                 * 8. Application URL
                 */

                String applyUrl = extractApplyUrl(job);

                if (isBlank(applyUrl)) {
                        return null;
                }

                /*
                 * 9. Create opportunity
                 */

                Opportunity opportunity = new Opportunity();

                opportunity.setTitle(title.trim());

                opportunity.setCompany(company.trim());

                opportunity.setLocation(location.trim());

                opportunity.setDescription(
                                cleanText(description));

                opportunity.setCountry("India");

                opportunity.setType(jobType);
                opportunity.setJobType(jobType);
                opportunity.setDepartment(firstNonBlank(
                                getString(job, "job_category"),
                                getString(job, "category")));
                opportunity.setWorkMode(classifyWorkMode(location, description));
                if (compensation != null) {
                        String lowerCompensation = compensation.toLowerCase();
                        if (lowerCompensation.contains("year") || lowerCompensation.contains("annual")) {
                                opportunity.setSalary(compensation);
                        } else {
                                opportunity.setStipend(compensation);
                        }
                }

                opportunity.setApplyUrl(applyUrl);

                opportunity.setSourceUrl(applyUrl);

                opportunity.setSourceName("Google Jobs");
                opportunity.setSourceJobId(firstNonBlank(
                                getString(job, "job_id"), getString(job, "jobId")));
                opportunity.setSourceJobId(getString(job, "job_id"));

                opportunity.setSkills(
                                extractSkills(title, description));

                opportunity.setDetectedAt(
                                LocalDateTime.now());

                opportunity.setIsActive(true);

                return opportunity;
        }

        private String classifyJobType(
                        String title,
                        String description) {

                String text = safe(title) + " " + safe(description);

                String lower = text.toLowerCase();

                if (lower.contains("ppo") || lower.contains("pre-placement")
                                || lower.contains("pre placement")) {
                        return "PPO";
                }
                if (lower.contains("intern") || lower.contains("internship")) {
                        return "INTERNSHIP";
                }
                if (lower.contains("full-time") || lower.contains("full time")
                                || lower.contains("entry level") || lower.contains("new grad")
                                || lower.contains("graduate engineer trainee")) {
                        return "FULL_TIME";
                }
                return null;
        }

        private String classifyWorkMode(String location, String description) {
                String text = (safe(location) + " " + safe(description)).toLowerCase();
                if (text.contains("hybrid"))
                        return "HYBRID";
                if (text.contains("remote") || text.contains("work from home") || text.contains("wfh"))
                        return "REMOTE";
                if (text.contains("on-site") || text.contains("onsite"))
                        return "ON_SITE";
                return null;
        }

        private String firstNonBlank(String... values) {
                for (String value : values) {
                        if (!isBlank(value))
                                return value.trim();
                }
                return null;
        }

        private boolean isIndiaLocation(
                        String location,
                        String combinedText) {

                String lowerLocation = safe(location).toLowerCase();

                String lowerCombined = safe(combinedText).toLowerCase();

                if (containsAny(
                                lowerLocation,
                                FOREIGN_COUNTRY_KEYWORDS)) {

                        return false;
                }

                for (String indiaKeyword : INDIA_LOCATION_KEYWORDS) {

                        if (lowerLocation.contains(
                                        indiaKeyword.toLowerCase())) {

                                return true;
                        }
                }

                /*
                 * Remote jobs are accepted only when
                 * the job description explicitly connects
                 * the remote opportunity with India.
                 */

                if (lowerLocation.contains("remote")
                                || lowerLocation.contains("anywhere")) {

                        return lowerCombined.contains("india")
                                        || lowerCombined.contains("indian")
                                        || lowerCombined.contains("india-based")
                                        || lowerCombined.contains("based in india");
                }

                return false;
        }

        private boolean containsForeignCountry(
                        String location) {

                String lower = safe(location).toLowerCase();

                return containsAny(
                                lower,
                                FOREIGN_COUNTRY_KEYWORDS);
        }

        private boolean isConsultancy(
                        String company,
                        String title,
                        String description) {

                String companyText = safe(company).toLowerCase();

                String fullText = safe(company) + " "
                                + safe(title) + " "
                                + safe(description);

                String lower = fullText.toLowerCase();

                /*
                 * Company-name check is intentionally
                 * strict for obvious agencies.
                 */

                for (String keyword : CONSULTANCY_KEYWORDS) {

                        if (companyText.contains(keyword)) {
                                return true;
                        }
                }

                /*
                 * Description check catches obvious
                 * recruitment/placement listings.
                 */

                int consultancyMatches = 0;

                for (String keyword : CONSULTANCY_KEYWORDS) {

                        if (lower.contains(keyword)) {
                                consultancyMatches++;
                        }
                }

                return consultancyMatches >= 2;
        }

        private boolean isTrainingProvider(
                        String company,
                        String title,
                        String description) {

                String companyText = safe(company).toLowerCase();

                String fullText = safe(company) + " "
                                + safe(title) + " "
                                + safe(description);

                String lower = fullText.toLowerCase();

                for (String keyword : TRAINING_KEYWORDS) {

                        if (companyText.contains(keyword)) {
                                return true;
                        }
                }

                int trainingMatches = 0;

                for (String keyword : TRAINING_KEYWORDS) {

                        if (lower.contains(keyword)) {
                                trainingMatches++;
                        }
                }

                return trainingMatches >= 2;
        }

        private String extractCompensation(
                        Map<?, ?> job,
                        String description) {

                StringBuilder text = new StringBuilder();

                text.append(
                                safe(description));

                Object detected = job.get("detected_extensions");

                if (detected instanceof Map<?, ?> map) {

                        text.append(" ")
                                        .append(
                                                        map.toString());
                }

                Object extensions = job.get("extensions");

                if (extensions instanceof List<?> list) {

                        for (Object item : list) {

                                text.append(" ")
                                                .append(
                                                                String.valueOf(item));
                        }
                }

                String source = text.toString();

                /*
                 * Indian rupee amounts
                 */

                Pattern rupeePattern = Pattern.compile(
                                "(₹|rs\\.?|inr)\\s*"
                                                + "([0-9][0-9,]*(?:\\.[0-9]+)?)"
                                                + "\\s*"
                                                + "(k|lakh|lac)?"
                                                + "(?:\\s*[-to]+\\s*"
                                                + "(₹|rs\\.?|inr)?\\s*"
                                                + "[0-9][0-9,]*(?:\\.[0-9]+)?"
                                                + "\\s*(k|lakh|lac)?)?"
                                                + "\\s*"
                                                + "(per month|monthly|/month|pm|per annum|annually|yearly|per year|/year)?",
                                Pattern.CASE_INSENSITIVE);

                Matcher rupeeMatcher = rupeePattern.matcher(source);

                if (rupeeMatcher.find()) {

                        String value = rupeeMatcher.group();

                        if (containsMoneyNumber(value)) {
                                return cleanCompensation(value);
                        }
                }

                /*
                 * Plain Indian salary/stipend numbers
                 */

                Pattern salaryPattern = Pattern.compile(
                                "(?:stipend|salary|pay|paid)"
                                                + "\\s*[:\\-]?\\s*"
                                                + "(?:₹|rs\\.?|inr)?\\s*"
                                                + "([0-9][0-9,]*(?:\\.[0-9]+)?)"
                                                + "\\s*"
                                                + "(?:k|lakh|lac)?"
                                                + "\\s*"
                                                + "(per month|monthly|/month|pm|per year|annually|yearly|/year)?",
                                Pattern.CASE_INSENSITIVE);

                Matcher salaryMatcher = salaryPattern.matcher(source);

                if (salaryMatcher.find()) {

                        String value = salaryMatcher.group();

                        if (containsMoneyNumber(value)) {
                                return cleanCompensation(value);
                        }
                }

                /*
                 * Salary range such as:
                 * 10000 - 20000 per month
                 */

                Pattern rangePattern = Pattern.compile(
                                "\\b[0-9][0-9,]*(?:\\.[0-9]+)?"
                                                + "\\s*(?:k|K|lakh|lac)?"
                                                + "\\s*(?:-|to)\\s*"
                                                + "[0-9][0-9,]*(?:\\.[0-9]+)?"
                                                + "\\s*(?:k|K|lakh|lac)?"
                                                + "\\s*(?:per month|monthly|/month|pm|per year|annually|yearly|/year)\\b",
                                Pattern.CASE_INSENSITIVE);

                Matcher rangeMatcher = rangePattern.matcher(source);

                if (rangeMatcher.find()) {

                        String value = rangeMatcher.group();

                        return cleanCompensation(value);
                }

                return null;
        }

        private String extractApplyUrl(
                        Map<?, ?> job) {

                Object applyOptions = job.get("apply_options");

                if (applyOptions instanceof List<?> options) {

                        for (Object optionObject : options) {

                                if (!(optionObject instanceof Map<?, ?> option)) {

                                        continue;
                                }

                                String link = getString(option, "link");

                                if (!isBlank(link)
                                                && link.startsWith("http")) {

                                        return link;
                                }
                        }
                }

                String shareLink = getString(job, "share_link");

                if (!isBlank(shareLink)
                                && shareLink.startsWith("http")) {

                        return shareLink;
                }

                return null;
        }

        private String extractSkills(
                        String title,
                        String description) {

                String text = safe(title) + " "
                                + safe(description);

                List<String> found = new ArrayList<>();

                for (String skill : SKILL_KEYWORDS) {

                        if (containsSkill(
                                        text,
                                        skill)) {

                                if (!found.contains(skill)) {
                                        found.add(skill);
                                }
                        }
                }

                return String.join(", ", found);
        }

        private boolean containsSkill(
                        String text,
                        String skill) {

                String lowerText = safe(text).toLowerCase();

                String lowerSkill = skill.toLowerCase();

                if (skill.equals("C")
                                || skill.equals("AI")) {

                        Pattern pattern = Pattern.compile(
                                        "\\b"
                                                        + Pattern.quote(lowerSkill)
                                                        + "\\b",
                                        Pattern.CASE_INSENSITIVE);

                        return pattern.matcher(lowerText)
                                        .find();
                }

                return lowerText.contains(
                                lowerSkill);
        }

        private boolean containsAny(
                        String text,
                        String[] keywords) {

                String lower = safe(text).toLowerCase();

                for (String keyword : keywords) {

                        if (lower.contains(
                                        keyword.toLowerCase())) {

                                return true;
                        }
                }

                return false;
        }

        private boolean containsMoneyNumber(
                        String text) {

                return text.matches(
                                ".*[0-9].*");
        }

        private String cleanCompensation(
                        String value) {

                return value
                                .replaceAll(
                                                "\\s+",
                                                " ")
                                .trim();
        }

        private String cleanText(
                        String value) {

                if (value == null) {
                        return null;
                }

                return value
                                .replaceAll(
                                                "\\s+",
                                                " ")
                                .trim();
        }

        private String normalize(
                        String value) {

                if (value == null) {
                        return "";
                }

                return value
                                .toLowerCase()
                                .replaceAll(
                                                "[^a-z0-9]",
                                                "");
        }

        private String safe(
                        String value) {

                return value == null
                                ? ""
                                : value;
        }

        private boolean isBlank(
                        String value) {

                return value == null
                                || value.isBlank();
        }

        private String getString(
                        Map<?, ?> map,
                        String key) {

                Object value = map.get(key);

                if (value == null) {
                        return null;
                }

                return String.valueOf(value);
        }
}