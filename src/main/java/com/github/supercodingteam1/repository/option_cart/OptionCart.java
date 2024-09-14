package com.github.supercodingteam1.repository.option_cart;

import com.github.supercodingteam1.repository.cart.Cart;
import com.github.supercodingteam1.repository.item.Item;
import com.github.supercodingteam1.repository.option.Option;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "option_cart")
@Entity
public class OptionCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_cart_id", nullable = false, length = 11)
    private Integer option_cart_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private Option option;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cart_id", nullable = false)
    private Cart cart;

}
