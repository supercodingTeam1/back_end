package com.github.supercodingteam1.web.dto;

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
    private String item_name;
    private Image item_image;
    private String category;
    private List<Option> option;
    private Integer price;
}
