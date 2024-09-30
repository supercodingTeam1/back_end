package com.github.supercodingteam1.service.mapper;

import com.github.supercodingteam1.repository.entity.item.Item;
import com.github.supercodingteam1.repository.entity.option.Option;
import com.github.supercodingteam1.web.dto.GetAllItemDTO;
import com.github.supercodingteam1.web.dto.GetAllItemOptionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GetAllItemMapper {
    GetAllItemMapper INSTANCE = Mappers.getMapper(GetAllItemMapper.class);

    @Mapping(target = "item_id", source = "itemId")
    @Mapping(target = "item_name", source = "itemName")
    @Mapping(target = "item_image", expression = "java(item.getImageList() != null && !item.getImageList().isEmpty() ? item.getImageList().get(0).getImageLink() : null)")
    @Mapping(target = "price", source = "itemPrice")
    @Mapping(target = "seller_id", source = "user.userId")
    GetAllItemDTO ItemToGetAllItemDTO(Item item);


}

