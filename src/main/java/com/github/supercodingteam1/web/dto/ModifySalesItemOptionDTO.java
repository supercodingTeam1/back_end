package com.github.supercodingteam1.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModifySalesItemOptionDTO {
    private Integer optionId;
    private Integer newStock;
}
