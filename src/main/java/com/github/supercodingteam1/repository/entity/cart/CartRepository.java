package com.github.supercodingteam1.repository.entity.cart;
import com.github.supercodingteam1.repository.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart,Integer> {
    List<Cart> findAllByUser(User user);
}
