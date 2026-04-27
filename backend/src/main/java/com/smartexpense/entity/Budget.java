package com.smartexpense.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "budgets",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "category_id", "budget_month"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "limit_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal limitAmount;

    /** Format: yyyy-MM */
    @Column(name = "budget_month", nullable = false, length = 7)
    private String budgetMonth;
}
