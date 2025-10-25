package com.senials.likes.entity;

import com.senials.partyboard.entity.PartyBoard;
import com.senials.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = "LIKES")
public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_number", nullable = false)
    private int likeNumber;

    @ManyToOne
    @JoinColumn(name = "user_number", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "party_board_number", nullable = false)
    private PartyBoard partyBoard;


    /* AllArgsConstructor */
    public Likes(int likeNumber, User user, PartyBoard partyBoard) {
        this.likeNumber = likeNumber;
        this.user = user;
        this.partyBoard = partyBoard;
    }

}
