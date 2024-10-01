package com.github.supercodingteam1.repository.entity.user;


import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.repository.query.Param;


@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    public User findByUserId(Integer userId);
    // 사용자 이름을 통해 비관적 락을 걸고 데이터를 조회하는 메서드
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    boolean findExistByUserName(@Param("user_name") String userName);
    User findByUserName(String userName);

    public Optional<User> findByEmail(String email);

    public Boolean existsByUserName(String userName);

    public Boolean existsByEmail(String email);

    public User findByUserNameAndPassword(String userName, String password);
}
