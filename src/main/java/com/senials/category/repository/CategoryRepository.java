package com.senials.category.repository;

import com.senials.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query(value = "SELECT * FROM category ORDER BY RAND()", nativeQuery = true)
    List<Category> findAllByRandom();
}
