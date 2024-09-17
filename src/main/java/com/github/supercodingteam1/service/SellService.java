package com.github.supercodingteam1.service;

import com.github.supercodingteam1.repository.category.Category;
import com.github.supercodingteam1.repository.image.Image;
import com.github.supercodingteam1.repository.item.Item;
import com.github.supercodingteam1.repository.item.ItemRepository;
import com.github.supercodingteam1.repository.option.Option;
import com.github.supercodingteam1.repository.option.OptionRepository;
import com.github.supercodingteam1.web.dto.AddSellItemDTO;
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
    private final ItemRepository itemRepository;
    private final OptionRepository optionRepository;

    public void addSellItem(AddSellItemDTO addSellItemDTO) {
        log.info(addSellItemDTO.toString());

        Category category = Category.builder()
                .categoryType(addSellItemDTO.getCategory_type())
                .categoryGender(addSellItemDTO.getCategory_gender())
                .build();

        Item newItem = Item.builder()
                .itemName(addSellItemDTO.getItem_name())
                .itemPrice(addSellItemDTO.getPrice())
                .category(category)
                .description(addSellItemDTO.getDescription())
                .build();


        List<Image> images = new ArrayList<>();
        List<String> imageStrings = addSellItemDTO.getItem_image();
        for(int i=0; i<imageStrings.size(); i++){
            Image newImage = Image.builder()
                    .item(newItem)
                    .imageLink(imageStrings.get(i))
                    .build();
            images.add(newImage);
        }
        images.get(0).setImageFirst(true);
        newItem.setImageList(images);

        List<Integer> option_size = addSellItemDTO.getOption_size();
        List<Integer> option_stock = addSellItemDTO.getOption_stock();

        List<Option> options = new ArrayList<>();

        for(int i =0; i < option_size.size(); i++){
            Option option = Option.builder()
                    .item(newItem)
                    .size(option_size.get(i))
                    .stock(option_stock.get(i))
                    .build();
            options.add(option);
        }

        System.out.println("item : " + newItem);
        System.out.println("options : " + options);
    }
}
