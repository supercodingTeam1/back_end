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
    @Column(name = "option_id", nullable = false)
    private Integer optionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id",nullable = false)
    private Item item;

    @Column(name="stock",nullable = false)
    private Integer stock;

    @Column(name="size", nullable = false)
    private String size; //int여도 되지 않을까?
}
