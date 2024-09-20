package com.github.supercodingteam1.repository.entity.item;

import com.github.supercodingteam1.repository.entity.category.Category;
import com.github.supercodingteam1.repository.entity.image.Image;
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
    @Column(name = "item_id", nullable = false, length = 11)
    private Integer itemId;

    @Column(name = "item_name", nullable = false, length = 50)
    private String itemName;

    @Column(name = "item_price", nullable = false, length = 11)
    private Integer itemPrice;

    @Builder.Default
    @Column(name = "total_sales", nullable = false, length = 11)
    private Integer totalSales = 0;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy="item",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Image> imageList;

    @Override
    public String toString() {
        return "Item{" +
                "itemName='" + itemName + '\'' +
                ",\n itemPrice=" + itemPrice +
                ",\n totalSales=" + totalSales +
                ",\n description='" + description + '\'' +
                ",\n category=" + category +
                ",\n imageList=" + imageList +
                '}';
    }
}
