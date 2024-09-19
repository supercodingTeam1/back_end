package com.github.supercodingteam1.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModifyCartDTO {
    private Integer option_id;
    private Integer quantity;
}
