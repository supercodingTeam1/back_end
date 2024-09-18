package com.github.supercodingteam1.web.dto;

import com.github.supercodingteam1.repository.category.Category;
import com.github.supercodingteam1.repository.option.Option;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddSellItemDTO {
    private String item_name;
    private List<String> item_image;
    private String description;
    private Integer price;
    private String category_type;
    private String category_gender;
    private List<Integer> option_size;
    private List<Integer> option_stock;

    @Override
    public String toString() {
        return "AddSellItemDTO{" +
                "item_name='" + item_name + '\'' +
                ", item_image=" + item_image +
                ", description='" + description + '\'' +
                ", category_type='" + category_type + '\'' +
                ", category_gender='" + category_gender + '\'' +
                ", option_size=" + option_size +
                ", option_stock=" + option_stock +
                '}';
    }
}
