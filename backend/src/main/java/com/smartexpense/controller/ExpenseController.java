package com.smartexpense.controller;

import com.smartexpense.dto.ExpenseDtos;
import com.smartexpense.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    public Page<ExpenseDtos.ExpenseResponse> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return expenseService.list(from, to, categoryId, page, size);
    }

    @PostMapping
    public ExpenseDtos.ExpenseResponse create(@Valid @RequestBody ExpenseDtos.CreateUpdateRequest request) {
        return expenseService.create(request);
    }

    @PutMapping("/{id}")
    public ExpenseDtos.ExpenseResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseDtos.CreateUpdateRequest request) {
        return expenseService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        expenseService.delete(id);
    }
}
