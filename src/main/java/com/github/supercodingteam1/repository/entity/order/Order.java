package com.github.supercodingteam1.repository.entity.order;

import com.github.supercodingteam1.repository.entity.cart.Cart;
import com.github.supercodingteam1.repository.entity.option.Option;
import com.github.supercodingteam1.repository.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name="order_num",nullable = false)
    private String orderNum;

    @OneToMany
    @JoinColumn(name = "option_id", nullable = false)
    private List<Option> options;

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
