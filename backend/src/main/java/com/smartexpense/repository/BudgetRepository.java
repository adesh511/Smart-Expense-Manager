package com.smartexpense.repository;

import com.smartexpense.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByIdAndUserId(Long id, Long userId);

    Optional<Budget> findByUserIdAndCategoryIdAndBudgetMonth(Long userId, Long categoryId, String budgetMonth);

    @Query("""
            SELECT b FROM Budget b JOIN FETCH b.category WHERE b.user.id = :userId
            ORDER BY b.budgetMonth DESC, b.category.name
            """)
    List<Budget> findAllWithCategoryByUserId(@Param("userId") Long userId);
}
