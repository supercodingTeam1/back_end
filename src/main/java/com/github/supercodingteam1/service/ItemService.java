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

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemService {
    private static final Logger log = LoggerFactory.getLogger(ItemService.class);
    private final ItemRepository itemRepository;
    private final OptionRepository optionRepository;

    public List<GetAllItemDTO> getAllItems(String sort, String order, Integer size) { //전체물품조회
        Comparator<Item> comparator;

        if ("sales".equalsIgnoreCase(sort)) {
            comparator = Comparator.comparing(Item::getTotalSales);
        }else if("price".equalsIgnoreCase(sort)){
            comparator = Comparator.comparing(Item::getItemPrice);
        }else{ //sort 없으면 등록순
            comparator = Comparator.comparing(Item::getItemId);
        }

        if("desc".equalsIgnoreCase(order)){
            comparator = comparator.reversed();
        }

        return itemRepository.findAll().stream()
                .filter(item -> (size == null || hasOptionWithSize(item,size)))
                .sorted(comparator)
                .map(this::convertToGetAllItemDTO)
                .toList();
    }

    private boolean hasOptionWithSize(Item item, Integer size) {
        return optionRepository.findAllByItem(item).stream()
                .anyMatch(option -> option.getSize().equals(size));
    }

    private GetAllItemDTO convertToGetAllItemDTO(Item item) {
        return GetAllItemDTO.builder()
                .item_id(item.getItemId())
                .item_name(item.getItemName())
                .item_image(item.getImageList().get(0).getImageLink())
                .category(item.getCategory())
                .option(OptionToGetAllItemDTOMapper.INSTANCE.OptionToGetAllItemOptionDTO(optionRepository.findAllByItem(item)))
                .price(item.getItemPrice())
                .build();
}
