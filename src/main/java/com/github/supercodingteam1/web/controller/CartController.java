package com.github.supercodingteam1.web.controller;

import com.github.supercodingteam1.repository.UserDetails.CustomUserDetails;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Operation(summary = "장바구니 구매내역 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 조회되었습니다."),
            @ApiResponse(responseCode = "403", description = "권한이 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<?> getAllCartItem(@AuthenticationPrincipal CustomUserDetails userDetails) { //장바구니 조회
        log.info("getAllCartItem 메소드 호출");

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증된 사용자가 아닙니다.");
        }

        cartService.getAllCartItem(userDetails);
        return ResponseEntity.ok(ResponseDTO.builder()
                .status(200)
                .message("장바구니 조회에 성공했습니다.")
                .build());
    }
    @Operation(summary = "장바구니에 물품 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 등록했습니다."),
            @ApiResponse(responseCode = "403", description = "권한이 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<?> addItemToCart(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody AddToCartDTO addToCartDTO) { //장바구니 담기
        //TODO : httpServletRequest에서 토큰 가져와서 user 객체 생성 해야함
        log.info("addItemCart 메소드 호출, {},{}", addToCartDTO.getOption_id(), addToCartDTO.getQuantity());

        cartService.addItemToCart(addToCartDTO, customUserDetails);

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
    public ResponseEntity<?> modifyCartItem(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody ModifyCartDTO modifyCartDTO) {
        log.info("modifyCartItem 메소드 호출");
        //TODO : 헤더에 담긴 토큰을 파싱해서 유저 누구인지 가져오는 기능 구현 필요
        // httpServletRequest.getHeader("X-AUTH-TOKEN");
        // 임시 유저 생성하여 사용

        cartService.modifyCartItem(modifyCartDTO, customUserDetails);
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
    public ResponseEntity<?> deleteCartItem(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody DeleteCartDTO deleteCartDTO){
        log.info("deleteCartItem 메소드 호출");
        User user = userRepository.findById(6).orElse(null);

        cartService.deleteCartItem(deleteCartDTO, customUserDetails);

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
    public ResponseEntity<?> orderItem(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody OrderDTO orderDTO) {
        cartService.orderItem(orderDTO, customUserDetails);
        return ResponseEntity.ok(ResponseDTO.builder()
                .status(200)
                .message("성공적으로 주문되었습니다.")
                .build());
    }
}
