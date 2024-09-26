package com.github.supercodingteam1.web.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddSellItemDTO {
    private String item_name;
    private String description;
    private Integer price;
    private String category_type;
    private String category_gender;
    private List<OptionContentDTO> options;

    @Override
    public String toString() {
        return "AddSellItemDTO{" +
                "item_name='" + item_name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category_type='" + category_type + '\'' +
                ", category_gender='" + category_gender + '\'' +
                ", options size =" + options.stream().map(OptionContentDTO::getSize).toList() +
                ", options stock = " + options.stream().map(OptionContentDTO::getStock).toList() +
                '}';
    }
}
