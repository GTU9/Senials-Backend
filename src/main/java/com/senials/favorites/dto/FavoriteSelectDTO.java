package com.senials.favorites.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoriteSelectDTO{
    private int hobbyNumber;
    private String hobbyName;
    private String categoryName;
    private boolean isFavorite;



    public FavoriteSelectDTO(int hobbyNumber, String hobbyName, String categoryName, boolean isFavorite) {
        this.hobbyNumber = hobbyNumber;
        this.hobbyName = hobbyName;
        this.categoryName = categoryName;
        this.isFavorite = isFavorite;
    }

}
