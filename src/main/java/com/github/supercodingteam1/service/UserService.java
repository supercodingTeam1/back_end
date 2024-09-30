package com.github.supercodingteam1.service;


import com.github.supercodingteam1.config.auth.jwt.JwtTokenProviderService;
import com.github.supercodingteam1.repository.UserDetails.CustomUserDetails;
import com.github.supercodingteam1.repository.entity.item.Item;
import com.github.supercodingteam1.repository.entity.option.Option;
import com.github.supercodingteam1.repository.entity.order.Order;
import com.github.supercodingteam1.repository.entity.order.OrderRepository;
import com.github.supercodingteam1.repository.entity.orderDetail.OrderDetail;
import com.github.supercodingteam1.repository.entity.orderDetail.OrderDetailRepository;
import com.github.supercodingteam1.repository.entity.user.RefreshTokenRepository;
import com.github.supercodingteam1.repository.entity.user.User;
import com.github.supercodingteam1.repository.entity.user.UserRepository;
import com.github.supercodingteam1.service.Utils.ImageUtils;
import com.github.supercodingteam1.service.Utils.MyBuyInfoDTOUtils;
import com.github.supercodingteam1.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
  private final OrderRepository orderRepository;
  private final OrderDetailRepository orderDetailRepository;
  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;

  private final JwtTokenProviderService jwtTokenProviderService;

  /**
   * 중복된 이메일 체크
   *
   * @param userEmail
   * @return
   */
  public boolean isDuplicateEmail(String userEmail) {
    return userRepository.existsByEmail(userEmail);
  }

  public User getByCredentials(String userName, String password) {
    User user = userRepository.findByEmail(userName).orElse(null);
    if (passwordEncoder.matches(password, user.getPassword())) {
      return user;
    }
    return null;
  }


  /**
   * 로그아웃처리
   *
   * @param loginDTO
   */
  public void logout(LoginDTO loginDTO) {
    User user = userRepository.findByUserName(loginDTO.getUser_name());
    refreshTokenRepository.deleteByUserUserId(user.getUserId());
  }


  /**
   * 회원 탈퇴 처리
   *
   * @param withdrawDTO
   */
  @Transactional
  public void withdraw(WithdrawDTO withdrawDTO, CustomUserDetails customUserDetails) {
    //1.토큰 삭제

    refreshTokenRepository.deleteByUserUserId(customUserDetails.getUserId());

    //2.유저 삭제
    userRepository.deleteById(customUserDetails.getUserId());
  }


//  /**
//   * 유저 정보 조회
//   *
//   * @param token
//   * @return
//   */
//  public UserDTO findByTokenUserInfo(String token) {
//
//    try {
//      // 1. 토큰을 이용해 사용자 ID 추출
//      //log.info("1. 토큰을 이용해 사용");
//      String userId = jwtTokenProviderService.validateAndGetUserId(token);
//      //log.info("1. 토큰을 이용해 사용자 ID 추출  :   {}" , userId);
//
//      User user = userRepository.findByUserId(Integer.valueOf(userId));
//
//      // 2. 유저 정보를 반환
//      return UserDTO.of(user);
//    } catch (Exception e) {
//      throw new IllegalStateException("유저 정보가 없습니다.");
//    }
//  }

  public MyPageDTO<UserInfoAndOrderDTO> getMyUserInfo(CustomUserDetails userDetails) {

    User user=userDetails.getUser();
    MyPageDTO<UserInfoAndOrderDTO> myPageDTO = new MyPageDTO<>();

    // User 정보 구성
    UserInfoDTO userInfoDTO = UserInfoDTO.builder()
            .user_id(user.getUserId())
            .email(user.getEmail())
            .name(user.getUserName())
            .user_address(user.getUserAddress())
            .profile(user.getUserImg())
            .phone_num(user.getPhoneNum())
            .roles(userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()))
            .build();

    // 최신 3개의 주문 정보 가져오기
    List<Order> recentOrders = orderRepository.findTop3ByUser_UserIdOrderByOrderAtDesc(userId);
    List<MyBuyInfoDTO> userOrderDTOList = new ArrayList<>();

    for (Order order : recentOrders) {
      List<MyBuyItemDetailDTO> myBuyItemDetailDTOList = new ArrayList<>();
      List<OrderDetail> orderDetailList = orderDetailRepository.findByOrder(order);

      // MyBuyInfoDTOUtils 사용하여 주문 내역 구성
      MyBuyInfoDTO userOrderDTO = MyBuyInfoDTOUtils.getUserOrderDTO(order, orderDetailList, myBuyItemDetailDTOList);
      userOrderDTOList.add(userOrderDTO);
    }

    UserInfoAndOrderDTO userInfoAndOrderDTO = UserInfoAndOrderDTO.builder()
            .user_info(userInfoDTO)
            .user_order(userOrderDTOList)
            .build();

    myPageDTO.setData(userInfoAndOrderDTO);

    return myPageDTO;
  }


  public MyPageDTO getMyBuyInfo(CustomUserDetails userDetails) {
    int userId = userDetails.getUserId();

    MyPageDTO<MyBuyInfoDTO> myPageDTO = new MyPageDTO<>();
    List<MyBuyInfoDTO> myBuyInfoDTOList = new ArrayList<>();

    List<Order> myOrderList = orderRepository.findAllByUser_UserIdFetchDetails(userId);

    // 각 Order에 대해 MyBuyInfoDTO 생성
    for (Order order : myOrderList) {
      List<MyBuyItemDetailDTO> myBuyItemDetailDTOList = new ArrayList<>();
      List<OrderDetail> orderDetailList = order.getOrderDetails(); // 페치된 OrderDetail 사용
      MyBuyInfoDTO myBuyInfoDTO = MyBuyInfoDTOUtils.getBuyInfoDTO(order, orderDetailList, myBuyItemDetailDTOList);

      myBuyInfoDTOList.add(myBuyInfoDTO);
    }

    myPageDTO.setData((MyBuyInfoDTO) myBuyInfoDTOList);
    return myPageDTO;
  }

}