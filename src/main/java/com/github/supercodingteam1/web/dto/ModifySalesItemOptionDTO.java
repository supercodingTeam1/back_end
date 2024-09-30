package com.github.supercodingteam1.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Valid
public class ModifySalesItemOptionDTO {
    @NotNull(message = "옵션 ID는 필수입니다.")
    private Integer optionId;

    @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
    private Integer newStock;
}
