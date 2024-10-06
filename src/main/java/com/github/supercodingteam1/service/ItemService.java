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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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

    @Autowired
    private final PagedResourcesAssembler<GetAllItemDTO> pagedResourcesAssembler;

//    public PagedModel<EntityModel<GetAllItemDTO>> getAllItemsPage(Integer page, Integer size, String sort, String order, Integer optionSize) throws Exception {
//        try {
//            page -= 1;
//            Comparator<Item> comparator = null;
//
//            if(sort != null && !sort.isEmpty()) {
//                if ("sales".equalsIgnoreCase(sort)) {
//                    comparator = Comparator.comparing(Item::getTotalSales);
//                    comparator = comparator.reversed();
//                }else if("price".equalsIgnoreCase(sort)){
//                    comparator = Comparator.comparing(Item::getItemPrice);
//                }else{ //sort 없으면 등록순
//                    comparator = Comparator.comparing(Item::getItemId);
//                }
//
//                if(order != null && "desc".equalsIgnoreCase(order)){
//                    comparator = comparator.reversed();
//                }
//            }
//
//            //option 중 모든 option에 대한 stock이 0이면 아이템 전체를 안보여주고
//            //option 중 일부 option에 대한 stock이 0이면 해당 option만 안보여주게 filtering 구현
//
//            List<Item> filteredItems = itemRepository.findAll().stream()
//                    .filter(item -> (optionSize == null || hasOptionWithSize(item, optionSize)))
//                    .filter(this::isStockMoreThanZero)
//                    .toList();
//            if(comparator != null){
//                filteredItems = filteredItems.stream().sorted(comparator).toList();
//            }
//
//            if(sort != null && sort.equalsIgnoreCase("sales")){
//                filteredItems = filteredItems.stream().limit(8).toList();
//            }
//
//            Integer totalItems = filteredItems.size();
//            Integer start = Math.toIntExact(Math.min(page*size, totalItems));
//            Integer end = Math.min(start + size, totalItems);
//
//            List<GetAllItemDTO> convertedAllItems = filteredItems.subList(start, end)
//                    .stream()
//                    .map(this::convertToGetAllItemDTO)
//                    .toList();
//            Page<GetAllItemDTO> getAllItemDTOPage = new PageImpl<>(convertedAllItems, PageRequest.of(page, size), totalItems);
//
//            PagedModel<EntityModel<GetAllItemDTO>> getAllItemDTOPagedModel = pagedResourcesAssembler.toModel(getAllItemDTOPage);
//            return getAllItemDTOPagedModel;
//        }catch (Exception e){
//            throw new Exception(e.getMessage());
//        }
//
//    }

    public PagedModel<EntityModel<GetAllItemDTO>> getAllCategoryItemsPage(String categoryGender, String categoryType, Integer page, Integer size, String sort, String order, Integer optionSize) throws Exception {
        try {
            page -= 1; // 페이지 번호를 0부터 시작하도록 조정
            Comparator<Item> comparator = null;

//            // 정렬 기준 설정
//            if(sort != null && !sort.isEmpty()) {
//                if ("sales".equalsIgnoreCase(sort)) {
//                    comparator = Comparator.comparing(Item::getTotalSales).reversed();
//                } else if("price".equalsIgnoreCase(sort)){
//                    comparator = Comparator.comparing(Item::getItemPrice);
//                } else {
//                    comparator = Comparator.comparing(Item::getItemId).reversed();
//                }
//                if(order != null && "desc".equalsIgnoreCase(order)){
//                    comparator = comparator.reversed();
//                }
//            }

            // 정렬 기준 설정
            if(sort != null && !sort.isEmpty()) {
                if ("sales".equalsIgnoreCase(sort)) {
                    comparator = Comparator.comparing(Item::getTotalSales).reversed();  // sales 기준 정렬(판매율높은 순)
                } else if("price".equalsIgnoreCase(sort)){
                    comparator = Comparator.comparing(Item::getItemPrice);   // price 기준 정렬(저가순)
                } else {
                    comparator = Comparator.comparing(Item::getItemId).reversed();  // 기본 정렬: itemId 내림차순
                }
            } else {
                // sort가 없을 경우 기본 정렬: itemId 내림차순
                comparator = Comparator.comparing(Item::getItemId).reversed();
            }

            if(order != null && "asc".equalsIgnoreCase(order)){
                comparator = comparator.reversed();  // asc 요청이 들어오면 정렬 순서를 바꿈
            }

            // 전체 아이템을 필터링
            List<Item> filteredItems = itemRepository.findAll().stream()
                    .filter(item -> {
                        boolean matchesGender = categoryGender == null ||
                                (item.getCategory().getCategoryGender() != null &&
                                        item.getCategory().getCategoryGender().equals(categoryGender));
                        boolean matchesType = categoryType == null ||
                                (item.getCategory().getCategoryType() != null &&
                                        item.getCategory().getCategoryType().equals(categoryType));
                        return matchesGender && matchesType;
                    })
                    .filter(item -> (optionSize == null || hasOptionWithSize(item, optionSize)))
                    .filter(this::isStockMoreThanZero)
                    .toList();

            // 정렬
            if(comparator != null){
                filteredItems = filteredItems.stream().sorted(comparator).toList();
            }

            // sales 기준으로 상위 8개 아이템만 표시
            if(sort != null && sort.equalsIgnoreCase("sales")){
                filteredItems = filteredItems.stream().limit(8).toList();
            }

            // 페이지 계산
            Integer totalItems = filteredItems.size();
            Integer start = Math.max(0, page * size);
            Integer end = Math.min(start + size, totalItems);

            List<GetAllItemDTO> convertedAllItems = filteredItems.subList(start, end)
                    .stream()
                    .map(this::convertToGetAllItemDTO)
                    .toList();

            Page<GetAllItemDTO> getAllItemDTOPage = new PageImpl<>(convertedAllItems, PageRequest.of(page, size), totalItems);
            return pagedResourcesAssembler.toModel(getAllItemDTOPage);

        } catch (Exception e) {
            // 오류 로그 추가
            log.error("Error retrieving category items: ", e);
            throw new Exception(e.getMessage());
        }
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

    public ItemDetailDTO getItemDetail(Integer itemId) {
        if(itemId == null) {
            throw new IllegalArgumentException("item id를 확인해주세요.");
        }
        try {
            Item item=itemRepository.findById(itemId).orElseThrow(()->new NotFoundException("해당되는 item을 찾을 수 없습니다."));
            List<String> imageList=item.getImageList().stream().map(Image::getImageLink).toList();

            return ItemDetailDTO.builder()
                    .item_id(itemId)
                    .item_name(item.getItemName())
                    .item_images(imageList)
                    .price(item.getItemPrice())
                    .description(item.getDescription())
                    .category(CategoryToCategoryDTOMapper.INSTANCE.categoryToCategoryDTO(item.getCategory()))
                    .option(OptionListToOptionDTOListMapper.INSTANCE.OptionListToOptionDTOList(item.getOptionList()))
                    .build();
        }catch (NotFoundException nfe){
            throw
                    new NotFoundException(nfe.getMessage());
        }

    }


}
