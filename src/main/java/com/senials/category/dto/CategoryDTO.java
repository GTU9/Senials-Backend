package com.senials.category.dto;

import com.senials.hobbyboard.dto.HobbyDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CategoryDTO {

    private int categoryNumber;

    private String categoryName;


    /* AllArgsConstructor */
    public CategoryDTO(int categoryNumber, String categoryName) {
        this.categoryNumber = categoryNumber;
        this.categoryName = categoryName;
    }

}
