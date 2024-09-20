package com.github.supercodingteam1.web.controller;

import com.github.supercodingteam1.service.SellService;
import com.github.supercodingteam1.web.dto.AddSellItemDTO;
import com.github.supercodingteam1.web.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/sell")
@RequiredArgsConstructor
public class SalesController {
    private static final Logger log = LoggerFactory.getLogger(SalesController.class);
    private final SellService sellService;

    @Operation(summary = "판매 물품 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 등록했습니다."),
            @ApiResponse(responseCode = "403", description = "권한이 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDTO AddSellItem(HttpServletRequest httpServletRequest,
                                   @Parameter(description = "상품 이미지 파일 목록", required = true) @RequestPart(value = "item_image", required = true) List<MultipartFile> item_image,
                                   @Parameter(description = "상품 상세 정보", required = true) @RequestPart(value = "request") AddSellItemDTO addSellItemDTO){

        log.info("AddSellItem 메소드 호출 {}", addSellItemDTO);
        sellService.addSellItem(item_image,addSellItemDTO);

        return ResponseDTO.builder()
                .status(200)
                .message("판매물품을 성공적으로 등록하였습니다").
                build();
    }
}
