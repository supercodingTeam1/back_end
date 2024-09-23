package com.github.supercodingteam1.repository.entity.item;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item,Integer> {
    List<Item> findAllByUser_UserId(@Param("userId") Integer userId);
}
