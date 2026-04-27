package com.smartexpense.repository;

import com.smartexpense.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Optional<Expense> findByIdAndUserId(Long id, Long userId);
    boolean existsByUserIdAndRecurringTemplateIdAndDate(Long userId, Long recurringTemplateId, LocalDate date);

    @Query("""
            SELECT e FROM Expense e
            WHERE e.user.id = :userId
            AND (:from IS NULL OR e.date >= :from)
            AND (:to IS NULL OR e.date <= :to)
            AND (:categoryId IS NULL OR e.category.id = :categoryId)
            """)
    Page<Expense> findByUserFiltered(
            @Param("userId") Long userId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("categoryId") Long categoryId,
            Pageable pageable);

    @Query("""
            SELECT COALESCE(SUM(e.amount), 0) FROM Expense e
            WHERE e.user.id = :userId AND e.date >= :from AND e.date <= :to
            """)
    BigDecimal sumAmountForUserBetween(@Param("userId") Long userId, @Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("""
            SELECT COALESCE(SUM(e.amount), 0) FROM Expense e
            WHERE e.user.id = :userId AND e.category.id = :categoryId
            AND e.date >= :from AND e.date <= :to
            """)
    BigDecimal sumAmountForUserCategoryBetween(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    @Query("""
            SELECT c.name, COALESCE(SUM(e.amount), 0) FROM Expense e
            JOIN e.category c
            WHERE e.user.id = :userId AND e.date >= :from AND e.date <= :to
            GROUP BY c.id, c.name
            ORDER BY 2 DESC
            """)
    List<Object[]> sumByCategoryForUserBetween(@Param("userId") Long userId, @Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query(value = """
            SELECT DATE_FORMAT(e.date, '%Y-%m') AS ym, COALESCE(SUM(e.amount), 0)
            FROM expenses e
            WHERE e.user_id = :userId AND e.date >= :from AND e.date <= :to
            GROUP BY DATE_FORMAT(e.date, '%Y-%m')
            ORDER BY ym
            """, nativeQuery = true)
    List<Object[]> sumByMonthNative(@Param("userId") Long userId, @Param("from") LocalDate from, @Param("to") LocalDate to);
}
