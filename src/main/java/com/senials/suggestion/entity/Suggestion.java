package com.senials.suggestion.entity;

import com.senials.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "suggestion")
@NoArgsConstructor
@Getter
@ToString
public class Suggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "suggestion_number")
    private int suggestionNumber;

    @ManyToOne
    @JoinColumn(name = "user_number", nullable = false)
    private User user;

    @Column(name = "suggestion_title", nullable = false, length = 255)
    private String suggestionTitle;

    @Column(name = "suggestion_type", nullable = false)
    private int suggestionType;

    @Column(name = "suggestion_detail", nullable = false, length = 5000)
    private String suggestionDetail;

    @Column(name = "suggestion_date", nullable = false)
    private LocalDateTime suggestionDate;

    @Builder
    public Suggestion(int suggestionNumber, User user, String suggestionTitle, int suggestionType, String suggestionDetail, LocalDateTime suggestionDate) {
        this.suggestionNumber = suggestionNumber;
        this.user = user;
        this.suggestionTitle = suggestionTitle;
        this.suggestionType = suggestionType;
        this.suggestionDetail = suggestionDetail;
        this.suggestionDate = suggestionDate;
    }

    public void initializeUser(User user){
        this.user=user;
    }
}
