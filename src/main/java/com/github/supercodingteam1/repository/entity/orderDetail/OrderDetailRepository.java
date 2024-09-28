package com.github.supercodingteam1.repository.entity.orderDetail;

import com.github.supercodingteam1.repository.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrder(Order order);

    List<OrderDetail> findAllByOrderId(Integer orderId);
}
