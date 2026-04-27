package com.smartexpense.repository;

import com.smartexpense.entity.RecurringExpense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RecurringExpenseRepository extends JpaRepository<RecurringExpense, Long> {
    List<RecurringExpense> findByActiveTrueAndNextRunDateLessThanEqual(LocalDate date);
}
