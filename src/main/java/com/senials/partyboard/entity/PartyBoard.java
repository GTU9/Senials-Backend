package com.senials.partyboard.entity;

import com.senials.hobbyboard.entity.Hobby;
import com.senials.likes.entity.Likes;
import com.senials.meet.entity.Meet;
import com.senials.partyboardimage.entity.PartyBoardImage;
import com.senials.partymember.entity.PartyMember;
import com.senials.partyreview.entity.PartyReview;
import com.senials.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = "PARTY_BOARD")
public class PartyBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_board_number", nullable = false)
    private int partyBoardNumber; // auto-increment

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_number", referencedColumnName = "user_number", nullable = false)
    private User user; // 외래키 user_number -> User 엔티티와의 관계

    @ManyToOne(optional = false)
    @JoinColumn(name = "hobby_number", referencedColumnName = "hobby_number", nullable = false)
    private Hobby hobby; // 외래키 hobby_number -> Hobby 엔티티와의 관계

    @Column(name = "party_board_name", nullable = false, length = 255)
    private String partyBoardName;

    @Column(name = "party_board_detail", length = 5000)
    private String partyBoardDetail;

    @Column(name = "party_board_open_date", nullable = false)
    private LocalDate partyBoardOpenDate; // now()

    @Column(name = "party_board_status", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    private int partyBoardStatus = 0; // 0-모집중, 1-모집완료

    @Column(name = "party_board_view_cnt", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int partyBoardViewCnt = 0;

    @Column(name = "party_board_like_cnt", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int partyBoardLikeCnt = 0;

    @Column(name = "party_board_report_cnt", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int partyBoardReportCnt = 0;


    /* 관계 설정 */
    // 모임 대표 이미지
    @OneToMany(mappedBy = "partyBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyBoardImage> images;

    // 좋아요
    @OneToMany(mappedBy = "partyBoard", orphanRemoval = true)
    private List<Likes> likes;

    // 모임 멤버 목록
    @OneToMany(mappedBy = "partyBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyMember> partyMembers;

    // 일정 목록
    @OneToMany(mappedBy = "partyBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Meet> meets;

    // 모임 후기 목록
    @OneToMany(mappedBy = "partyBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyReview> reviews;


    /* AllArgsConstructor */
    @Builder
    public PartyBoard(int partyBoardNumber, User user, Hobby hobby, String partyBoardName, String partyBoardDetail, LocalDate partyBoardOpenDate, int partyBoardStatus, int partyBoardViewCnt, int partyBoardLikeCnt, int partyBoardReportCnt) {
        this.partyBoardNumber = partyBoardNumber;
        this.user = user;
        this.hobby = hobby;
        this.partyBoardName = partyBoardName;
        this.partyBoardDetail = partyBoardDetail;
        this.partyBoardOpenDate = partyBoardOpenDate;
        this.partyBoardStatus = partyBoardStatus;
        this.partyBoardViewCnt = partyBoardViewCnt;
        this.partyBoardLikeCnt = partyBoardLikeCnt;
        this.partyBoardReportCnt = partyBoardReportCnt;
    }


    /* 글 작성 시 대표 이미지 설정 */
    public void initializeImages(List<PartyBoardImage> images) {
        this.images = images;
    }


    /* 글 수정 용 */
    public void updateHobby(Hobby hobby) {
        this.hobby = hobby;
    }

    public void updatePartyBoardName(String partyBoardName) {
        this.partyBoardName = partyBoardName;
    }

    public void updatePartyBoardDetail(String partyBoardDetail) {
        this.partyBoardDetail = partyBoardDetail;
    }

    public void updatePartyBoardStatus(int partyBoardStatus) {
        this.partyBoardStatus = partyBoardStatus;
    }

    public void updateImages(List<PartyBoardImage> images) {
        this.images = images;
    }


    /* 모임 가입 */
    public void registerPartyMember(PartyMember partyMember) {
        this.partyMembers.add(partyMember);
    }
}
