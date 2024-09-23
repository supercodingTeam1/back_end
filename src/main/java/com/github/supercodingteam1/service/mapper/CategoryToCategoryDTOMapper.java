package com.github.supercodingteam1.service.mapper;

import com.github.supercodingteam1.repository.entity.category.Category;
import com.github.supercodingteam1.web.dto.CategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryToCategoryDTOMapper {
    CategoryToCategoryDTOMapper INSTANCE = Mappers.getMapper(CategoryToCategoryDTOMapper.class);
    @Mapping(source = "categoryGender", target = "category_gender")
    @Mapping(source = "categoryType", target = "category_type")
    CategoryDTO categoryToCategoryDTO(Category category);
}
