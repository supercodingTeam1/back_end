package com.github.supercodingteam1.service;

import com.github.supercodingteam1.repository.item.Item;
import com.github.supercodingteam1.repository.item.ItemRepository;
import com.github.supercodingteam1.repository.option.Option;
import com.github.supercodingteam1.repository.option.OptionRepository;
import com.github.supercodingteam1.web.dto.GetAllItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final OptionRepository optionRepository;
    public List<GetAllItemDTO> getAllItems() {
        return itemRepository.findAll().stream()
                .map(item -> {
                     return GetAllItemDTO.builder()
                            .item_id(item.getItemId())
                            .option(optionRepository.findAllByItem(item))
                            .item_name(item.getItemName())
                            .item_image(item.getImageList().get(0))
                            .price(item.getItemPrice())
                            .build();
                }).toList();
    }
}
