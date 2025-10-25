package com.senials.suggestion.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class SuggestionDTO {
    private int suggestionNumber;
    private int userNumber; //건의 추가 유저
    private String userName;
    private String suggestionTitle; // 건의 제목
    private int suggestionType;     //0- 취미 추가, 1- 버그 제보
    private String suggestionDetail; //건의 내용
    private LocalDateTime suggestionDate;

    public SuggestionDTO(int suggestionNumber,
                         int userNumber,
                         String userName,
                         String suggestionTitle,
                         int suggestionType,
                         String suggestionDetail,
                         LocalDateTime suggestionDate) {
        this.suggestionNumber = suggestionNumber;
        this.userNumber = userNumber;
        this.userName=userName;
        this.suggestionTitle = suggestionTitle;
        this.suggestionType = suggestionType;
        this.suggestionDetail = suggestionDetail;
        this.suggestionDate = suggestionDate;
    }
}
