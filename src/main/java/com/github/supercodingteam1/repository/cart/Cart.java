package com.github.supercodingteam1.repository.cart;

import com.github.supercodingteam1.repository.option.Option;
import com.github.supercodingteam1.repository.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "carts")
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id", nullable = false)
    private Integer cartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private List<Option> optionList;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
