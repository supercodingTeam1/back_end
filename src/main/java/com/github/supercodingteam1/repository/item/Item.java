package com.github.supercodingteam1.repository.item;

import com.github.supercodingteam1.repository.category.Category;
import com.github.supercodingteam1.repository.image.Image;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "items")
@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "price", nullable = false)
    private String price;

    @Column(name = "total_sales", nullable = false)
    private Integer totalSales;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy="item",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Image> imageList;
}