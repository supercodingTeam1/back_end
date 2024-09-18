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
    private List<Option> options;

    @Override
    public String toString() {
        return "AddSellItemDTO{" +
                "item_name='" + item_name + '\'' +
                ", item_image=" + item_image +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category_type='" + category_type + '\'' +
                ", category_gender='" + category_gender + '\'' +
                ", options size =" + options.stream().map(Option::getSize).toList() +
                ", options stock = " + options.stream().map(Option::getStock).toList() +
                '}';
    }
}
