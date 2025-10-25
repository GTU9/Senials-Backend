package com.senials.hobbyboard.repository;

import com.senials.hobbyboard.entity.Hobby;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HobbyRepository extends JpaRepository<Hobby, Integer> {
    List<Hobby> findByCategoryNumber(int categoryNumber);

    List<Hobby> findByHobbyAbility(int hobbyAbility);

    @Query(value="SELECT h " +
            "FROM Hobby h " +
            "WHERE h.hobbyName LIKE %:keyword%")
    Page<Hobby> findHobbyByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
