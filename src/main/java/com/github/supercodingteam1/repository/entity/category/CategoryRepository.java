package com.github.supercodingteam1.repository.entity.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer> {
    Optional<Category> findByCategoryTypeAndCategoryGender(String categoryType, String categoryGender);
}
