package com.github.supercodingteam1.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyBuyInfoDTO {
    private Integer order_num;
    private LocalDateTime order_at;
    private List<MyBuyItemDetailDTO> myBuyItemDetailDTOList; // 주문 상품 리스트

}
