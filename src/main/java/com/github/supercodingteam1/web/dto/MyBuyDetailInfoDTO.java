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
public class MyBuyDetailInfoDTO {
    private Integer order_id;
    private String item_image;
    private String item_name;
    private Double price;
    private List<OptionDTO> option;  // 옵션 리스트
}
