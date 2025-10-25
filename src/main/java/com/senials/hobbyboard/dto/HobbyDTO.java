package com.senials.hobbyboard.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class HobbyDTO {

    private int hobbyNumber;       // 취미 번호
    private int categoryNumber;    // 카테고리 번호
    private String categoryName;    // 카테고리 네임
    private String hobbyName;      // 취미 이름
    private String hobbyExplain;   // 취미 설명
    private String hobbyImg;       // 취미 이미지 경로
    private int hobbyAbility;      // 취미 요구 능력치
    private int hobbyBudget;       // 취미 예산
    private int hobbyLevel;        // 취미 난이도
    private int hobbyTendency;     // 취미 성향
    private double rating;          // 취미 평균 평점
    private int reviewCount;         // 취미 후기 개수

    public HobbyDTO(int hobbyNumber,
                    int categoryNumber,
                    String hobbyName,
                    String hobbyExplain,
                    String hobbyImg,
                    int hobbyAbility,
                    int hobbyBudget,
                    int hobbyLevel,
                    int hobbyTendency,
                    double rating,
                    int reviewCount) {
        this.hobbyNumber = hobbyNumber;
        this.categoryNumber = categoryNumber;
        this.hobbyName = hobbyName;
        this.hobbyExplain = hobbyExplain;
        this.hobbyImg = hobbyImg;
        this.hobbyAbility = hobbyAbility;
        this.hobbyBudget = hobbyBudget;
        this.hobbyLevel = hobbyLevel;
        this.hobbyTendency = hobbyTendency;
        this.rating=rating;
        this.reviewCount=reviewCount;
    }
    
}
