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
import com.github.supercodingteam1.web.dto.*;
import com.github.supercodingteam1.web.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


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

  public User getByCredentials(String userEmail, String password) {
    try {
      User user = userRepository.findByEmail(userEmail).orElse(null);
      if (passwordEncoder.matches(password, Objects.requireNonNull(user).getPassword())) {
        return user;
      }else throw new BadRequestException("Invalid password");
    }catch (BadRequestException badRequestException) {
      return null;
    }


  }


  /**
   * 로그아웃처리
   *
   * @param loginDTO
   */
  public void logout(LoginDTO loginDTO) {
    User user = userRepository.findByUserName(loginDTO.getUser_email());
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


  /**
   * 유저 정보 조회
   *
   * @param token
   * @return
   */
  public UserDTO findByTokenUserInfo(String token) {

    try {
      // 1. 토큰을 이용해 사용자 ID 추출
      //log.info("1. 토큰을 이용해 사용");
      String userId = jwtTokenProviderService.validateAndGetUserId(token);
      //log.info("1. 토큰을 이용해 사용자 ID 추출  :   {}" , userId);

      User user = userRepository.findByUserId(Integer.valueOf(userId));

      // 2. 유저 정보를 반환
      return UserDTO.of(user);
    } catch (Exception e) {
      throw new IllegalStateException("유저 정보가 없습니다.");
    }
  }


  public MyPageDTO getMyBuyInfo(CustomUserDetails userDetails) {

    int userId = userDetails.getUserId();

    MyPageDTO<MyBuyInfoDTO> myPageDTO = new MyPageDTO<>();
    List<MyBuyInfoDTO> myBuyInfoDTOList = new ArrayList<>();

    List<Order> myOrderList = orderRepository.findAllByUser_UserId(userId);

    for (Order order : myOrderList) {
      List<MyBuyItemDetailDTO> myBuyItemDetailDTOList = new ArrayList<>();

      // 3. 해당 주문의 OrderDetail 을 조회
      List<OrderDetail> orderDetailList = orderDetailRepository.findByOrder(order);

      for (OrderDetail orderDetail : orderDetailList) {
        Option option = orderDetail.getOptions();  // 주문한 옵션
        Item item = option.getItem();

        // 상품의 대표 이미지 가져오기
        String mainImageUrl = ImageUtils.getMainImageUrl(item);

        // MyBuyItemOptionDetailDTO 생성 (옵션 정보 포함)
        MyBuyItemOptionDetailDTO myBuyItemOptionDetailDTO = MyBuyItemOptionDetailDTO.builder()
                .option_id(option.getOptionId())
                .size(option.getSize())
                .quantity(orderDetail.getQuantity())
                .build();

        // MyBuyItemDetailDTO 생성 (상품 정보 및 대표 이미지 포함)
        MyBuyItemDetailDTO myBuyItemDetailDTO = MyBuyItemDetailDTO.builder()
                .order_id(orderDetail.getOrder().getOrder_id())
                .item_image(mainImageUrl)
                .item_name(item.getItemName())
                .price(item.getItemPrice())
                .myBuyItemOptionDetailDTOList(Collections.singletonList(myBuyItemOptionDetailDTO))
                .build();

        // MyBuyItemDetailDTO를 목록에 추가
        myBuyItemDetailDTOList.add(myBuyItemDetailDTO);
      }

      // MyBuyInfoDTO 생성 (주문 정보 포함)
      MyBuyInfoDTO myBuyInfoDTO = MyBuyInfoDTO.builder()
              .order_num(order.getOrderNum())
              .order_at(order.getOrderAt())
              .address(order.getOrderAddress())
              .phone_num(order.getPhoneNum())
              .myBuyItemDetailDTOList(myBuyItemDetailDTOList)
              .build();

      myBuyInfoDTOList.add(myBuyInfoDTO);
    }

    myPageDTO.setData(myBuyInfoDTOList);

    return myPageDTO;
  }
}