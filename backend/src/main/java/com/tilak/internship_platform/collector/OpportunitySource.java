package com.tilak.internship_platform.collector;

import com.tilak.internship_platform.entity.Opportunity;
import java.util.List;

public interface OpportunitySource {
    String getSourceName();
    List<Opportunity> fetchOpportunities();
}
