package com.github.supercodingteam1.web.controller;

import com.github.supercodingteam1.repository.UserDetails.CustomUserDetails;
import com.github.supercodingteam1.service.SellService;
import com.github.supercodingteam1.web.dto.AddSellItemDTO;
import com.github.supercodingteam1.web.dto.GetAllSalesItemDTO;
import com.github.supercodingteam1.web.dto.ModifySalesItemOptionDTO;
import com.github.supercodingteam1.web.dto.ResponseDTO;
import com.github.supercodingteam1.web.exceptions.NotFoundException;
import com.github.supercodingteam1.web.exceptions.UnauthorizedAccessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sell")
@RequiredArgsConstructor
public class SalesController {
    private static final Logger log = LoggerFactory.getLogger(SalesController.class);
    private final SellService sellService;

    @Operation(summary = "판매 물품 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 조회했습니다."),
            @ApiResponse(responseCode = "403", description = "권한이 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
//    @GetMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @GetMapping
    public ResponseEntity<?> GetAllSellItem(@AuthenticationPrincipal CustomUserDetails userDetails){
        Map<String, Object> result = new HashMap<>();
        log.info("GetAllSellItem 메소드 호출");
        try {
            List<GetAllSalesItemDTO> getAllSalesItemDTOList = sellService.getAllSellItem(userDetails);
            result.put("status", HttpStatus.OK.value());
            result.put("message", "성공적으로 조회했습니다");
            result.put("allSalesItemDTOList", getAllSalesItemDTOList);

            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (NotFoundException nfe){
            result.put("status", HttpStatus.NOT_FOUND.value());
            result.put("message", nfe.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }



    @Operation(summary = "판매 물품 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 등록했습니다."),
            @ApiResponse(responseCode = "403", description = "권한이 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> AddSellItem(@Parameter(description = "상품 이미지 파일 목록", required = true) @RequestPart(required = false, value = "item_image") List<MultipartFile> item_image,
                                      @Parameter(description = "상품 상세 정보", required = true) @RequestPart(required = false, value = "request") AddSellItemDTO addSellItemDTO,
                                      @AuthenticationPrincipal CustomUserDetails customUserDetails){

        System.out.println(item_image);
        System.out.println(addSellItemDTO);

        log.info("AddSellItem 메소드 호출 {}", addSellItemDTO);
        try {

            sellService.addSellItem(item_image,addSellItemDTO, customUserDetails);

            return ResponseEntity.ok().body(ResponseDTO.builder()
                    .status(200)
                    .message("판매물품을 성공적으로 등록하였습니다").
                    build());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.builder()
                    .status(404).message(e.getMessage()).build());
        }
    }

    @Operation(summary = "판매 물품 옵션 재고 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 수정했습니다."),
            @ApiResponse(responseCode = "403", description = "권한이 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping
    public ResponseEntity<?> updateSellItem(
            @Valid @RequestBody List<ModifySalesItemOptionDTO> modifySalesItemOptionDTOList,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        try {
            sellService.updateSellItem(modifySalesItemOptionDTOList, customUserDetails);
            return ResponseEntity.ok().body(ResponseDTO.builder()
                    .status(200)
                    .message("상품 옵션의 재고를 성공적으로 수정하였습니다.")
                    .build());
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder()
                    .status(403)
                    .message(e.getMessage())
                    .build());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.builder()
                    .status(404)
                    .message(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.builder()
                    .status(500)
                    .message("서버 오류가 발생했습니다.")
                    .build());
        }
    }
}
