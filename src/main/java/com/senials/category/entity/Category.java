package com.senials.category.entity;

import com.senials.hobbyboard.entity.Hobby;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = "CATEGORY")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_number", nullable = false)
    private int categoryNumber;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    /* 엔티티 관계 설정 */
    @OneToMany(mappedBy = "category")
    private List<Hobby> hobbies;

    /* AllArgsConstructor */
    public Category(int categoryNumber, String categoryName) {
        this.categoryNumber = categoryNumber;
        this.categoryName = categoryName;
    }

}
