package com.github.supercodingteam1.service.Utils;

import com.github.supercodingteam1.repository.entity.item.Item;
import com.github.supercodingteam1.repository.entity.option.Option;
import com.github.supercodingteam1.repository.entity.order.Order;
import com.github.supercodingteam1.repository.entity.orderDetail.OrderDetail;
import com.github.supercodingteam1.web.dto.MyBuyInfoDTO;
import com.github.supercodingteam1.web.dto.MyBuyItemDetailDTO;
import com.github.supercodingteam1.web.dto.MyBuyItemOptionDetailDTO;
import com.github.supercodingteam1.web.dto.UserOrderDTO;

import java.util.Collections;
import java.util.List;

public class MyBuyInfoDTOUtils {
    public static MyBuyInfoDTO getBuyInfoDTO(Order order, List<OrderDetail> orderDetailList, List<MyBuyItemDetailDTO> myBuyItemDetailDTOList) {

        for (OrderDetail orderDetail : orderDetailList) {
            Option option = orderDetail.getOptions();  // 주문한 옵션
            Item item = option.getItem();

            // 상품의 대표 이미지 가져오기
            String mainImageUrl = ImageUtils.getMainImageUrl(item);

            // MyBuyItemOptionDetailDTO 생성 (옵션 정보 포함)
            MyBuyItemOptionDetailDTO myBuyItemOptionDetailDTO = MyBuyItemOptionDetailDTO.builder()
                    .option_id(option.getOptionId())
                    .size(option.getSize())
                    .quantity(orderDetail.getQuantity())
                    .build();

            // MyBuyItemDetailDTO 생성 (상품 정보 및 대표 이미지 포함)
            MyBuyItemDetailDTO myBuyItemDetailDTO = MyBuyItemDetailDTO.builder()
                    .order_id(orderDetail.getOrder().getOrder_id())
                    .item_image(mainImageUrl)
                    .item_name(item.getItemName())
                    .price(item.getItemPrice())
                    .myBuyItemOptionDetailDTOList(Collections.singletonList(myBuyItemOptionDetailDTO))
                    .build();

            // MyBuyItemDetailDTO를 목록에 추가
            myBuyItemDetailDTOList.add(myBuyItemDetailDTO);
        }

        // MyBuyInfoDTO 생성 (주문 정보 포함)
        MyBuyInfoDTO myBuyInfoDTO = MyBuyInfoDTO.builder()
                .order_num(order.getOrderNum())
                .order_at(order.getOrderAt())
                .address(order.getOrderAddress())
                .phone_num(order.getPhoneNum())
                .myBuyItemDetailDTOList(myBuyItemDetailDTOList)
                .build();

        return myBuyInfoDTO;
    }

    public static UserOrderDTO getUserOrderDTO(Order order, List<OrderDetail> orderDetailList, List<MyBuyItemDetailDTO> myBuyItemDetailDTOList) {

        for (OrderDetail orderDetail : orderDetailList) {
            Option option = orderDetail.getOptions();
            Item item = option.getItem();

            String mainImageUrl = ImageUtils.getMainImageUrl(item);

            MyBuyItemOptionDetailDTO myBuyItemOptionDetailDTO = MyBuyItemOptionDetailDTO.builder()
                    .option_id(option.getOptionId())
                    .size(option.getSize())
                    .quantity(orderDetail.getQuantity())
                    .build();

            MyBuyItemDetailDTO myBuyItemDetailDTO = MyBuyItemDetailDTO.builder()
                    .order_id(orderDetail.getOrder().getOrder_id())
                    .item_image(mainImageUrl)
                    .item_name(item.getItemName())
                    .price(item.getItemPrice())
                    .myBuyItemOptionDetailDTOList(Collections.singletonList(myBuyItemOptionDetailDTO))
                    .build();

            myBuyItemDetailDTOList.add(myBuyItemDetailDTO);
        }

        return UserOrderDTO.builder()
                .order_num(order.getOrderNum())
                .order_at(order.getOrderAt())
                .address(order.getOrderAddress())
                .phone_num(order.getPhoneNum())
                .order_item(myBuyItemDetailDTOList)
                .build();
    }
}

