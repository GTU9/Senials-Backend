package com.senials.category.service;

import com.senials.category.dto.CategoryDTO;
import com.senials.category.dto.CategoryDTOWithHobbies;
import com.senials.category.entity.Category;
import com.senials.category.repository.CategoryRepository;
import com.senials.common.mapper.CategoryMapper;
import com.senials.common.mapper.CategoryMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryService(
            CategoryRepository categoryRepository
            , CategoryMapperImpl categoryMapperImpl
    ) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapperImpl;
    }

    public List<CategoryDTO> getAllCategories() {

        List<Category> categoryList = categoryRepository.findAll();

        List<CategoryDTO> categoryDTOList = categoryList.stream()
                .map(categoryMapper::toCategoryDTO)
                .toList();

        return categoryDTOList;
    }


    public List<CategoryDTOWithHobbies> getAllCategoriesWithHobbies() {

        List<Category> categoryList = categoryRepository.findAll();

        List<CategoryDTOWithHobbies> getCategoryDTOWithHobbies = categoryList.stream()
                .map(categoryMapper::toCategoryDTOWithHobbies)
                .toList();

        return getCategoryDTOWithHobbies;

    }

    public List<CategoryDTO> getAllCategoriesRand() {

        List<Category> categoryList = categoryRepository.findAllByRandom();

        List<CategoryDTO> categoryDTOList = categoryList.stream().map(category -> {
            CategoryDTO categoryDTO = categoryMapper.toCategoryDTO(category);
            return categoryDTO;
        }).toList();

        return categoryDTOList;
    }
}
