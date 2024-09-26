package com.github.supercodingteam1.service.mapper;

import com.github.supercodingteam1.repository.entity.option.Option;
import com.github.supercodingteam1.web.dto.OptionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OptionListToOptionDTOListMapper {
    OptionListToOptionDTOListMapper INSTANCE = Mappers.getMapper(OptionListToOptionDTOListMapper.class);
    List<OptionDTO> OptionListToOptionDTOList(List<Option> option);
}
