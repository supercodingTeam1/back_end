package com.github.supercodingteam1.repository.entity.user;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.repository.query.Param;


@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(@Param("email") String email);
}
