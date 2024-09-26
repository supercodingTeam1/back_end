package com.github.supercodingteam1.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyBuyItemDetailDTO {
    private Integer order_id;
    private String item_image;
    private String item_name;
    private Integer price;
    private List<MyBuyItemOptionDetailDTO> myBuyItemOptionDetailDTOList;
}
