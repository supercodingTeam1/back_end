package com.github.supercodingteam1.web.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetAllSalesItemDTO {
    private Integer item_id;
    private String category_gender;
    private String category_type;
    private Integer option_id;
    private Integer size;
    private Integer stock;
    private String item_image;
    private String item_name;
    private Integer price;
}
