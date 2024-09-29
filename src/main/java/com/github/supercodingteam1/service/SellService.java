package com.github.supercodingteam1.service;

import com.github.supercodingteam1.repository.UserDetails.CustomUserDetails;
import com.github.supercodingteam1.repository.entity.category.Category;
import com.github.supercodingteam1.repository.entity.category.CategoryRepository;
import com.github.supercodingteam1.repository.entity.image.Image;
import com.github.supercodingteam1.repository.entity.image.ImageRepository;
import com.github.supercodingteam1.repository.entity.item.Item;
import com.github.supercodingteam1.repository.entity.item.ItemRepository;
import com.github.supercodingteam1.repository.entity.option.Option;
import com.github.supercodingteam1.repository.entity.option.OptionRepository;
import com.github.supercodingteam1.repository.entity.user.User;
import com.github.supercodingteam1.repository.entity.user.UserRepository;
import com.github.supercodingteam1.service.Utils.ImageUtils;
import com.github.supercodingteam1.service.mapper.CategoryToCategoryDTOMapper;
import com.github.supercodingteam1.service.mapper.OptionListToOptionDTOListMapper;
import com.github.supercodingteam1.web.dto.AddSellItemDTO;
import com.github.supercodingteam1.web.dto.GetAllSalesItemDTO;
import com.github.supercodingteam1.web.dto.ModifySalesItemOptionDTO;
import com.github.supercodingteam1.web.dto.OptionDTO;
import com.github.supercodingteam1.web.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellService {
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final OptionRepository optionRepository;
    private final ImageRepository imageRepository;

    private final S3Uploader s3Uploader;
    private final UserRepository userRepository;



    public List<GetAllSalesItemDTO> getAllSellItem(CustomUserDetails userDetails) {
        int userId=userDetails.getUserId();
        List<Item> userSellItems=itemRepository.findAllByUser_UserId(userId);
        if(userSellItems==null || userSellItems.isEmpty()){
            throw new NotFoundException("판매중인 물품이 없습니다.");
        }
        List<GetAllSalesItemDTO> getAllSalesItemDTOList=new ArrayList<>();
        for(Item sellItems: userSellItems){
            GetAllSalesItemDTO getAllSalesItemDTO=GetAllSalesItemDTO.builder()
                    .item_id(sellItems.getItemId())
                    .price(sellItems.getItemPrice())
                    .item_name(sellItems.getItemName())
                    .item_image(ImageUtils.getMainImageUrl(sellItems))
                    .category(CategoryToCategoryDTOMapper.INSTANCE.categoryToCategoryDTO(sellItems.getCategory()))
                    .options(OptionListToOptionDTOListMapper.INSTANCE.OptionListToOptionDTOList(sellItems.getOptionList()))
                    .build();

            getAllSalesItemDTOList.add(getAllSalesItemDTO);
        }
        return getAllSalesItemDTOList;
    }


//    @Transactional
    public void updateSellItem(List<ModifySalesItemOptionDTO> modifySalesItemOptionDTOList) {
        // 각 옵션에 대해 업데이트
        for (ModifySalesItemOptionDTO dto : modifySalesItemOptionDTOList) {
            // 옵션 ID에 해당하는 옵션을 조회
            Option option = optionRepository.findById(dto.getOptionId())
                    .orElseThrow(() -> new NotFoundException("Option not found with id: " + dto.getOptionId()));

            // 재고(stock)를 업데이트
            option.setStock(dto.getNewStock());

            // 변경된 값을 저장
            optionRepository.save(option);

            log.info("Option {}의 재고가 {}로 업데이트되었습니다.", dto.getOptionId(), dto.getNewStock());
        }
    }


    @Transactional
    public void addSellItem(List<MultipartFile> item_image, AddSellItemDTO addSellItemDTO, CustomUserDetails customUserDetails) throws Exception {
        try {
            Category category = categoryRepository.findByCategoryTypeAndCategoryGender( //카테고리 설정
                            addSellItemDTO.getCategory_type(),
                            addSellItemDTO.getCategory_gender()) //리포지토리에서 해당 카테고리가 있으면 가져오고
                    .orElse(Category.builder()                   //없으면 카테고리 새로 생성
                            .categoryType(addSellItemDTO.getCategory_type())
                            .categoryGender(addSellItemDTO.getCategory_gender())
                            .build());

            categoryRepository.save(category); //이미 존재하는 카테고리는 저장 안됨
            User user = userRepository.findByEmail(customUserDetails.getEmail()).orElse(null);

            Item newItem = Item.builder() //아이템 객체 생성
                    .itemName(addSellItemDTO.getItem_name())
                    .itemPrice(addSellItemDTO.getPrice())
                    .category(category)
                    .description(addSellItemDTO.getDescription())
                    .user(user)
                    .build();

            itemRepository.save(newItem);

            List<String> imageUrlList = new ArrayList<>();
            List<Image> imageList = new ArrayList<>();

            for(MultipartFile multipartFile : item_image) {
                try {
                    imageUrlList.add(s3Uploader.upload(multipartFile, "images"));
                }catch (IOException e) {
                    throw new IOException(e.getMessage());
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
        }catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }

    }


}
