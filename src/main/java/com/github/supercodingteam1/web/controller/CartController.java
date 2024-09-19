package com.github.supercodingteam1.web.controller;

import com.github.supercodingteam1.repository.user.User;
import com.github.supercodingteam1.repository.user.UserRepository;
import com.github.supercodingteam1.service.CartService;
import com.github.supercodingteam1.service.ItemService;
import com.github.supercodingteam1.web.dto.AddToCartDTO;
import com.github.supercodingteam1.web.dto.ModifyCartDTO;
import com.github.supercodingteam1.web.dto.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private static final Logger log = LoggerFactory.getLogger(CartController.class);
    private final ItemService itemService;
    private final CartService cartService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> addItemToCart(HttpServletRequest httpServletRequest, @RequestBody AddToCartDTO addToCartDTO) { //장바구니 담기
        log.info("addItemCart 메소드 호출, {},{}", addToCartDTO.getOption_id(), addToCartDTO.getQuantity());

        cartService.addItemToCart(addToCartDTO);
        return ResponseEntity.ok(ResponseDTO.builder()
                .status(200)
                .message("장바구니에 담았습니다.")
                .build());
    }

    @PutMapping
    public ResponseEntity<?> modifyCartItem(HttpServletRequest httpServletRequest, @RequestBody ModifyCartDTO modifyCartDTO) {
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
}
