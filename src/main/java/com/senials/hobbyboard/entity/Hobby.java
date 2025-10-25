package com.senials.hobbyboard.entity;

import com.senials.category.entity.Category;
import com.senials.favorites.entity.Favorites;
import com.senials.hobbyreview.entity.HobbyReview;
import com.senials.partyboard.entity.PartyBoard;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "HOBBY")
public class Hobby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hobby_number", nullable = false)
    private int hobbyNumber;

    @Column(name = "category_number", nullable = false)
    private int categoryNumber;

    @Column(name = "hobby_name", nullable = false, length = 255)
    private String hobbyName;

    @Column(name = "hobby_explain", nullable = false, length = 255)
    private String hobbyExplain;

    @Column(name = "hobby_img", nullable = false, length = 5000)
    private String hobbyImg;

    @Column(name = "hobby_ability", nullable = false)
    private int hobbyAbility;

    @Column(name = "hobby_budget", nullable = false)
    private int hobbyBudget;

    @Column(name = "hobby_level", nullable = false)
    private int hobbyLevel;

    @Column(name = "hobby_tendency", nullable = false)
    private int hobbyTendency;

    // 카테고리와의 관계 설정
    @ManyToOne
    @JoinColumn(name = "category_number", insertable = false, updatable = false)
    private Category category;

    // 관계 설정
    @OneToMany(mappedBy = "hobby")
    private List<PartyBoard> partyBoards; // Hobby -> PartyBoard 관계 (1:N)

    @OneToMany(mappedBy = "hobby", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HobbyReview> hobbyReviews; // Hobby -> HobbyReview 관계 (1:N)

    @OneToMany(mappedBy = "hobby", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorites> favorites;

    /* AllArgsConstructor */
    public Hobby(int hobbyNumber, int categoryNumber, String hobbyName, String hobbyExplain, String hobbyImg, int hobbyAbility, int hobbyBudget, int hobbyLevel, int hobbyTendency, List<PartyBoard> partyBoards, List<HobbyReview> hobbyReviews, List<Favorites> favorites) {
        this.hobbyNumber = hobbyNumber;
        this.categoryNumber = categoryNumber;
        this.hobbyName = hobbyName;
        this.hobbyExplain = hobbyExplain;
        this.hobbyImg = hobbyImg;
        this.hobbyAbility = hobbyAbility;
        this.hobbyBudget = hobbyBudget;
        this.hobbyLevel = hobbyLevel;
        this.hobbyTendency = hobbyTendency;
        this.partyBoards = partyBoards;
        this.hobbyReviews = hobbyReviews;
        this.favorites = favorites;
    }

    public void initializeCategory(Category category) {
        this.category = category;
    }

}
