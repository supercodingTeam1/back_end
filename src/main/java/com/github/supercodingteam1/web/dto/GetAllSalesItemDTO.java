package com.github.supercodingteam1.web.dto;

import com.github.supercodingteam1.repository.entity.option.Option;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetAllSalesItemDTO {
    private Integer item_id;
    private String item_image;
    private String item_name;
    private Integer price;
    private CategoryDTO category;
    private List<OptionDTO> options;

}
