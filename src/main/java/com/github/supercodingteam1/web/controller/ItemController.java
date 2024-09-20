package com.github.supercodingteam1.web.controller;

import com.github.supercodingteam1.service.ItemService;
import com.github.supercodingteam1.web.dto.GetAllItemDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "모든 아이템 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 조회했습니다."),
            @ApiResponse(responseCode = "403", description = "권한이 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping //물품전체조회
    public ResponseEntity<?> getAllItems(HttpServletRequest httpServletRequest,
                                         @Schema(description = "정렬 기준, sales로 설정하면 정렬된 목록에서 상위 8개만 출력", allowableValues = {"sales", "price"}) @RequestParam(required = false) String sort,
                                         @Schema(description = "정렬 순서",allowableValues = {"asc", "desc"})@RequestParam(required = false) String order,
                                         @Schema(description = "사이즈별 필터링")@RequestParam(required = false) Integer size){
        Map<String, Object> responseBody = new HashMap<>();
        log.info("getAllItems 메소드 호출");
        List<GetAllItemDTO> getAllItemDTOList = itemService.getAllItems(sort, order, size);

        if(sort != null && sort.equalsIgnoreCase("sales"))
            responseBody.put("items", getAllItemDTOList.stream().limit(8));
        else
            responseBody.put("items", getAllItemDTOList);

        return ResponseEntity.ok(responseBody);
    }

}
