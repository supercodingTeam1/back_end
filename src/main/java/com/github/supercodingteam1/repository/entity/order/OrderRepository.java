package com.github.supercodingteam1.repository.entity.order;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {
    List<Order> findAllByUser_UserId(int userId);

    List<Order> findTop3ByUser_UserIdOrderByOrderAtDesc(int userId);

    List<Order> findAllByUser_UserIdFetchDetails(int userId);
}
