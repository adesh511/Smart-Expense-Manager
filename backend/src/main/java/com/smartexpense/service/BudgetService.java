package com.smartexpense.service;

import com.smartexpense.dto.BudgetDtos;
import com.smartexpense.entity.Budget;
import com.smartexpense.entity.Category;
import com.smartexpense.entity.User;
import com.smartexpense.exception.ApiException;
import com.smartexpense.repository.BudgetRepository;
import com.smartexpense.repository.CategoryRepository;
import com.smartexpense.repository.ExpenseRepository;
import com.smartexpense.repository.UserRepository;
import com.smartexpense.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    @Transactional(readOnly = true)
    public List<BudgetDtos.BudgetResponse> listForCurrentUser() {
        Long userId = SecurityUtils.currentUserId();
        return budgetRepository.findAllWithCategoryByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public BudgetDtos.BudgetResponse create(BudgetDtos.CreateRequest request) {
        Long userId = SecurityUtils.currentUserId();
        YearMonth.parse(request.getBudgetMonth());
        if (budgetRepository.findByUserIdAndCategoryIdAndBudgetMonth(userId, request.getCategoryId(), request.getBudgetMonth()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "Budget already exists for this category and month");
        }
        User user = userRepository.findById(userId).orElseThrow();
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Unknown category"));
        Budget budget = Budget.builder()
                .user(user)
                .category(category)
                .limitAmount(request.getLimitAmount())
                .budgetMonth(request.getBudgetMonth())
                .build();
        return toResponse(budgetRepository.save(budget));
    }

    @Transactional
    public BudgetDtos.BudgetResponse update(Long id, BudgetDtos.CreateRequest request) {
        Long userId = SecurityUtils.currentUserId();
        YearMonth.parse(request.getBudgetMonth());
        Budget budget = budgetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Budget not found"));
        budgetRepository.findByUserIdAndCategoryIdAndBudgetMonth(userId, request.getCategoryId(), request.getBudgetMonth())
                .filter(b -> !b.getId().equals(id))
                .ifPresent(b -> {
                    throw new ApiException(HttpStatus.CONFLICT, "Budget already exists for this category and month");
                });
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Unknown category"));
        budget.setCategory(category);
        budget.setLimitAmount(request.getLimitAmount());
        budget.setBudgetMonth(request.getBudgetMonth());
        return toResponse(budgetRepository.save(budget));
    }

    @Transactional
    public void delete(Long id) {
        Long userId = SecurityUtils.currentUserId();
        Budget budget = budgetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Budget not found"));
        budgetRepository.delete(budget);
    }

    private BudgetDtos.BudgetResponse toResponse(Budget b) {
        YearMonth ym = YearMonth.parse(b.getBudgetMonth());
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        BigDecimal spent = expenseRepository.sumAmountForUserCategoryBetween(
                b.getUser().getId(), b.getCategory().getId(), start, end);
        BigDecimal remaining = b.getLimitAmount().subtract(spent);
        boolean over = spent.compareTo(b.getLimitAmount()) > 0;
        return BudgetDtos.BudgetResponse.builder()
                .id(b.getId())
                .categoryId(b.getCategory().getId())
                .categoryName(b.getCategory().getName())
                .limitAmount(b.getLimitAmount())
                .budgetMonth(b.getBudgetMonth())
                .spentAmount(spent)
                .remainingAmount(remaining)
                .overBudget(over)
                .build();
    }
}
