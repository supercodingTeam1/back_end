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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);
    private final ItemService itemService;
    private final OptionService optionService;

    @GetMapping //물품전체조회
    public ResponseEntity<?> getAllItems(HttpServletRequest httpServletRequest,
                                         @RequestParam(required = false) String sort,
                                         @RequestParam(required = false) String order,
                                         @RequestParam(required = false) Integer size){
        Map<String, Object> responseBody = new HashMap<>();
        log.info("getAllItems 요청");
        List<GetAllItemDTO> getAllItemDTOList = itemService.getAllItems(sort, order, size);

        if(sort.equalsIgnoreCase("sales"))
            responseBody.put("items", getAllItemDTOList.stream().limit(8));
        else
            responseBody.put("items",getAllItemDTOList);

        return ResponseEntity.ok(responseBody);
    }
}
