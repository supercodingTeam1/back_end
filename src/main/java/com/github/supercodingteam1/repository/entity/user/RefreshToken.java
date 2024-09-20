package com.github.supercodingteam1.repository.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "refresh_token") // 테이블 이름 명시
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id", nullable = false) // length 속성 제거
    private Integer refreshTokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "refresh_token", nullable = false, length = 255)
    private String refreshToken;

    @Column(name = "expiration", nullable = false)
    private LocalDateTime expiration;

    public static RefreshToken createRefreshToken(User user, String refreshToken, Long remainingMilliSeconds) {
        return RefreshToken.builder()
                .refreshToken(refreshToken)
                .user(user)
                .expiration(LocalDateTime.now().plusSeconds(remainingMilliSeconds / 1000)) // remainingMilliSeconds를 초 단위로 변환
                .build();
    }

}
