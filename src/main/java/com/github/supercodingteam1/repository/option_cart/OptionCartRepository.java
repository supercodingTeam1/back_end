package com.github.supercodingteam1.repository.option_cart;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionCartRepository extends JpaRepository<OptionCart,Integer> {
}
