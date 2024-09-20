package com.github.supercodingteam1.config.auth.jwt;

import com.github.supercodingteam1.config.auth.dto.TokenDTO;
import com.github.supercodingteam1.repository.entity.user.RefreshToken;
import com.github.supercodingteam1.repository.entity.user.RefreshTokenRepository;
import com.github.supercodingteam1.repository.entity.user.User;
import com.github.supercodingteam1.repository.entity.user.UserRepository;
import com.github.supercodingteam1.web.dto.UserDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;


/**
 * 추가된 라이브러리를 사용해서 JWT를 생성하고 검증하는 컴포넌트
 */
@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class JwtTokenProviderService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;


    @Value("${spring.jwt.token.access-expiration-time}")
    private long accessExpirationTime;

    @Value("${spring.jwt.token.refresh-expiration-time}")
    private long refreshExpirationTime;


    /**
     * 1. 로그인시 접근 토큰 생성
     * @param user
     * @return
     */
    public TokenDTO create(User user){
        long currentTimeMillis = System.currentTimeMillis(); // 현재 시간
        Date expireDate = new Date(currentTimeMillis + accessExpirationTime); // 만료일 설정
        long accessTokenExpires = currentTimeMillis + accessExpirationTime; // accessTokenExpires 값 계산

        //1.접근 토큰 생성   //generateAccessToken  ==>,doGenerateTokenDateInput
        String accessToken = jwtTokenUtil.doGenerateTokenDateInput(user.getUserId(), expireDate);

        //2.갱신토큰 생성후 DB 에 저장
        RefreshToken refreshToken = saveRefreshToken(user);

        return TokenDTO.of(accessToken, refreshToken.getRefreshToken(), accessTokenExpires);
    }




    /**
     * 갱신토큰 생성후 DB 에 저장
     * @param user
     * @return
     */
    private RefreshToken saveRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.createRefreshToken(user,
                jwtTokenUtil.generateRefreshToken(user.getUserId(), refreshExpirationTime), refreshExpirationTime);


        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }




    /**

     * 2. access token + refresh token 재발급 처리
     * @param user
     * @return
     */
    private TokenDTO reissueRefreshToken(User user , RefreshToken getDBRefreshTokenInfo) {
        long currentTimeMillis = System.currentTimeMillis(); // 현재 시간
        Date expireDate = new Date(currentTimeMillis + accessExpirationTime); // 만료일 설정
        long accessTokenExpires = currentTimeMillis + accessExpirationTime; // accessTokenExpires 값 계산

        //access token + refresh token 재발급
        String accessToken = jwtTokenUtil.doGenerateTokenDateInput(user.getUserId(), expireDate);

        //RefreshToken refreshToken = saveRefreshToken(user);
        RefreshToken refreshToken = RefreshToken.createRefreshToken(user,
                jwtTokenUtil.generateRefreshToken(user.getUserId(), refreshExpirationTime), refreshExpirationTime);

        //더티 체킹 업데이트
        getDBRefreshTokenInfo.setRefreshToken(refreshToken.getRefreshToken());

        return TokenDTO.of(accessToken, refreshToken.getRefreshToken(),accessTokenExpires);
    }






    public UserDTO reissue(String refreshToken ) throws  Exception{

        //1.refreshToken 유효성 체크
        String userId = validateAndGetUserId(refreshToken);

        //2.DB 저장된 토큰 정보를 가져온다.
        RefreshToken getDBRefreshTokenInfo = refreshTokenRepository.findByRefreshToken(refreshToken);
        
        //3.DB 에 가져온 값이 null 이 아니면 재발급처리
        if (getDBRefreshTokenInfo!=null) {
            User user = userRepository.findByUserId(Integer.valueOf(userId));

            //access token + refresh token 재발급
            TokenDTO tokenDto = reissueRefreshToken(user, getDBRefreshTokenInfo);
            UserDTO userDTO = UserDTO.of(user);
            userDTO.setToken(tokenDto);
            return userDTO;

        }
        throw new RuntimeException("유효하지 않은 토큰 입니다.");
    }




    /**
     * 토큰 확인
     * @param token
     * @return
     */
    public String validateAndGetUserId(String token) throws  Exception{
        return jwtTokenUtil.validateAndGetUserId(token);
    }


    public void logout(String refreshToken) throws  Exception{
        //1.refreshToken 를 파싱해서 userId 값을 가져온다.
        String userId=validateAndGetUserId(refreshToken);

        //2.DB 에서 삭제 처리
        refreshTokenRepository.deleteByUserUserId(Integer.valueOf(userId));
    }


    /**
     * 접근 토큰으로 회원정보 가져오기
     * @param accessToken
     * @return
     * @throws Exception
     */
    public UserDTO getMember(String accessToken) throws  Exception{
        String userId=validateAndGetUserId(accessToken);
        User user= userRepository.findById(Integer.valueOf(userId)).orElseThrow(EntityNotFoundException::new);
        return  UserDTO.of(user);
    }

}
