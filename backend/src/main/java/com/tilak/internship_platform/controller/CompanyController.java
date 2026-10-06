package com.tilak.internship_platform.controller;

import com.tilak.internship_platform.dto.response.ApiResponse;
import com.tilak.internship_platform.entity.Company;
import com.tilak.internship_platform.service.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Company>>> getAllCompanies() {
        return ResponseEntity.ok(ApiResponse.success(companyService.getVerifiedCompanies()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Company>> getCompanyById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(companyService.getCompanyById(id)));
    }
}
