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
    private Integer item_id;
    private String item_name;
    private List<Image> item_image;
    private Integer price;
    private String description;
    private String category_type;
    private String category_gender;
    private Integer option_size;
    private Integer option_stock;
}
