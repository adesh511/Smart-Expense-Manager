package com.smartexpense.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseDtos {

    @Data
    public static class CreateUpdateRequest {
        @NotNull @DecimalMin("0.01")
        private BigDecimal amount;
        @NotNull
        private Long categoryId;
        @NotNull
        private LocalDate date;
        @Size(max = 2000)
        private String description;
        private boolean recurring;
        private String frequency;
        private LocalDate nextRunDate;
        private Boolean active;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExpenseResponse {
        private Long id;
        private BigDecimal amount;
        private Long categoryId;
        private String categoryName;
        private LocalDate date;
        private String description;
        private Long recurringTemplateId;
    }
}
