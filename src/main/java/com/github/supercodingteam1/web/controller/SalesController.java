package com.github.supercodingteam1.web.controller;

import com.github.supercodingteam1.repository.category.Category;
import com.github.supercodingteam1.repository.image.Image;
import com.github.supercodingteam1.repository.item.Item;
import com.github.supercodingteam1.repository.option.Option;
import com.github.supercodingteam1.service.SellService;
import com.github.supercodingteam1.web.dto.AddSellItemDTO;
import com.github.supercodingteam1.web.dto.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/sell")
@RequiredArgsConstructor
public class SalesController {
    private static final Logger log = LoggerFactory.getLogger(SalesController.class);
    private final SellService sellService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseDTO AddSellItem(HttpServletRequest httpServletRequest,
                                   @RequestPart(value = "item_image", required = true, name = "item_image") List<MultipartFile> item_image,
                                   @RequestPart(value = "request", name = "request") AddSellItemDTO addSellItemDTO){

       sellService.addSellItem(item_image,addSellItemDTO);

        return ResponseDTO.builder()
                .status(200)
                .message("판매물품을 성공적으로 등록하였습니다").
                build();
    }
}
