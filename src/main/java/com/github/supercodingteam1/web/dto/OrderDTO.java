package com.github.supercodingteam1.web.dto;

import com.github.supercodingteam1.repository.entity.item.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private List<OrderItemDTO> items;
    private Double total_price;
    private String name;
    private String payment;
    private String address;
    private String phone_num;
    private Boolean isFromCart;
}
