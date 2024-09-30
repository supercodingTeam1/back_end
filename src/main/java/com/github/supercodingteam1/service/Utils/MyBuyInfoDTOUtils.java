package com.github.supercodingteam1.service.Utils;

import com.github.supercodingteam1.repository.entity.item.Item;
import com.github.supercodingteam1.repository.entity.option.Option;
import com.github.supercodingteam1.repository.entity.order.Order;
import com.github.supercodingteam1.repository.entity.orderDetail.OrderDetail;
import com.github.supercodingteam1.web.dto.MyBuyInfoDTO;
import com.github.supercodingteam1.web.dto.MyBuyItemDetailDTO;
import com.github.supercodingteam1.web.dto.MyBuyItemOptionDetailDTO;

import java.util.Collections;
import java.util.List;

public class MyBuyInfoDTOUtils {

    // 공통 메서드
    private static MyBuyItemDetailDTO createItemDetailDTO(OrderDetail orderDetail) {
        Option option = orderDetail.getOptions();
        Item item = option.getItem();

        String mainImageUrl = ImageUtils.getMainImageUrl(item);

        MyBuyItemOptionDetailDTO myBuyItemOptionDetailDTO = MyBuyItemOptionDetailDTO.builder()
                .option_id(option.getOptionId())
                .size(option.getSize())
                .quantity(orderDetail.getQuantity())
                .build();

        return MyBuyItemDetailDTO.builder()
                .order_id(orderDetail.getOrder().getOrder_id())
                .item_image(mainImageUrl)
                .item_name(item.getItemName())
                .price(item.getItemPrice())
                .myBuyItemOptionDetailDTOList(Collections.singletonList(myBuyItemOptionDetailDTO))
                .build();
    }

    public static MyBuyInfoDTO getBuyInfoDTO(Order order, List<OrderDetail> orderDetailList, List<MyBuyItemDetailDTO> myBuyItemDetailDTOList) {
        for (OrderDetail orderDetail : orderDetailList) {
            MyBuyItemDetailDTO myBuyItemDetailDTO = createItemDetailDTO(orderDetail);
            myBuyItemDetailDTOList.add(myBuyItemDetailDTO);
        }

        return buildMyBuyInfoDTO(order, myBuyItemDetailDTOList);
    }

    public static MyBuyInfoDTO getUserOrderDTO(Order order, List<OrderDetail> orderDetailList, List<MyBuyItemDetailDTO> myBuyItemDetailDTOList) {
        for (OrderDetail orderDetail : orderDetailList) {
            MyBuyItemDetailDTO myBuyItemDetailDTO = createItemDetailDTO(orderDetail);
            myBuyItemDetailDTOList.add(myBuyItemDetailDTO);
        }

        return buildMyBuyInfoDTO(order, myBuyItemDetailDTOList);
    }

    // DTO 빌더 메서드
    private static MyBuyInfoDTO buildMyBuyInfoDTO(Order order, List<MyBuyItemDetailDTO> myBuyItemDetailDTOList) {
        return MyBuyInfoDTO.builder()
                .order_num(order.getOrderNum())
                .order_at(order.getOrderAt())
                .address(order.getOrderAddress())
                .phone_num(order.getPhoneNum())
                .myBuyItemDetailDTOList(myBuyItemDetailDTOList)
                .build();
    }
}
