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
public class CategoryDTOWithHobbies {

    private int categoryNumber;

    private String categoryName;

    private List<HobbyDTO> hobbies;

    /* AllArgsConstructor */
    public CategoryDTOWithHobbies(int categoryNumber, String categoryName, List<HobbyDTO> hobbies) {
        this.categoryNumber = categoryNumber;
        this.categoryName = categoryName;
        this.hobbies = hobbies;
    }
}
