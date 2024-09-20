package com.github.supercodingteam1.repository.entity.option;


import com.github.supercodingteam1.repository.entity.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option,Integer> {
    public List<Option> findAllByItem(Item item);
}
