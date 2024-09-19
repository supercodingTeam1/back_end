package com.github.supercodingteam1.repository.option_cart;

import com.github.supercodingteam1.repository.cart.Cart;
import com.github.supercodingteam1.repository.option.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.supercodingteam1.repository.cart.Cart;
import com.github.supercodingteam1.repository.option.Option;
import com.github.supercodingteam1.repository.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptionCartRepository extends JpaRepository<OptionCart,Integer> {
    OptionCart findByOptionAndCart(Option option, Cart existingCart);
}
