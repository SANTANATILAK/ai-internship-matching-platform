package com.tilak.internship_platform.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.tilak.internship_platform.entity.Opportunity;
import com.tilak.internship_platform.repository.OpportunityRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class OpportunitySearchService {

    private final OpportunityRepository repository;
    private final OpportunityEligibilityService eligibilityService;

    public OpportunitySearchService(
            OpportunityRepository repository,
            OpportunityEligibilityService eligibilityService) {
        this.repository = repository;
        this.eligibilityService = eligibilityService;
    }

    public Page<Opportunity> search(
            String query,
            String category,
            String branch,
            Integer graduationYear,
            String workMode,
            Pageable pageable) {

        Specification<Opportunity> specification = (root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("isActive")));
            predicates.add(cb.equal(cb.lower(root.get("country")), "india"));
            predicates.add(cb.equal(root.get("verificationStatus"), "VERIFIED"));
            predicates.add(cb.equal(root.get("opportunityStatus"), "OPEN"));

            if (query != null && !query.isBlank()) {
                String term = "%" + query.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("company")), term),
                        cb.like(cb.lower(root.get("title")), term),
                        cb.like(cb.lower(root.get("description")), term),
                        cb.like(cb.lower(root.get("skills")), term),
                        cb.like(cb.lower(root.get("department")), term)));
            }
            if (workMode != null && !workMode.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("workMode")),
                        "%" + workMode.trim().toLowerCase(Locale.ROOT) + "%"));
            }
            addCategoryPredicate(category, root, cb, predicates);
            return cb.and(predicates.toArray(Predicate[]::new));
        };

        boolean needsEligibilityFilter = graduationYear != null || (branch != null && !branch.isBlank());
        if (!needsEligibilityFilter) {
            return repository.findAll(specification, pageable);
        }

        Pageable scanPage = PageRequest.of(0, 500, Sort.by(Sort.Direction.DESC, "lastUpdatedAt"));
        List<Opportunity> eligible = new ArrayList<>();
        Page<Opportunity> batch;
        do {
            batch = repository.findAll(specification, scanPage);
            for (Opportunity opportunity : batch.getContent()) {
                if (matchesRequestedEligibility(opportunity, branch, graduationYear)) {
                    eligible.add(opportunity);
                }
            }
            scanPage = batch.nextPageable();
        } while (batch.hasNext());

        int from = Math.min(Math.toIntExact(pageable.getOffset()), eligible.size());
        int to = Math.min(from + pageable.getPageSize(), eligible.size());
        return new PageImpl<>(eligible.subList(from, to), pageable, eligible.size());
    }

    private boolean matchesRequestedEligibility(
            Opportunity opportunity,
            String branch,
            Integer graduationYear) {
        if (graduationYear != null
                && (!eligibilityService.graduationYearEligible(
                        opportunity.getGraduationYears(), graduationYear)
                        || !eligibilityService.jobTypeEligible(
                                opportunity.getJobType() == null ? opportunity.getType() : opportunity.getJobType(),
                                graduationYear))) {
            return false;
        }
        return branch == null || branch.isBlank()
                || eligibilityService.branchEligible(opportunity.getBranch(), branch);
    }

    private void addCategoryPredicate(
            String category,
            jakarta.persistence.criteria.Root<Opportunity> root,
            jakarta.persistence.criteria.CriteriaBuilder cb,
            List<Predicate> predicates) {
        if (category == null || category.isBlank()) return;
        String value = category.toLowerCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        var jobType = cb.lower(cb.coalesce(root.get("jobType"), root.get("type")));
        switch (value) {
            case "internship", "internships" -> predicates.add(cb.like(jobType, "%intern%"));
            case "paid_internship", "paid_internships" -> {
                predicates.add(cb.like(jobType, "%intern%"));
                predicates.add(cb.or(
                        cb.and(cb.isNotNull(root.get("stipend")), cb.notEqual(cb.trim(root.get("stipend")), "")),
                        cb.and(cb.isNotNull(root.get("salary")), cb.notEqual(cb.trim(root.get("salary")), ""))));
            }
            case "ppo", "pre_placement", "pre_placement_offer" -> predicates.add(cb.or(
                    cb.like(jobType, "%ppo%"), cb.like(jobType, "%pre_placement%")));
            case "full_time", "fulltime", "jobs" -> predicates.add(cb.or(
                    cb.like(jobType, "%full_time%"), cb.like(jobType, "%full-time%"),
                    cb.like(jobType, "%full time%"), cb.equal(jobType, "job")));
            default -> predicates.add(cb.equal(cb.literal(false), true));
        }
    }
}
