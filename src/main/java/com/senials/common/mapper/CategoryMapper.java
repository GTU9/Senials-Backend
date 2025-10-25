package com.senials.common.mapper;

import com.senials.category.dto.CategoryDTO;
import com.senials.category.dto.CategoryDTOWithHobbies;
import com.senials.category.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    /* Category -> CategoryDTO */
    CategoryDTO toCategoryDTO(Category category);

    /* Category -> CategoryDTOWithHobbies */
    CategoryDTOWithHobbies toCategoryDTOWithHobbies(Category category);
}
