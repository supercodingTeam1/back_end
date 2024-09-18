package com.github.supercodingteam1.service;

import com.github.supercodingteam1.repository.category.Category;
import com.github.supercodingteam1.repository.category.CategoryRepository;
import com.github.supercodingteam1.repository.image.Image;
import com.github.supercodingteam1.repository.image.ImageRepository;
import com.github.supercodingteam1.repository.item.Item;
import com.github.supercodingteam1.repository.item.ItemRepository;
import com.github.supercodingteam1.repository.option.Option;
import com.github.supercodingteam1.repository.option.OptionRepository;
import com.github.supercodingteam1.web.dto.AddSellItemDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SellService {
    private static final Logger log = LoggerFactory.getLogger(SellService.class);
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final OptionRepository optionRepository;
    private final ImageRepository imageRepository;

    @Transactional
    public void addSellItem(AddSellItemDTO addSellItemDTO) {
        log.info(addSellItemDTO.toString());

        Category category = categoryRepository.findByCategoryTypeAndCategoryGender(
                addSellItemDTO.getCategory_type(),
                        addSellItemDTO.getCategory_gender()) //리포지토리에서 해당 카테고리가 있으면 가져오고
                .orElse(Category.builder()                   //없으면 카테고리 새로 생성
                        .categoryType(addSellItemDTO.getCategory_type())
                        .categoryGender(addSellItemDTO.getCategory_gender())
                        .build());

        categoryRepository.save(category); //이미 존재하는 카테고리는 저장 안됨

        Item newItem = Item.builder()
                .itemName(addSellItemDTO.getItem_name())
                .itemPrice(addSellItemDTO.getPrice())
                .category(category)
                .description(addSellItemDTO.getDescription())
                .build();

        itemRepository.save(newItem);


        List<Image> images = new ArrayList<>();
        List<String> imageStrings = addSellItemDTO.getItem_image();
        for (String imageString : imageStrings) { //입력한 사진 링크로 Image 객체를 만들어 List에 추가
            Image newImage = Image.builder()
                    .item(newItem)
                    .imageLink(imageString)
                    .build();
            images.add(newImage);
        }
        imageRepository.saveAll(images);
        images.get(0).setImageFirst(true);
        newItem.setImageList(images); //만들어진 List를 Item에 추가

        List<Integer> option_size = addSellItemDTO.getOptions().stream().map(Option::getSize).toList();
        List<Integer> option_stock = addSellItemDTO.getOptions().stream().map(Option::getStock).toList();

        List<Option> options = new ArrayList<>(); //Option List 생성
        for(int i =0; i < option_size.size(); i++){ //입력한 옵션 size와 stock으로 Option 생성하여 optionList에 추가
            Option option = Option.builder()
                    .item(newItem)
                    .size(option_size.get(i))
                    .stock(option_stock.get(i))
                    .build();
            options.add(option);
        }
        optionRepository.saveAll(options);


    }
}
