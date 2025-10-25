package com.senials.hobbyboard.service;

import com.senials.common.mapper.HobbyMapper;
import com.senials.common.mapper.PartyBoardMapper;
import com.senials.favorites.entity.Favorites;
import com.senials.favorites.repository.FavoritesRepository;
import com.senials.hobbyboard.dto.HobbyDTO;
import com.senials.hobbyboard.dto.HobbyDTOForCard;
import com.senials.hobbyboard.entity.Hobby;
import com.senials.hobbyboard.repository.HobbyRepository;
import com.senials.hobbyreview.repository.HobbyReviewRepository;
import com.senials.partyboard.dto.PartyBoardDTOForDetail;
import com.senials.partyboard.entity.PartyBoard;
import com.senials.partyboard.repository.PartyBoardRepository;
import com.senials.user.entity.User;
import com.senials.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class HobbyService {

    private final HobbyMapper hobbyMapper;
    private final HobbyRepository hobbyRepository;
    private final HobbyReviewRepository hobbyReviewRepository;
    private final UserRepository userRepository;
    private final FavoritesRepository favoritesRepository;
    private final PartyBoardRepository partyBoardRepository;
    private final PartyBoardMapper partyBoardMapper;

    public HobbyService(HobbyRepository hobbyRepository,
                        HobbyMapper hobbyMapper,
                        UserRepository userRepository,
                        FavoritesRepository favoritesRepository,
                        PartyBoardRepository partyBoardRepository,
                        PartyBoardMapper partyBoardMapper,
                        HobbyReviewRepository hobbyReviewRepository) {
        this.hobbyRepository = hobbyRepository;
        this.hobbyMapper = hobbyMapper;
        this.userRepository = userRepository;
        this.favoritesRepository = favoritesRepository;
        this.partyBoardRepository=partyBoardRepository;
        this.partyBoardMapper=partyBoardMapper;
        this.hobbyReviewRepository=hobbyReviewRepository;
    }

    //전체 hobby 불러오기
    public List<HobbyDTO> findAll() {
        List<Hobby> hobbyList = hobbyRepository.findAll();

        List<HobbyDTO> hobbyDTOList = hobbyList.stream().map(hobby -> {
            HobbyDTO dto=hobbyMapper.toHobbyDTO(hobby);
            dto.setRating(hobbyReviewRepository.avgRatingByHobbyNumber(hobby.getHobbyNumber()));
            dto.setReviewCount(hobbyReviewRepository.reviewCountByHobbyNumber(hobby.getHobbyNumber()));
            return dto;
        }).toList();

        return hobbyDTOList;
    }

    //취미 리스트 전체 조회후 최소 리뷰수 필터링 후 , 평균 평점 순 조회
    public List<HobbyDTO> hobbySortByRating(int minimumReviewCount, int limit) {
        List<Hobby> hobbyList = hobbyRepository.findAll();

        List<HobbyDTO> hobbyDTOList = hobbyList.stream().map(hobby -> {
            HobbyDTO dto=hobbyMapper.toHobbyDTO(hobby);
            dto.setRating(hobbyReviewRepository.avgRatingByHobbyNumber(hobby.getHobbyNumber()));
            dto.setReviewCount(hobbyReviewRepository.reviewCountByHobbyNumber(hobby.getHobbyNumber()));
            // 카테고리 네임도 추가
            dto.setCategoryName(hobby.getCategory().getCategoryName());
            return dto;
        }).toList();

        hobbyDTOList=hobbyDTOList.stream().filter(hobbyDTO->hobbyDTO.getReviewCount()>=minimumReviewCount)
                .sorted(Comparator.comparing(HobbyDTO::getRating).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        return hobbyDTOList;
    }

    //특정 hobby hobbyNumber로 불러오기
    public HobbyDTO findById(int hobbyNumber) {
        Hobby hobby = hobbyRepository.findById(hobbyNumber).orElseThrow(() -> new IllegalArgumentException("해당 취미가 존재하지 않습니다: " + hobbyNumber));
        HobbyDTO hobbyDTO = hobbyMapper.toHobbyDTO(hobby);
        hobbyDTO.setRating(hobbyReviewRepository.avgRatingByHobbyNumber(hobby.getHobbyNumber()));
        hobbyDTO.setReviewCount(hobbyReviewRepository.reviewCountByHobbyNumber(hobby.getHobbyNumber()));
        return hobbyDTO;
    }

    //특정 hobby들 categoryNumber로 불러오기
    public List<HobbyDTO> findByCategory(int categoryNumber) {
        List<Hobby> hobbyList = hobbyRepository.findByCategoryNumber(categoryNumber);

        List<HobbyDTO> hobbyDTOList = hobbyList.stream().map(hobby -> {
            HobbyDTO dto=hobbyMapper.toHobbyDTO(hobby);
            dto.setRating(hobbyReviewRepository.avgRatingByHobbyNumber(hobby.getHobbyNumber()));
            dto.setReviewCount(hobbyReviewRepository.reviewCountByHobbyNumber(hobby.getHobbyNumber()));
            return dto;
        }).toList();

        return hobbyDTOList;
    }

    //4가지 요소를 우선순위로 둬서 최종적으로 하나의 hobby를 조회
    public HobbyDTO suggestHobby(int hobbyAbility, int hobbyBudget, int hobbyLevel, int hobbyTendency) {
        List<Hobby> hobbyList = hobbyRepository.findByHobbyAbility(hobbyAbility);
        if (hobbyList.isEmpty()) {
            throw new IllegalStateException("No hobbies found for the given ability.");
        }

        // 단계별 필터링
        List<Hobby> previousList = new ArrayList<>(hobbyList);

        List<Hobby> filteredList = hobbyList.stream()
                .filter(hobby -> hobby.getHobbyBudget() == hobbyBudget)
                .toList();

        if (filteredList.isEmpty()) {
            filteredList = previousList;
        } else {
            previousList = filteredList;
            filteredList = filteredList.stream()
                    .filter(hobby -> hobby.getHobbyTendency() == hobbyTendency)
                    .toList();

            if (filteredList.isEmpty()) {
                filteredList = previousList;
            } else {
                previousList = filteredList;
                filteredList = filteredList.stream()
                        .filter(hobby -> hobby.getHobbyLevel() == hobbyLevel)
                        .toList();

                if (filteredList.isEmpty()) {
                    filteredList = previousList;
                }
            }
        }

        // 필터링 결과 확인
        if (filteredList.isEmpty()) {
            throw new IllegalStateException("No hobbies matched the criteria.");
        }

        // Hobby -> HobbyDTO 변환
        List<HobbyDTO> hobbyDTOList = filteredList.stream()
                .map(hobbyMapper::toHobbyDTO)
                .toList();

        // 무작위로 하나 선택
        Random random = new Random();
        return hobbyDTOList.get(random.nextInt(hobbyDTOList.size()));
    }

    //사용자와 취미를 받아와 해당 사용자에게 해당 취미를 관심사로 부여
    public Favorites setFavoritesByHobby(int hobbyNumber, int userNumber){
        User user= userRepository.findById(userNumber).orElseThrow(() -> new IllegalArgumentException("해당 유저 번호가 존재하지 않습니다: " + userNumber));
        Hobby hobby=hobbyRepository.findById(hobbyNumber).orElseThrow(() -> new IllegalArgumentException("해당 취미 번호가 존재하지 않습니다: " + hobbyNumber));
        Favorites favoritesEntity = new Favorites();
        favoritesEntity.initializeHobby(hobby);
        favoritesEntity.initializeUser(user);

        Favorites favorites=favoritesRepository.save(favoritesEntity);

        return favorites;
    }

    //취미 번호를 통해 해당하는 모임리스트 조회
    public List<PartyBoardDTOForDetail> getPartyBoardByHobbyNumber(int hobbyNumber) {
        Hobby hobby=hobbyRepository.findById(hobbyNumber).orElseThrow(() -> new IllegalArgumentException("해당 취미 번호가 존재하지 않습니다: " + hobbyNumber));;
        List<PartyBoard> partyBoardList=partyBoardRepository.findByHobby(hobby);
        List<PartyBoardDTOForDetail> partyBoardDTOList=partyBoardList.stream().map(partyBoardMapper::toPartyBoardDTOForDetail).toList();

        return partyBoardDTOList;
    }

    //키워드를 통한 취미이름 필터링 후 페이지네이션으로 조회
    public List<HobbyDTOForCard> searchHobbyByKeyword(String keyword, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Hobby> hobbyPage = hobbyRepository.findHobbyByKeyword(keyword, pageable);

        List<HobbyDTOForCard> dtoList = hobbyPage.getContent().stream()
                .map(hobbyMapper::toHobbyDTOForCard)
                .toList();

        return dtoList;
    }

}
