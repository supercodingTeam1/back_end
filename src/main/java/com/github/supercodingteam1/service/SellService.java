package com.github.supercodingteam1.service;

import com.github.supercodingteam1.repository.entity.category.Category;
import com.github.supercodingteam1.repository.entity.category.CategoryRepository;
import com.github.supercodingteam1.repository.entity.image.Image;
import com.github.supercodingteam1.repository.entity.image.ImageRepository;
import com.github.supercodingteam1.repository.entity.item.Item;
import com.github.supercodingteam1.repository.entity.item.ItemRepository;
import com.github.supercodingteam1.repository.entity.option.Option;
import com.github.supercodingteam1.repository.entity.option.OptionRepository;
import com.github.supercodingteam1.web.dto.AddSellItemDTO;
import com.github.supercodingteam1.web.dto.OptionDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    private final S3Uploader s3Uploader;


    @Transactional
    public void addSellItem(List<MultipartFile> item_image, AddSellItemDTO addSellItemDTO) {

        Category category = categoryRepository.findByCategoryTypeAndCategoryGender( //카테고리 설정
                        addSellItemDTO.getCategory_type(),
                        addSellItemDTO.getCategory_gender()) //리포지토리에서 해당 카테고리가 있으면 가져오고
                .orElse(Category.builder()                   //없으면 카테고리 새로 생성
                        .categoryType(addSellItemDTO.getCategory_type())
                        .categoryGender(addSellItemDTO.getCategory_gender())
                        .build());

        categoryRepository.save(category); //이미 존재하는 카테고리는 저장 안됨

        Item newItem = Item.builder() //아이템 객체 생성
                .itemName(addSellItemDTO.getItem_name())
                .itemPrice(addSellItemDTO.getPrice())
                .category(category)
                .description(addSellItemDTO.getDescription())
                .build();

        itemRepository.save(newItem);
  
        List<String> imageUrlList = new ArrayList<>();
        List<Image> imageList = new ArrayList<>();

        for(MultipartFile multipartFile : item_image) {
            try {
                imageUrlList.add(s3Uploader.upload(multipartFile, "images"));

            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (String imageUrl : imageUrlList) {
            Image image = Image.builder()
                    .imageLink(imageUrl)
                    .item(newItem)
                    .build();
            imageList.add(image);
        }
        imageList.get(0).setImageFirst(true);
        imageRepository.saveAll(imageList);

        List<Integer> option_size = addSellItemDTO.getOptions().stream().map(OptionDTO::getSize).toList();
        List<Integer> option_stock = addSellItemDTO.getOptions().stream().map(OptionDTO::getStock).toList();

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
