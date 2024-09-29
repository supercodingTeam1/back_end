package com.github.supercodingteam1.web.controller;

import com.github.supercodingteam1.service.ItemService;
import com.github.supercodingteam1.web.dto.GetAllItemDTO;
import com.github.supercodingteam1.web.dto.ItemDetailDTO;
import com.github.supercodingteam1.web.exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


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
    @SecurityRequirement(name = "") //swagger에서 인증 제외
    @GetMapping //물품전체조회
    public ResponseEntity<?> getAllItems(HttpServletRequest httpServletRequest,
                                         @Schema(description = "정렬 기준, sales로 설정하면 정렬된 목록에서 상위 8개만 출력", allowableValues = {"sales", "price"}) @RequestParam(required = false) String sort,
                                         @Schema(description = "정렬 순서",allowableValues = {"asc", "desc"})@RequestParam(required = false) String order,
                                         @Schema(description = "사이즈별 필터링")@RequestParam(required = false) Integer optionSize,
                                         @Schema(description = "페이지 번호", defaultValue = "1")@RequestParam(value = "page", defaultValue = "1") Integer page,
                                         @Schema(description = "한 페이지에 보여질 아이템 수", defaultValue = "10")@RequestParam(value = "size", defaultValue = "10") Integer size){
        Map<String, Object> responseBody = new HashMap<>();
        log.info("getAllItems 메소드 호출");
        try {
            PagedModel<EntityModel<GetAllItemDTO>> getAllItemDTOPage = itemService.getAllItemsPage(page, size, sort, order, optionSize);

            responseBody.put("items", getAllItemDTOPage);

            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "성공적으로 조회했습니다.");
            return ResponseEntity.ok(responseBody);

        }catch (Exception e) {
            responseBody.put("error", e.getMessage());
        }
        return ResponseEntity.ok(responseBody);
    }

    @Operation(summary = "상세 아이템 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 조회했습니다."),
            @ApiResponse(responseCode = "404", description = "해당 아이템을 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @SecurityRequirement(name = "") //swagger에서 인증 제외
    @GetMapping("/detail")
    public ResponseEntity<?> getDetailItem(@RequestParam(required = false) Integer item_id){
        Map<String, Object> responseBody = new HashMap<>();
        try {
            log.info("getDetailItem 메소드 호출");
            ItemDetailDTO getItemDetailDTO = itemService.getItemDetail(item_id);
            responseBody.put("item_detail",getItemDetailDTO);
            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "성공적으로 조회했습니다.");
            return ResponseEntity.ok(responseBody);

        }catch (NotFoundException nfe){
            responseBody.put("status", HttpStatus.NOT_FOUND);
            responseBody.put("message", nfe.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

}
