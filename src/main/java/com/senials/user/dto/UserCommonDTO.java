package com.senials.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserCommonDTO {

    /*      private int userNumber;*/

    private String userName;

    private String userNickname;

    private String userDetail;

    private String userProfileImg;

    private int userStatus;

    // 새로운 생성자 추가
    public UserCommonDTO(String userName, String userNickname, String userDetail, String userProfileImg, int userStatus) {
        this.userName = userName;
        this.userNickname = userNickname;
        this.userDetail = userDetail;
        this.userProfileImg = userProfileImg;
        this.userStatus = userStatus;
    }
    // 새로운 생성자 추가
    public UserCommonDTO(String userName, String userNickname, String userDetail) {
        this.userName = userName;
        this.userNickname = userNickname;
        this.userDetail = userDetail;
    }

}
