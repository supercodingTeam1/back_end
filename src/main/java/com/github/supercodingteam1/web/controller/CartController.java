package com.github.supercodingteam1.web.controller;

import com.github.supercodingteam1.service.ItemService;
import com.github.supercodingteam1.web.dto.AddToCartDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<?> addItemToCart(@RequestBody AddToCartDTO addToCartDTO) {

        return ResponseEntity.ok().build();
    }
}
