package com.github.supercodingteam1.repository.entity.optionCart;

import com.github.supercodingteam1.repository.entity.cart.Cart;
import com.github.supercodingteam1.repository.entity.option.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.supercodingteam1.repository.entity.user.User;

import java.util.List;

@Repository
public interface OptionCartRepository extends JpaRepository<OptionCart,Integer> {
    OptionCart findByOptionAndCart(Option option, Cart existingCart);
    OptionCart findByOptionAndCart_User(Option option, User user);
    OptionCart findByCart(Cart cart);
//    List<OptionCart> findAllByUserId(Integer userId);
    List<OptionCart> findAllByCart_User(User user);

}
