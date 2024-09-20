package com.github.supercodingteam1.service;

import com.github.supercodingteam1.repository.entity.item.Item;
import com.github.supercodingteam1.repository.entity.item.ItemRepository;
import com.github.supercodingteam1.repository.entity.option.Option;
import com.github.supercodingteam1.repository.entity.option.OptionRepository;
import com.github.supercodingteam1.service.mapper.OptionToGetAllItemDTOMapper;
import com.github.supercodingteam1.web.dto.GetAllItemDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

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

        //option 중 모든 option에 대한 stock이 0이면 아이템 전체를 안보여주고
        //option 중 일부 option에 대한 stock이 0이면 해당 option만 안보여주게 filtering 구현

        return itemRepository.findAll().stream()
                .filter(item -> (size == null || hasOptionWithSize(item,size)))
                .filter(this::isStockMoreThanZero) //item의 options 중 stock이 모두 0 이면 출력 안되게 filter 적용
                .sorted(comparator)
                .map(this::convertToGetAllItemDTO)
                .toList();
    }

    private boolean isStockMoreThanZero(Item item) {
        return optionRepository.findAllByItem(item).stream()
                .anyMatch(option -> option.getStock() > 0);
    }

    private boolean hasOptionWithSize(Item item, Integer size) {
        return optionRepository.findAllByItem(item).stream()
                .anyMatch(option -> option.getSize().equals(size));
    }

    private GetAllItemDTO convertToGetAllItemDTO(Item item) {
        //item에 해당하는 option 중 재고가 있는 option만 보여지도록
        List<Option> options = optionRepository.findAllByItem(item)
                .stream()
                .filter(option -> option.getStock() > 0)
                .toList();


        return GetAllItemDTO.builder()
                .item_id(item.getItemId())
                .item_name(item.getItemName())
                .item_image(item.getImageList().get(0).getImageLink())
                .category(item.getCategory())
                .option(OptionToGetAllItemDTOMapper.INSTANCE.OptionToGetAllItemOptionDTO(options))
                .price(item.getItemPrice())
                .build();
    }
}
