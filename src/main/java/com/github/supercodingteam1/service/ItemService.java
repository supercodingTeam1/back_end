package com.github.supercodingteam1.service;

import com.github.supercodingteam1.repository.item.Item;
import com.github.supercodingteam1.repository.item.ItemRepository;
import com.github.supercodingteam1.repository.option.OptionRepository;
import com.github.supercodingteam1.service.mapper.OptionToGetAllItemDTOMapper;
import com.github.supercodingteam1.web.dto.GetAllItemDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private static final Logger log = LoggerFactory.getLogger(ItemService.class);
    private final ItemRepository itemRepository;
    private final OptionRepository optionRepository;
    public List<GetAllItemDTO> getAllItems(String sort, String order) { //전체물품조회
        if(sort == null || sort.isEmpty()) {
            return itemRepository.findAll().stream()
                    .map(item -> {
                        return GetAllItemDTO.builder()
                                .item_id(item.getItemId())
                                .item_name(item.getItemName())
                                .item_image(item.getImageList().get(0).getImageLink())
                                .category(item.getCategory())
                                .option(OptionToGetAllItemDTOMapper.INSTANCE.OptionToGetAllItemOptionDTO(optionRepository.findAllByItem(item)))
                                .price(item.getItemPrice())
                                .build();
                    }).toList();
        }else { //request param으로 sort와 order가 들어올때
            if(order.equals("asc")) {
                return itemRepository.findAll().stream()
                        .sorted(Comparator.comparing(Item::getTotalSales))
                        .map(item -> {
                            return GetAllItemDTO.builder()
                                    .item_id(item.getItemId())
                                    .item_name(item.getItemName())
                                    .item_image(item.getImageList().get(0).getImageLink())
                                    .category(item.getCategory())
                                    .option(OptionToGetAllItemDTOMapper.INSTANCE.OptionToGetAllItemOptionDTO(optionRepository.findAllByItem(item)))
                                    .price(item.getItemPrice())
                                    .build();
                        }).toList();
            }
            else{
                return itemRepository.findAll().stream()
                        .sorted(Comparator.comparing(Item::getTotalSales).reversed())
                        .map(item -> {
                            return GetAllItemDTO.builder()
                                    .item_id(item.getItemId())
                                    .item_name(item.getItemName())
                                    .item_image(item.getImageList().get(0).getImageLink())
                                    .category(item.getCategory())
                                    .option(OptionToGetAllItemDTOMapper.INSTANCE.OptionToGetAllItemOptionDTO(optionRepository.findAllByItem(item)))
                                    .price(item.getItemPrice())
                                    .build();
                        }).toList();
            }
        }
    }

    public List<GetAllItemDTO> getAllItemsOrderBySales(String sort, String order) {
        log.info(sort, order);
        return Arrays.asList(new GetAllItemDTO());
    }
}
