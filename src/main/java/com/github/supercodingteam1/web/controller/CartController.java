package com.github.supercodingteam1.web.controller;

import com.github.supercodingteam1.service.CartService;
import com.github.supercodingteam1.service.ItemService;
import com.github.supercodingteam1.web.dto.AddToCartDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private static final Logger log = LoggerFactory.getLogger(CartController.class);
    private final ItemService itemService;
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<?> addItemToCart(HttpServletRequest httpServletRequest, @RequestBody AddToCartDTO addToCartDTO) { //장바구니 담기
        log.info("addItemCart 메소드 호출, {},{}", addToCartDTO.getOption_id(), addToCartDTO.getQuantity());

        cartService.addItemToCart(addToCartDTO);
        return ResponseEntity.ok().build();
    }
}
