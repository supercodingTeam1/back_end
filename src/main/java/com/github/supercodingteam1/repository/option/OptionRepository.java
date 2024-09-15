package com.github.supercodingteam1.repository.option;


import com.github.supercodingteam1.repository.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option,Integer> {
    public List<Option> findAllByItem(Item item);
}
