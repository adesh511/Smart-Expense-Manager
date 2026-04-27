package com.smartexpense.service;

import com.smartexpense.entity.Expense;
import com.smartexpense.entity.RecurrenceFrequency;
import com.smartexpense.entity.RecurringExpense;
import com.smartexpense.repository.ExpenseRepository;
import com.smartexpense.repository.RecurringExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecurringExpenseScheduler {

    private final RecurringExpenseRepository recurringExpenseRepository;
    private final ExpenseRepository expenseRepository;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void generateRecurringExpenses() {
        LocalDate today = LocalDate.now();
        List<RecurringExpense> due = recurringExpenseRepository.findByActiveTrueAndNextRunDateLessThanEqual(today);
        for (RecurringExpense template : due) {
            LocalDate cursor = template.getNextRunDate();
            while (!cursor.isAfter(today)) {
                boolean exists = expenseRepository.existsByUserIdAndRecurringTemplateIdAndDate(
                        template.getUser().getId(), template.getId(), cursor);
                if (!exists) {
                    Expense generated = Expense.builder()
                            .user(template.getUser())
                            .category(template.getCategory())
                            .amount(template.getAmount())
                            .description(template.getDescription())
                            .date(cursor)
                            .recurringTemplateId(template.getId())
                            .build();
                    expenseRepository.save(generated);
                }
                cursor = advance(cursor, template.getFrequency());
            }
            template.setNextRunDate(cursor);
        }
    }

    private LocalDate advance(LocalDate from, RecurrenceFrequency frequency) {
        return switch (frequency) {
            case DAILY -> from.plusDays(1);
            case WEEKLY -> from.plusWeeks(1);
            case MONTHLY -> from.plusMonths(1);
        };
    }
}
