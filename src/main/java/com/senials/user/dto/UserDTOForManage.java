package com.senials.user.dto;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDTOForManage {

    private int userNumber;

    private String userName;

    private LocalDate userBirth;

    private String userEmail;

    private int userGender;

    private int userReportCnt;

    private int userStatus;

    private String userNickname;

    private LocalDate userSignupDate;


    /* AllArgsConstructor */
    @Builder
    public UserDTOForManage(int userNumber, String userName, LocalDate userBirth, String userEmail, int userGender, int userReportCnt, int userStatus, String userNickname, LocalDate userSignupDate) {
        this.userNumber = userNumber;
        this.userName = userName;
        this.userBirth = userBirth;
        this.userEmail = userEmail;
        this.userGender = userGender;
        this.userReportCnt = userReportCnt;
        this.userStatus = userStatus;
        this.userNickname = userNickname;
        this.userSignupDate = userSignupDate;
    }
}
