package com.github.supercodingteam1.web.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyBuyItemDetailDTO {
    private Integer order_id;
    private String item_image;
    private String item_name;
    private Integer price;
    private List<MyBuyItemOptionDetailDTO> myBuyItemOptionDetailDTOList;
}
