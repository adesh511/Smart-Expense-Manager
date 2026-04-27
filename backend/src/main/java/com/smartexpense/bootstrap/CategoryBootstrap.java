package com.smartexpense.bootstrap;

import com.smartexpense.entity.Category;
import com.smartexpense.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryBootstrap implements CommandLineRunner {

    private static final List<String> DEFAULT_CATEGORIES = List.of(
            "Food", "Transport", "Shopping", "Bills", "Entertainment", "Health", "Other");

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return;
        }
        for (String name : DEFAULT_CATEGORIES) {
            categoryRepository.save(Category.builder().name(name).build());
        }
    }
}
