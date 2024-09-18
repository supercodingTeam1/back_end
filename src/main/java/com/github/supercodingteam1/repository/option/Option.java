package com.github.supercodingteam1.repository.option;

import com.github.supercodingteam1.repository.item.Item;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "options")
@Entity
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id", nullable = false, length = 11)
    private Integer optionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id",nullable = false)
    private Item item;

    @Column(name="option_stock",nullable = false, length = 11)
    private Integer stock;

    @Column(name="size", nullable = false, length = 11)
    private Integer size;

    @Override
    public String toString() {
        return "Option{" +
                "item=" + item.getItemName() +
                ", stock=" + stock +
                ", size=" + size +
                '}';
    }
}
