package com.nithish.finance_dashboard_backend.controller;

import com.nithish.finance_dashboard_backend.dto.DashboardSummaryResponse;
import com.nithish.finance_dashboard_backend.model.FinancialRecord;
import com.nithish.finance_dashboard_backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<DashboardSummaryResponse> getSummary() {
        return ResponseEntity.ok(service.getSummary());
    }

    @GetMapping("/category-summary")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<Map<String, BigDecimal>> getCategorySummary() {
        return ResponseEntity.ok(service.getCategorySummary());
    }

    @GetMapping("/monthly-trends")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<List<Map>> getMonthlyTrends() {
        return ResponseEntity.ok(service.getMonthlyTrends());
    }

    @GetMapping("/recent-activity")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<List<FinancialRecord>> getRecentActivity() {
        return ResponseEntity.ok(service.getRecentActivity());
    }
}
