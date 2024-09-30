package com.github.supercodingteam1.repository.entity.option;


import com.github.supercodingteam1.repository.entity.item.Item;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptionRepository extends JpaRepository<Option,Integer> {
    public List<Option> findAllByItem(Item item);

    // 동시성 문제 해결을 위한 비관적 락 사용
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Option o WHERE o.optionId = :optionId")
    Optional<Option> findByOptionId(@Param("optionId") Integer optionId);

}