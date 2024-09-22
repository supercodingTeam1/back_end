package com.github.supercodingteam1.web.dto;

import com.github.supercodingteam1.repository.entity.image.Image;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDetailDTO {
    private String item_id;
    private String category_type;
    private Integer category_gender;
    private Integer option_size;
    private Integer option_stock;
    private List<Image> item_image;
    private String item_name;
    private Integer price;
    private String description;
}
