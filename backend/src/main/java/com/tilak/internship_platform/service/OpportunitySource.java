package com.tilak.internship_platform.service;

import java.util.List;
import java.util.Set;

import com.tilak.internship_platform.entity.Opportunity;

public interface OpportunitySource {

    String getSourceName();

    List<Opportunity> fetchOpportunities();

    default boolean isConfigured() {
        return true;
    }

    default String getLastFetchFailureMessage() {
        return null;
    }

    default boolean isCompleteSnapshot() {
        return false;
    }

    default Set<String> getCurrentOpportunityUrls() {
        return Set.of();
    }
}