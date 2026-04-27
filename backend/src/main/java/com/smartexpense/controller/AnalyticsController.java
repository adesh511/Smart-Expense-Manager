package com.smartexpense.controller;

import com.smartexpense.dto.AnalyticsDtos;
import com.smartexpense.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public AnalyticsDtos.DashboardResponse dashboard(
            @RequestParam(required = false) String month) {
        return analyticsService.dashboard(month);
    }
}
