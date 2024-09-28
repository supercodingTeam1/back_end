package com.github.supercodingteam1.web.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyBuyInfoDTO {
    private String order_num;
    private LocalDateTime order_at;
    private List<MyBuyItemDetailDTO> myBuyItemDetailDTOList; // 주문 상품 리스트

}
