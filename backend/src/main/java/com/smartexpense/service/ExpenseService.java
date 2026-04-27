package com.smartexpense.service;

import com.smartexpense.dto.ExpenseDtos;
import com.smartexpense.entity.Category;
import com.smartexpense.entity.Expense;
import com.smartexpense.entity.User;
import com.smartexpense.exception.ApiException;
import com.smartexpense.repository.CategoryRepository;
import com.smartexpense.repository.ExpenseRepository;
import com.smartexpense.repository.UserRepository;
import com.smartexpense.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ExpenseDtos.ExpenseResponse> list(
            LocalDate from, LocalDate to, Long categoryId, int page, int size) {
        Long userId = SecurityUtils.currentUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        return expenseRepository.findByUserFiltered(userId, from, to, categoryId, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public ExpenseDtos.ExpenseResponse create(ExpenseDtos.CreateUpdateRequest request) {
        Long userId = SecurityUtils.currentUserId();
        User user = userRepository.findById(userId).orElseThrow();
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Unknown category"));
        Expense expense = Expense.builder()
                .user(user)
                .category(category)
                .amount(request.getAmount())
                .date(request.getDate())
                .description(request.getDescription())
                .build();
        return toResponse(expenseRepository.save(expense));
    }

    @Transactional
    public ExpenseDtos.ExpenseResponse update(Long id, ExpenseDtos.CreateUpdateRequest request) {
        Long userId = SecurityUtils.currentUserId();
        Expense expense = expenseRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Expense not found"));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Unknown category"));
        expense.setAmount(request.getAmount());
        expense.setCategory(category);
        expense.setDate(request.getDate());
        expense.setDescription(request.getDescription());
        return toResponse(expenseRepository.save(expense));
    }

    @Transactional
    public void delete(Long id) {
        Long userId = SecurityUtils.currentUserId();
        Expense expense = expenseRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Expense not found"));
        expenseRepository.delete(expense);
    }

    private ExpenseDtos.ExpenseResponse toResponse(Expense e) {
        return ExpenseDtos.ExpenseResponse.builder()
                .id(e.getId())
                .amount(e.getAmount())
                .categoryId(e.getCategory().getId())
                .categoryName(e.getCategory().getName())
                .date(e.getDate())
                .description(e.getDescription())
                .build();
    }
}
