package com.senials.user.entity;

import com.senials.favorites.entity.Favorites;
import com.senials.likes.entity.Likes;
import com.senials.partyboard.entity.PartyBoard;
import com.senials.partymember.entity.PartyMember;
import com.senials.partyreview.entity.PartyReview;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_number", nullable = false)
    private int userNumber;

    @Column(name = "user_pwd", nullable = false, length = 255)
    private String userPwd;

    @Column(name = "user_name", nullable = false, length = 255)
    private String userName;

    @Column(name = "user_birth", nullable = false, length = 255)
    private LocalDate userBirth;

    @Column(name = "user_email", nullable = false, length = 255)
    private String userEmail;

    @Column(name = "user_gender", nullable = false)
    private int userGender; // 0-남, 1-여

    @Column(name = "user_report_cnt", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int userReportCnt;

    @Column(name = "user_status", nullable = false)
    private int userStatus; // 0-기본, 1-임시활동정지, 2-활동정지

    @Column(name = "user_nickname", nullable = false, length = 255)
    private String userNickname;

    @Column(name = "user_detail", length = 255)
    private String userDetail;

    @Column(name = "user_profile_img", length = 5000)
    private String userProfileImg;

    @Column(name = "user_signup_date", nullable = false)
    private LocalDate userSignupDate;

    @Column(name = "user_uuid", nullable = false, length = 5000)
    private String userUuid;

    /* 관계 설정 */
    // 만든 모임
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyBoard> partyBoards; // User -> PartyBoard 관계 (1:N)

    // 가입한 모임
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyMember> partyMemberships;

    // 좋아요 한 모임
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Likes> likes;

    // 작성한 후기
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyReview> partyReviews;

    // 등록한 관심사
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorites> favoritesList;

    /* AllArgsConstructor */
    @Builder
    public User(int userNumber, String userPwd, String userName, LocalDate userBirth, String userEmail, int userGender, int userReportCnt, int userStatus, String userNickname, String userDetail, String userProfileImg, LocalDate userSignupDate, String userUuid) {
        this.userNumber = userNumber;
        this.userPwd = userPwd;
        this.userName = userName;
        this.userBirth = userBirth;
        this.userEmail = userEmail;
        this.userGender = userGender;
        this.userReportCnt = 0;
        this.userStatus = 0;
        this.userNickname = userNickname;
        this.userDetail = userDetail;
        this.userProfileImg = userProfileImg;
        this.userSignupDate = (userSignupDate != null) ? userSignupDate : LocalDate.now();
        this.userUuid = (userUuid != null) ? userUuid : UUID.randomUUID().toString();
    }

    /* 닉네임 수정용*/
    public void updateUserNickname(String userNickname){
        this.userNickname = userNickname;
    }

    /* 한줄 소개 수정용*/
    public void updateUserDetail(String userDetail){
        this.userDetail = userDetail;
    }

    /* 신고 카운트 증가 */
    public void increaseReportCnt() {
        this.userReportCnt += 1;
    }

    /* 유저 상태 변경 */
    public void changeStatus(int status) {
        this.userStatus = status;
    }

    /* 프로필 이미지 수정용 */
    public void updateUserProfileImg(String userProfileImg){
        this.userProfileImg = userProfileImg;
    }

    public void initializePwd(String password) {
        this.userPwd = password;
    }

    public void initializeSignupDate() {
        this.userSignupDate = LocalDate.now(); // 현재 날짜로 초기화
    }

    public void initializeUuid() {
        this.userUuid = UUID.randomUUID().toString();
    }


}
