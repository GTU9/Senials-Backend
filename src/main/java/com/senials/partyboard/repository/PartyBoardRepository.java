package com.senials.partyboard.repository;

import com.senials.hobbyboard.entity.Hobby;
import com.senials.partyboard.entity.PartyBoard;
import com.senials.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartyBoardRepository extends JpaRepository<PartyBoard, Integer>, JpaSpecificationExecutor<PartyBoard> {

    // 모임 상세 조회
    PartyBoard findByPartyBoardNumber(int partyBoardNumber);

    Page<PartyBoard> findAllByHobbyIn(List<Hobby> hobby, Pageable pageable);

    /* 페이지네이션 용 */
    Page<PartyBoard> findByUser(User user, Pageable pageable);

    /*사용자별 만든 모임의 수*/
    long countByUser(User user);

    List<PartyBoard> findByHobby(Hobby hobby);


    /* 같은 취미 추천 모임 (상세 페이지 최하단) */
    @Query(value = "SELECT * FROM party_board WHERE party_board_number != :partyBoardNumber AND hobby_number IN (select hobby_number from hobby hb where hb.category_number = (select category_number from hobby where hobby_number = :hobbyNumber)) ORDER BY RAND() LIMIT 4", nativeQuery = true)
    public List<PartyBoard> find4ByHobbyOrderByRand(int partyBoardNumber, int hobbyNumber);


    /* 인기 추천 모임 */
    /* 평점 높은 순, 리뷰 개수 N개 이상, 모집중 >> M개 */
    @Query(value = """
            SELECT pb.* 
            FROM party_board pb 
            JOIN party_review pr 
                ON pb.party_board_status = 0 
                       AND 
                   pb.party_board_number = pr.party_board_number 
            GROUP BY pb.party_board_number 
            HAVING COUNT(pr.party_review_number) >= :minReviewCount 
            ORDER BY AVG(pr.party_review_rate) DESC 
            """
            , countQuery = """
                SELECT COUNT(*) 
                FROM party_board pb 
                    JOIN party_review pr 
                        ON pb.party_board_status = 0 
                               AND 
                           pb.party_board_number = pr.party_board_number 
                GROUP BY pb.party_board_number
                HAVING COUNT(pr.party_review_number) >= :minReviewCount
                ORDER BY AVG(pr.party_review_rate) DESC
                """
            , nativeQuery = true)
    Page<PartyBoard> findPopularPartyBoards(int minReviewCount, Pageable pageable);

    @Query(value="SELECT pb " +
            "FROM PartyBoard pb " +
            "WHERE pb.partyBoardName LIKE %:keyword%")
    Page<PartyBoard> findPartyByKeyword(@Param("keyword") String keyword, Pageable pageable);


}
