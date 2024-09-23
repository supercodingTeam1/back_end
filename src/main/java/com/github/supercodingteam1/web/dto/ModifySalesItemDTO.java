package com.github.supercodingteam1.web.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModifySalesItemDTO {
    private Integer optionId;
    private Integer newStock;
}
