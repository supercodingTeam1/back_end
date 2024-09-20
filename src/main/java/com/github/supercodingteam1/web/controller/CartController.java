package com.github.supercodingteam1.web.controller;

import com.github.supercodingteam1.service.CartService;
import com.github.supercodingteam1.service.ItemService;
import com.github.supercodingteam1.web.dto.*;
import com.github.supercodingteam1.repository.entity.user.User;
import com.github.supercodingteam1.repository.entity.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private static final Logger log = LoggerFactory.getLogger(CartController.class);
    private final ItemService itemService;
    private final CartService cartService;
    private final UserRepository userRepository;

    @Operation(summary = "장바구니에 물품 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 등록했습니다."),
            @ApiResponse(responseCode = "403", description = "권한이 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<?> addItemToCart(HttpServletRequest httpServletRequest, @RequestBody AddToCartDTO addToCartDTO) { //장바구니 담기
        log.info("addItemCart 메소드 호출, {},{}", addToCartDTO.getOption_id(), addToCartDTO.getQuantity());

        cartService.addItemToCart(addToCartDTO);

        return ResponseEntity.ok(ResponseDTO.builder()
                .status(200)
                .message("장바구니에 담았습니다.")
                .build());
    }

    @Operation(summary = "장바구니에 담긴 물품 옵션 or 재고 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 수정했습니다."),
            @ApiResponse(responseCode = "403", description = "권한이 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping
    public ResponseEntity<?> modifyCartItem(HttpServletRequest httpServletRequest, @RequestBody ModifyCartDTO modifyCartDTO) {
        log.info("modifyCartItem 메소드 호출");
        //TODO : 헤더에 담긴 토큰을 파싱해서 유저 누구인지 가져오는 기능 구현 필요
        // httpServletRequest.getHeader("X-AUTH-TOKEN");
        // 임시 유저 생성하여 사용
        User user = userRepository.findById(6).orElse(null);
        cartService.modifyCartItem(modifyCartDTO, user);
        //TODO 받아온 옵션 id와 수량으로 cart 테이블에 있는 quantity 바꾸고, option_cart에 있는 option id 변경
        return ResponseEntity.ok(ResponseDTO.builder()
                .status(200)
                .message("성공적으로 수정되었습니다.")
                .build());
    }

    @Operation(summary = "장바구니에 담긴 물품 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 삭제했습니다."),
            @ApiResponse(responseCode = "403", description = "권한이 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping
    public ResponseEntity<?> deleteCartItem(HttpServletRequest httpServletRequest, @RequestBody DeleteCartDTO deleteCartDTO){
        log.info("deleteCartItem 메소드 호출");
        User user = userRepository.findById(6).orElse(null);

        cartService.deleteCartItem(deleteCartDTO, user);

        return ResponseEntity.ok(ResponseDTO.builder()
                .status(200)
                .message("성공적으로 삭제되었습니다.")
                .build());
    }

    @Operation(summary = "장바구니에 담긴 물품 주문 및 결제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 결제했습니다."),
            @ApiResponse(responseCode = "403", description = "권한이 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/order")
    public ResponseEntity<?> orderCartItem(HttpServletRequest httpServletRequest, @RequestBody OrderDTO orderDTO) {
        cartService.orderCartItem(orderDTO);
        return ResponseEntity.ok(ResponseDTO.builder()
                .status(200)
                .message("성공적으로 주문되었습니다.")
                .build());
    }
}
