package com.github.supercodingteam1.repository.entity.order;

import com.github.supercodingteam1.repository.entity.cart.Cart;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false, length = 11)
    private Integer order_id;

    @Column(name = "user_id", nullable = false)
    private Integer user_id;

    @Column(name="order_num",nullable = false)
    private String orderNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(name = "order_at", nullable = false)
    private LocalDateTime orderAt;

    @Column(name = "order_address",nullable = false, length = 200)
    private String orderAddress;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "payment", nullable = false, length = 20)
    private String payment;

    @Column(name = "phone_num", nullable = false, length = 15)
    private String phoneNum;
}
