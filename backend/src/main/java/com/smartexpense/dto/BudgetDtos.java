package com.smartexpense.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class BudgetDtos {

    @Data
    public static class CreateRequest {
        @NotNull
        private Long categoryId;
        @NotNull @DecimalMin("0.01")
        private BigDecimal limitAmount;
        @NotBlank
        @Pattern(regexp = "\\d{4}-\\d{2}", message = "budgetMonth must be yyyy-MM")
        private String budgetMonth;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BudgetResponse {
        private Long id;
        private Long categoryId;
        private String categoryName;
        private BigDecimal limitAmount;
        private String budgetMonth;
        private BigDecimal spentAmount;
        private BigDecimal remainingAmount;
        private boolean overBudget;
    }
}
