package com.tilak.internship_platform.service;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tilak.internship_platform.entity.Opportunity;
import com.tilak.internship_platform.entity.OpportunitySourceSync;
import com.tilak.internship_platform.repository.OpportunitySourceSyncRepository;

@Component
public class OpportunitySourceManager {

    private static final Logger logger = LoggerFactory.getLogger(OpportunitySourceManager.class);

    private final List<OpportunitySource> sources;
    private final OpportunityService opportunityService;
    private final OpportunitySourceSyncRepository syncRepository;

    public OpportunitySourceManager(
            List<OpportunitySource> sources,
            OpportunityService opportunityService,
            OpportunitySourceSyncRepository syncRepository) {
        this.sources = sources;
        this.opportunityService = opportunityService;
        this.syncRepository = syncRepository;
    }

    public List<Opportunity> collectFromAllSources() {

        List<Opportunity> opportunities = new ArrayList<>();

        for (OpportunitySource source : sources) {
            String sourceName = source.getSourceName();
            OpportunitySourceSync sync = syncRepository.findBySourceNameIgnoreCase(sourceName)
                    .orElseGet(() -> {
                        OpportunitySourceSync created = new OpportunitySourceSync();
                        created.setSourceName(sourceName);
                        return created;
                    });
            sync.setLastAttemptAt(LocalDateTime.now());
            sync.setLastFetchedCount(0);
            sync.setLastProcessedCount(0);
            sync.setLastInsertedCount(0);
            sync.setLastUpdatedCount(0);
            sync.setLastFailureMessage(null);

            if (!source.isConfigured()) {
                sync.setStatus("NOT_CONFIGURED");
                sync.setLastFailureMessage("Required API key or permitted feed configuration is not set.");
                syncRepository.save(sync);
                logger.info("Opportunity source {} is not configured", sourceName);
                continue;
            }

            logger.info("Starting opportunity source sync: {}", sourceName);

            try {
                List<Opportunity> sourceOpportunities = source.fetchOpportunities();
                if (sourceOpportunities == null) {
                    throw new IllegalStateException("Source returned a null result set.");
                }
                opportunities.addAll(sourceOpportunities);
                sync.setLastFetchedCount(sourceOpportunities.size());
                String failure = source.getLastFetchFailureMessage();
                if (failure == null || failure.isBlank()) {
                    sync.setStatus("SUCCESS");
                    sync.setLastSuccessfulAt(LocalDateTime.now());
                    if (source.isCompleteSnapshot()) {
                        opportunityService.closeMissingFromSnapshot(
                                sourceName, source.getCurrentOpportunityUrls());
                    }
                } else {
                    sync.setStatus("PARTIAL");
                    sync.setLastFailureMessage(limit(failure));
                }
            } catch (Exception exception) {
                sync.setStatus("FAILED");
                sync.setLastFailureMessage(limit(exception.getClass().getSimpleName()
                        + ": " + exception.getMessage()));
                logger.warn("Opportunity source {} failed: {}", sourceName,
                        exception.getMessage());
            } finally {
                syncRepository.save(sync);
            }
        }

        return opportunities;
    }

    private String limit(String value) {
        if (value == null)
            return null;
        return value.length() > 2000 ? value.substring(0, 2000) : value;
    }
}