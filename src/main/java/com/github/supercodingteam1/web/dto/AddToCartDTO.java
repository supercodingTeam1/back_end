package com.github.supercodingteam1.web.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddToCartDTO {
    private Integer option_id;
    private Integer quantity;
}
