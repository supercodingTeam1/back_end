package com.github.supercodingteam1.repository.entity.category;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "category")
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false, length = 11)
    private Integer categoryId;

    @Column(name = "category_type",nullable = false, length = 20)
    private String categoryType;

    @Column(name = "category_gender", nullable = false, length = 20)
    private String categoryGender;

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                "categoryType='" + categoryType + '\'' +
                ",\n categoryGender='" + categoryGender + '\'' +
                '}';
    }
}
