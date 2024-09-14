package com.github.supercodingteam1.repository.order;

import com.github.supercodingteam1.repository.cart.Cart;
import com.github.supercodingteam1.repository.item.Item;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.boot.archive.scan.spi.ClassFileArchiveEntryHandler;

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

    @Column(name="order_num",nullable = false)
    private Double orderNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(name = "order_at", nullable = false)
    private LocalDateTime orderAt;

    @Column(name = "order_address",nullable = false, length = 200)
    private String orderAddress;
}
