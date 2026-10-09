package com.tilak.internship_platform.service;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tilak.internship_platform.entity.Opportunity;
import com.tilak.internship_platform.repository.OpportunitySourceSyncRepository;

@Component
public class OpportunityCollector {

        private static final Logger logger = LoggerFactory.getLogger(OpportunityCollector.class);

        private final OpportunityService opportunityService;
        private final OpportunitySourceManager sourceManager;
        private final OpportunitySourceSyncRepository syncRepository;

        public OpportunityCollector(
                        OpportunityService opportunityService,
                        OpportunitySourceManager sourceManager,
                        OpportunitySourceSyncRepository syncRepository) {

                this.opportunityService = opportunityService;
                this.sourceManager = sourceManager;
                this.syncRepository = syncRepository;
        }

        public void collectOpportunities() {

                List<Opportunity> opportunities = sourceManager.collectFromAllSources();

                Map<String, int[]> countsBySource = new HashMap<>();

                for (Opportunity opportunity : opportunities) {
                        OpportunitySaveResult result = opportunityService
                                        .saveCollectedOpportunityWithStats(opportunity);
                        int[] counts = countsBySource.computeIfAbsent(
                                        opportunity.getSourceName(), ignored -> new int[3]);
                        counts[0]++;
                        if (result.inserted())
                                counts[1]++;
                        if (result.updated())
                                counts[2]++;
                }

                countsBySource.forEach((sourceName, counts) -> syncRepository.findBySourceNameIgnoreCase(sourceName)
                                .ifPresent(sync -> {
                                        sync.setLastProcessedCount(counts[0]);
                                        sync.setLastInsertedCount(counts[1]);
                                        sync.setLastUpdatedCount(counts[2]);
                                        syncRepository.save(sync);
                                }));
                logger.info("Opportunity sync processed {} records from {} sources",
                                opportunities.size(), countsBySource.size());
        }
}