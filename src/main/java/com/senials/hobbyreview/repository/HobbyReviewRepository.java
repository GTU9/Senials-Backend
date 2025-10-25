package com.senials.hobbyreview.repository;

import com.senials.hobbyboard.entity.Hobby;
import com.senials.hobbyreview.entity.HobbyReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HobbyReviewRepository extends JpaRepository<HobbyReview,Integer> {
    List<HobbyReview> findByHobby(Hobby hobby);

    @Query(value = "SELECT COALESCE(AVG(hobby_review_rate), 0) " +
            "FROM hobby_review " +
            "WHERE hobby_number = :hobbyNumber",
            nativeQuery = true)
    Double avgRatingByHobbyNumber(@Param("hobbyNumber") int hobbyNumber);

    @Query(value = "SELECT COUNT(hobby_review_rate) " +
            "FROM hobby_review " +
            "WHERE hobby_number = :hobbyNumber",
            nativeQuery = true)
    int reviewCountByHobbyNumber(@Param("hobbyNumber") int hobbyNumber);

}
