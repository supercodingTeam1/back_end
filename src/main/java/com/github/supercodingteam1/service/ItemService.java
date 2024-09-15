package com.github.supercodingteam1.service;

import com.github.supercodingteam1.repository.item.ItemRepository;
import com.github.supercodingteam1.repository.option.OptionRepository;
import com.github.supercodingteam1.service.mapper.OptionToGetAllItemDTOMapper;
import com.github.supercodingteam1.web.dto.GetAllItemDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private static final Logger log = LoggerFactory.getLogger(ItemService.class);
    private final ItemRepository itemRepository;
    private final OptionRepository optionRepository;
    public List<GetAllItemDTO> getAllItems() {
        log.info(itemRepository.findAll().stream().toList().toString());

        log.info(optionRepository.findAllByItem(itemRepository.findById(1).orElseThrow(null)).toString());

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
    }
}
