package com.github.supercodingteam1.web.dto;

import com.github.supercodingteam1.repository.category.Category;
import com.github.supercodingteam1.repository.image.Image;
import com.github.supercodingteam1.repository.option.Option;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetAllItemDTO {
    private Integer item_id;
    private Category category;
    private List<GetAllItemOptionDTO> option;
    private String item_image;
    private String item_name;
    private Integer price;
}
