package com.senials.user.repository;

import com.senials.user.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /*유저 모든 검색*/
    List<User> findAll();

    @Query(value = "SELECT u FROM User u WHERE u.userName like :keyword OR u.userNickname like :keyword")
    List<User> findAll(String keyword, Sort sort);
}
