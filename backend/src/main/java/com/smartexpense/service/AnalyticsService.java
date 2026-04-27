package com.smartexpense.service;

import com.smartexpense.dto.AnalyticsDtos;
import com.smartexpense.exception.ApiException;
import com.smartexpense.repository.ExpenseRepository;
import com.smartexpense.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ExpenseRepository expenseRepository;

    @Transactional(readOnly = true)
    public AnalyticsDtos.DashboardResponse dashboard(String monthParam) {
        Long userId = SecurityUtils.currentUserId();
        YearMonth ref = YearMonth.from(LocalDate.now());
        if (monthParam != null && !monthParam.isBlank()) {
            try {
                ref = YearMonth.parse(monthParam.trim());
            } catch (DateTimeParseException e) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid month; use yyyy-MM");
            }
        }
        LocalDate start = ref.atDay(1);
        LocalDate end = ref.atEndOfMonth();

        BigDecimal total = toBigDecimal(expenseRepository.sumAmountForUserBetween(userId, start, end));
        List<AnalyticsDtos.CategorySlice> slices = new ArrayList<>();
        for (Object[] row : expenseRepository.sumByCategoryForUserBetween(userId, start, end)) {
            String name = row[0] != null ? row[0].toString() : "";
            BigDecimal amt = toBigDecimal(row[1]);
            slices.add(AnalyticsDtos.CategorySlice.builder().categoryName(name).amount(amt).build());
        }

        YearMonth fromMonth = ref.minusMonths(5);
        LocalDate trendStart = fromMonth.atDay(1);
        List<AnalyticsDtos.MonthTotal> trend = new ArrayList<>();
        for (Object[] row : expenseRepository.sumByMonthNative(userId, trendStart, end)) {
            String ym = sqlScalarToString(row[0]);
            BigDecimal val = toBigDecimal(row[1]);
            trend.add(AnalyticsDtos.MonthTotal.builder().month(ym).total(val).build());
        }

        return AnalyticsDtos.DashboardResponse.builder()
                .referenceMonth(ref.toString())
                .totalForMonth(total)
                .spendByCategory(slices)
                .lastMonthsTrend(trend)
                .build();
    }

    /**
     * JPQL/native scalars often return Long/Double for aggregates with COALESCE(..., 0), not BigDecimal.
     */
    private static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal bd) {
            return bd;
        }
        if (value instanceof Number n) {
            return BigDecimal.valueOf(n.doubleValue());
        }
        return new BigDecimal(value.toString());
    }

    private static String sqlScalarToString(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof byte[] b) {
            return new String(b, StandardCharsets.UTF_8);
        }
        return value.toString();
    }
}
