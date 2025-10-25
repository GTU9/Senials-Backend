package com.senials.favorites.repository;

import com.senials.favorites.entity.Favorites;
import com.senials.hobbyboard.entity.Hobby;
import com.senials.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, Integer> {

    List<Favorites> findAllByUser(User user);


    // 특정 사용자의 관심사를 조회
    List<Favorites> findByUser(User user);

    // 특정 사용자가 특정 취미를 이미 등록했는지 확인하는 메서드
    boolean existsByUserAndHobby(User user, Hobby hobby);

    // 사용자 번호(userNumber)와 취미 번호(hobbyNumber)로 Favorites 조회
    Favorites findByUserAndHobby(User user, Hobby hobby);

}
