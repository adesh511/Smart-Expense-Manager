package com.smartexpense.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/** Holder for nested DTO types only (no Lombok on this empty outer class). */
public final class AnalyticsDtos {

    private AnalyticsDtos() {
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategorySlice {
        private String categoryName;
        private BigDecimal amount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthTotal {
        private String month;
        private BigDecimal total;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardResponse {
        private String referenceMonth;
        private BigDecimal totalForMonth;
        private List<CategorySlice> spendByCategory;
        private List<MonthTotal> lastMonthsTrend;
    }
}
