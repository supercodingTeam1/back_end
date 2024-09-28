package com.github.supercodingteam1.web.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyBuyItemOptionDetailDTO {
    private Integer option_id;
    private Integer size;
    private Integer quantity;
}