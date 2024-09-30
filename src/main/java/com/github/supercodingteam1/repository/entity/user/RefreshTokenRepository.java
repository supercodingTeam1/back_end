package com.github.supercodingteam1.repository.entity.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository  extends JpaRepository<RefreshToken, Integer> {


    // user의 id를 기반으로 refresh token을 삭제
    public void deleteByUserUserId(Integer userId);

    RefreshToken findByUserUserId(Integer user);

    RefreshToken findByRefreshToken(String refreshToken);

}
