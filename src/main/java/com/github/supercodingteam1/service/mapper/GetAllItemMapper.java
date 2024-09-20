package com.github.supercodingteam1.service.mapper;

import com.github.supercodingteam1.repository.entity.item.Item;
import com.github.supercodingteam1.web.dto.GetAllItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GetAllItemMapper {
    GetAllItemMapper INSTANCE = Mappers.getMapper(GetAllItemMapper.class);

    @Mapping(target = "item_id", source = "itemId")
    @Mapping(target = "item_name", source = "itemName")
//    @Mapping(target = "item_image", source = "imageList.get(0)")
    GetAllItemDTO ItemToGetAllItemDTO(Item item);
}

