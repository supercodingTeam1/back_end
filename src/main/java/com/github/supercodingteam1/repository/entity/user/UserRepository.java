package com.github.supercodingteam1.repository.entity.user;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.repository.query.Param;


@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    public User findByUserId(Integer userId);

    public User findByUserName(String userName);

    public Optional<User> findByEmail(String email);

    public Boolean existsByUserName(String userName);

    public Boolean existsByEmail(String email);

    public User findByUserNameAndPassword(String userName, String password);
}
