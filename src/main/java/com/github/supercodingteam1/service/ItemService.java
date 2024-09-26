package com.github.supercodingteam1.service;

import com.github.supercodingteam1.repository.entity.category.CategoryRepository;
import com.github.supercodingteam1.repository.entity.image.Image;
import com.github.supercodingteam1.repository.entity.item.Item;
import com.github.supercodingteam1.repository.entity.item.ItemRepository;
import com.github.supercodingteam1.repository.entity.option.Option;
import com.github.supercodingteam1.repository.entity.option.OptionRepository;
import com.github.supercodingteam1.service.mapper.CategoryToCategoryDTOMapper;
import com.github.supercodingteam1.service.mapper.OptionListToOptionDTOListMapper;
import com.github.supercodingteam1.service.mapper.OptionToGetAllItemDTOMapper;
import com.github.supercodingteam1.web.dto.GetAllItemDTO;
import com.github.supercodingteam1.web.dto.ItemDetailDTO;
import com.github.supercodingteam1.web.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private static final Logger log = LoggerFactory.getLogger(ItemService.class);
    private final ItemRepository itemRepository;
    private final OptionRepository optionRepository;
    private final CategoryRepository categoryRepository;


    public Page<GetAllItemDTO> getAllItemsPage(Integer page, Integer size, String sort, String order, Integer optionSize) {
        page -= 1;
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

        Pageable pageable = PageRequest.of(page, size);

        List<Item> filteredItems = itemRepository.findAll().stream()
                .filter(item -> (optionSize == null || hasOptionWithSize(item, optionSize)))
                .filter(this::isStockMoreThanZero)
                .sorted(comparator)
                .toList();

        Integer totalItems = filteredItems.size();
        Integer start = Math.toIntExact(Math.min(page*size, totalItems));
        Integer end = Math.min(start + size, totalItems);

        List<GetAllItemDTO> convertedAllItems = filteredItems.subList(start, end)
                .stream()
                .map(this::convertToGetAllItemDTO)
                .toList();

        return new PageImpl<>(convertedAllItems, PageRequest.of(page, size), totalItems);
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
                .seller_id(item.getUser().getUserId())
                .build();
    }

    public ItemDetailDTO getItemDetail(Integer optionId) {
        Option option=optionRepository.findById(optionId).orElseThrow(()->new NotFoundException("해당되는 option을 찾을 수 없습니다."));
        Integer itemId=option.getItem().getItemId();
        Item item=itemRepository.findById(itemId).orElseThrow(()->new NotFoundException("해당되는 item을 찾을 수 없습니다."));
        List<String> imageList=item.getImageList().stream().map(image -> image.getImageLink()).toList();

        return ItemDetailDTO.builder()
                .item_id(itemId)
                .item_name(item.getItemName())
                .item_images(imageList)
                .price(item.getItemPrice())
                .description(item.getDescription())
                .category(CategoryToCategoryDTOMapper.INSTANCE.categoryToCategoryDTO(item.getCategory()))
                .option(OptionListToOptionDTOListMapper.INSTANCE.OptionListToOptionDTOList(item.getOptionList()))
                .build();

    }

}
