package com.github.supercodingteam1.repository.entity.orderDetail;

import com.github.supercodingteam1.repository.entity.option.Option;
import com.github.supercodingteam1.repository.entity.order.Order;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.C;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_detail")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Integer orderDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private Option options;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Override
    public String toString() {
        return "OrderDetail{" +
                "options=" + options +
                ", quantity=" + quantity +
                '}';
    }
}
