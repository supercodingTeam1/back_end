package com.github.supercodingteam1.service.mapper;

import com.github.supercodingteam1.repository.entity.option.Option;
import com.github.supercodingteam1.web.dto.GetAllItemOptionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OptionToGetAllItemDTOMapper {
    OptionToGetAllItemDTOMapper INSTANCE = Mappers.getMapper(OptionToGetAllItemDTOMapper.class);

    @Mapping(source = "optionId", target = "option_id")
    GetAllItemOptionDTO OptionToGetAllItemOptionDTO(Option option);

    List<GetAllItemOptionDTO> OptionToGetAllItemOptionDTO(List<Option> options);
}
