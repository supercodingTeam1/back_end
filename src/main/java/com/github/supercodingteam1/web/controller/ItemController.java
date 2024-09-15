package com.github.supercodingteam1.web.controller;

import com.github.supercodingteam1.repository.item.Item;
import com.github.supercodingteam1.repository.option.Option;
import com.github.supercodingteam1.service.ItemService;
import com.github.supercodingteam1.service.OptionService;
import com.github.supercodingteam1.web.dto.GetAllItemDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);
    private final ItemService itemService;
    private final OptionService optionService;

    @GetMapping("/")
    public ResponseEntity<?> getAllItems(HttpServletRequest httpServletRequest){
        Map<String, Object> responseBody = new HashMap<>();
        log.info("getAllItems 요청");
        List<GetAllItemDTO> getAllItemDTOList = itemService.getAllItems();
//        responseBody.put("items", getAllItemDTOList);
//        log.info("getAllItemDTOList: {}", getAllItemDTOList);
        return ResponseEntity.ok(responseBody);
    }
}
