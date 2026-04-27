package com.smartexpense.controller;

import com.smartexpense.dto.BudgetDtos;
import com.smartexpense.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public List<BudgetDtos.BudgetResponse> list() {
        return budgetService.listForCurrentUser();
    }

    @PostMapping
    public BudgetDtos.BudgetResponse create(@Valid @RequestBody BudgetDtos.CreateRequest request) {
        return budgetService.create(request);
    }

    @PutMapping("/{id}")
    public BudgetDtos.BudgetResponse update(
            @PathVariable Long id,
            @Valid @RequestBody BudgetDtos.CreateRequest request) {
        return budgetService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        budgetService.delete(id);
    }
}
