package com.github.supercodingteam1.web.dto;

import com.github.supercodingteam1.repository.entity.category.Category;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetAllItemDTO {
    private Integer item_id;
    private String item_name;
    private Integer price;
    private Category category;
    private List<GetAllItemOptionDTO> option;
    private String item_image;
}
