package com.senials.category.controller;

import com.senials.category.dto.CategoryDTO;
import com.senials.category.dto.CategoryDTOWithHobbies;
import com.senials.category.service.CategoryService;
import com.senials.common.ResponseMessage;
import com.senials.config.HttpHeadersFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CategoryController {

    private final HttpHeadersFactory httpHeadersFactory;

    private final CategoryService categoryService;


    public CategoryController(
            CategoryService categoryService
            , HttpHeadersFactory httpHeadersFactory
    ) {
        this.categoryService = categoryService;
        this.httpHeadersFactory = httpHeadersFactory;
    }


    @GetMapping("/categories")
    public ResponseEntity<ResponseMessage> getAllCategories(
            @RequestParam(required = false, defaultValue = "false") boolean includeHobby
            , @RequestParam(required = false, defaultValue = "false") boolean asRandom
    ) {

        Map<String, Object> responseMap = new HashMap<>();
        if(!includeHobby){
            List<CategoryDTO> categoryDTOList = null;
            if(asRandom){
                categoryDTOList = categoryService.getAllCategoriesRand();
            } else {
                categoryDTOList = categoryService.getAllCategories();
            }
            responseMap.put("categories", categoryDTOList);
        } else {
            List<CategoryDTOWithHobbies> categoryDTOList = categoryService.getAllCategoriesWithHobbies();
            responseMap.put("categories", categoryDTOList);
        }

        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "카테고리 전체 조회 성공", responseMap));
    }

}
