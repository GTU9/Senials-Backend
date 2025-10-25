package com.senials.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {

    private int userNumber;

    private String userPwd;

    private String userName;

    private LocalDate userBirth;

    private String userEmail;

    private int userGender;

    private int userReportCnt;

    private int userStatus;

    private String userNickname;

    private String userDetail;

    private String userProfileImg;

    private LocalDate userSignupDate;

    private String userUuid;

    /* AllArgsConstructor */
    public UserDTO(int userNumber, String userPwd, String userName, LocalDate userBirth,
                   String userEmail, int userGender, int userReportCnt, int userStatus,
                   String userNickname, String userDetail, String userProfileImg,
                   LocalDate userSignupDate, String userUuid) {
        this.userNumber = userNumber;
        this.userPwd = userPwd;
        this.userName = userName;
        this.userBirth = userBirth;
        this.userEmail = userEmail;
        this.userGender = userGender;
        this.userReportCnt = userReportCnt;
        this.userStatus = userStatus;
        this.userNickname = userNickname;
        this.userDetail = userDetail;
        this.userProfileImg = userProfileImg;
        this.userSignupDate = userSignupDate;
        this.userUuid = userUuid;
    }
}
