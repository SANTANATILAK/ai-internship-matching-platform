package com.tilak.internship_platform.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@ConditionalOnProperty(prefix = "opportunities.scheduler", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OpportunityScheduler {

        private static final Logger logger = LoggerFactory.getLogger(OpportunityScheduler.class);

        private final OpportunityService opportunityService;
        private final OpportunityCollector opportunityCollector;

        public OpportunityScheduler(
                        OpportunityService opportunityService,
                        OpportunityCollector opportunityCollector) {

                this.opportunityService = opportunityService;
                this.opportunityCollector = opportunityCollector;
        }

        @Scheduled(initialDelayString = "${opportunities.refresh-initial-delay-ms:0}", fixedDelayString = "${opportunities.refresh-interval-ms:3600000}")
        public void collectOpportunities() {
                logger.info("Hourly opportunity synchronization started");
                try {
                        opportunityCollector.collectOpportunities();
                        opportunityService.expirePastDeadlines();
                        opportunityService.recordRefresh();
                        logger.info("Hourly opportunity synchronization completed; {} verified openings remain active",
                                        opportunityService.getActiveOpportunities().size());
                } catch (Exception exception) {
                        logger.error("Hourly opportunity synchronization failed", exception);
                }
        }
}