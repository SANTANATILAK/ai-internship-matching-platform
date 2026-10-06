package com.tilak.internship_platform.collector;

import com.tilak.internship_platform.entity.*;
import com.tilak.internship_platform.matcher.SkillNormalizer;
import com.tilak.internship_platform.repository.CollectorLogRepository;
import com.tilak.internship_platform.repository.OpportunityRepository;
import com.tilak.internship_platform.verification.OpportunityVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class OpportunityCollectorService {

    private static final Logger logger = LoggerFactory.getLogger(OpportunityCollectorService.class);

    private final List<OpportunitySource> opportunitySources;
    private final OpportunityRepository opportunityRepository;
    private final OpportunityDeduplicationService deduplicationService;
    private final OpportunityVerificationService verificationService;
    private final SkillNormalizer skillNormalizer;
    private final CollectorLogRepository collectorLogRepository;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public OpportunityCollectorService(List<OpportunitySource> opportunitySources,
                                     OpportunityRepository opportunityRepository,
                                     OpportunityDeduplicationService deduplicationService,
                                     OpportunityVerificationService verificationService,
                                     SkillNormalizer skillNormalizer,
                                     CollectorLogRepository collectorLogRepository) {
        this.opportunitySources = opportunitySources;
        this.opportunityRepository = opportunityRepository;
        this.deduplicationService = deduplicationService;
        this.verificationService = verificationService;
        this.skillNormalizer = skillNormalizer;
        this.collectorLogRepository = collectorLogRepository;
    }

    @Transactional
    public synchronized void runCollectionCycle() {
        if (!isRunning.compareAndSet(false, true)) {
            logger.warn("Collection cycle is already executing. Skipping concurrent trigger.");
            return;
        }

        LocalDateTime cycleStart = LocalDateTime.now();
        logger.info("=== STARTING HOURLY OPPORTUNITY COLLECTION CYCLE AT {} ===", cycleStart);

        int totalFound = 0;
        int totalAdded = 0;
        int totalUpdated = 0;
        int totalExpired = 0;
        StringBuilder errorReport = new StringBuilder();

        try {
            // Step 1: Detect and mark expired opportunities
            List<Opportunity> expiredOpps = opportunityRepository.findExpiredOpportunities(LocalDateTime.now());
            for (Opportunity exp : expiredOpps) {
                exp.setStatus(OpportunityStatus.EXPIRED);
                opportunityRepository.save(exp);
                totalExpired++;
            }
            logger.info("Identified and marked {} expired opportunities as EXPIRED", totalExpired);

            // Step 2: Query each registered source connector
            for (OpportunitySource source : opportunitySources) {
                try {
                    List<Opportunity> items = source.fetchOpportunities();
                    totalFound += items.size();

                    for (Opportunity incoming : items) {
                        // 1. Skill Normalization
                        if (incoming.getRequiredSkills() != null) {
                            String[] rawSkills = incoming.getRequiredSkills().split("[,;|]");
                            List<String> normalizedSkills = new ArrayList<>();
                            for (String s : rawSkills) {
                                String n = skillNormalizer.normalize(s.trim());
                                if (!n.isEmpty()) normalizedSkills.add(n);
                            }
                            incoming.setRequiredSkills(String.join(", ", normalizedSkills));
                        }

                        // 2. Verification
                        VerificationStatus vStatus = verificationService.verifyOpportunity(incoming);
                        incoming.setVerificationStatus(vStatus);

                        // 3. Deduplication check
                        Optional<Opportunity> existingOpt = deduplicationService.findExistingDuplicate(incoming);
                        if (existingOpt.isPresent()) {
                            Opportunity existing = existingOpt.get();
                            existing.setLastChecked(LocalDateTime.now());
                            existing.setLastUpdated(LocalDateTime.now());
                            existing.setStipend(incoming.getStipend());
                            existing.setSalary(incoming.getSalary());
                            existing.setRequiredSkills(incoming.getRequiredSkills());
                            existing.setVerificationStatus(vStatus);
                            opportunityRepository.save(existing);
                            totalUpdated++;
                        } else {
                            incoming.setLastChecked(LocalDateTime.now());
                            incoming.setLastUpdated(LocalDateTime.now());
                            opportunityRepository.save(incoming);
                            totalAdded++;
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error collecting from source {}: {}", source.getSourceName(), e.getMessage());
                    errorReport.append("[").append(source.getSourceName()).append(": ").append(e.getMessage()).append("] ");
                }
            }

            // Step 3: Record Collector Log
            CollectorLog log = CollectorLog.builder()
                    .sourceName("Hourly Multi-Source Pipeline")
                    .status(errorReport.length() == 0 ? "SUCCESS" : "PARTIAL")
                    .opportunitiesFound(totalFound)
                    .opportunitiesAdded(totalAdded)
                    .opportunitiesUpdated(totalUpdated)
                    .opportunitiesExpired(totalExpired)
                    .errorMessage(errorReport.length() > 0 ? errorReport.toString() : null)
                    .startedAt(cycleStart)
                    .completedAt(LocalDateTime.now())
                    .build();

            collectorLogRepository.save(log);
            logger.info("=== COLLECTION CYCLE COMPLETE. Added: {}, Updated: {}, Expired: {} ===",
                    totalAdded, totalUpdated, totalExpired);

        } finally {
            isRunning.set(false);
        }
    }

    public boolean isCurrentlyRunning() {
        return isRunning.get();
    }
}
