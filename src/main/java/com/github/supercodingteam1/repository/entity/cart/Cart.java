package com.github.supercodingteam1.repository.entity.cart;

import com.github.supercodingteam1.repository.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cart")
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id", nullable = false, length = 11)
    private Integer cartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(name = "cart_quantity", nullable = false, length = 11)
    private Integer cartQuantity;


    @Override
    public String toString() {
        return "Cart{" +
                "cartId=" + cartId +
                ", user=" + user.getUserId() +
                ", cartQuantity=" + cartQuantity +
                '}';
    }
}
