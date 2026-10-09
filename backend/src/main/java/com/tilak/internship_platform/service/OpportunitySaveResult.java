package com.tilak.internship_platform.service;

import com.tilak.internship_platform.entity.Opportunity;

public record OpportunitySaveResult(
        Opportunity opportunity,
        boolean inserted,
        boolean updated) {
}