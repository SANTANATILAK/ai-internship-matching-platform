package com.tilak.internship_platform.scheduler;

import com.tilak.internship_platform.collector.OpportunityCollectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OpportunityScheduler {

    private static final Logger logger = LoggerFactory.getLogger(OpportunityScheduler.class);
    private final OpportunityCollectorService collectorService;

    public OpportunityScheduler(OpportunityCollectorService collectorService) {
        this.collectorService = collectorService;
    }

    /**
     * Executes automatically every hour at minute 0 in Asia/Kolkata timezone.
     */
    @Scheduled(cron = "${app.scheduler.cron:0 0 * * * ?}", zone = "${app.scheduler.zone:Asia/Kolkata}")
    public void executeHourlyOpportunitySync() {
        logger.info("CRON TRIGGER: Starting scheduled hourly opportunity synchronization at {}", LocalDateTime.now());
        collectorService.runCollectionCycle();
    }
}
