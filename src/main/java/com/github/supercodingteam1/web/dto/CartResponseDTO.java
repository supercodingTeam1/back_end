package com.github.supercodingteam1.web.dto;

import com.github.supercodingteam1.repository.entity.category.Category;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDTO {
    private Integer option_id;
    private Integer size;
    private String item_image;
    private String item_name;
    private Integer quantity;
    private Integer price;
    }
