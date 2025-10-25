package com.senials.partyboard.service;

import com.senials.common.mapper.PartyBoardMapper;
import com.senials.common.mapper.PartyBoardMapperImpl;
import com.senials.favorites.entity.Favorites;
import com.senials.favorites.repository.FavoritesRepository;
import com.senials.hobbyboard.dto.HobbyDTOForCard;
import com.senials.hobbyboard.entity.Hobby;
import com.senials.hobbyboard.repository.HobbyRepository;
import com.senials.likes.repository.LikeRepository;
import com.senials.partyboard.dto.*;
import com.senials.partyboard.entity.PartyBoard;
import com.senials.partyboard.repository.PartyBoardRepository;
import com.senials.partyboard.repository.PartyBoardSpecification;
import com.senials.partyboardimage.entity.PartyBoardImage;
import com.senials.partyboardimage.repository.PartyBoardImageRepository;
import com.senials.partymember.repository.PartyMemberRepository;
import com.senials.partyreview.repository.PartyReviewRepository;
import com.senials.user.entity.User;
import com.senials.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PartyBoardService {

    private final String partyImagePath = "src/main/resources/static/img/party_board";

    private final PartyBoardMapper partyBoardMapper;

    private final PartyBoardRepository partyBoardRepository;

    private final UserRepository userRepository;

    private final HobbyRepository hobbyRepository;

    private final FavoritesRepository favoritesRepository;

    private final PartyMemberRepository partyMemberRepository;

    private final PartyReviewRepository partyReviewRepository;

    private final LikeRepository likeRepository;

    private final ResourceLoader resourceLoader;
    private final PartyBoardImageRepository partyBoardImageRepository;


    @Autowired
    public PartyBoardService(
            PartyBoardMapperImpl partyBoardMapperImpl
            , PartyBoardRepository partyBoardRepository
            , UserRepository userRepository
            , HobbyRepository hobbyRepository
            , FavoritesRepository favoritesRepository
            , PartyMemberRepository partyMemberRepository
            , PartyReviewRepository partyReviewRepository
            , LikeRepository likeRepository
            , ResourceLoader resourceLoader,
            PartyBoardImageRepository partyBoardImageRepository)
    {
        this.partyBoardMapper = partyBoardMapperImpl;
        this.partyBoardRepository = partyBoardRepository;
        this.userRepository = userRepository;
        this.hobbyRepository = hobbyRepository;
        this.favoritesRepository = favoritesRepository;
        this.partyMemberRepository = partyMemberRepository;
        this.partyReviewRepository = partyReviewRepository;
        this.likeRepository = likeRepository;
        this.resourceLoader = resourceLoader;
        this.partyBoardImageRepository = partyBoardImageRepository;
    }

    /* 같은 취미 추천 모임 (상세 페이지 최하단) */
    public List<PartyBoardDTOForCard> getRecommendedPartyBoards(Integer userNumber, int partyBoardNumber) {
        User user;
        if(userNumber != null) {
            user = userRepository.findById(userNumber).orElseThrow(IllegalArgumentException::new);
        } else {
            user = null;
        }

        PartyBoard foundPartyBoard = partyBoardRepository.findById(partyBoardNumber)
                .orElseThrow(IllegalArgumentException::new);
        List<PartyBoard> partyBoardList = partyBoardRepository.find4ByHobbyOrderByRand(partyBoardNumber, foundPartyBoard.getHobby().getHobbyNumber());

        List<PartyBoardDTOForCard> partyBoardDTOList = partyBoardList.stream().map(partyBoard -> {
            PartyBoardDTOForCard partyBoardDTO = partyBoardMapper.toPartyBoardDTOForCard(partyBoard);
            String firstImageStr = null;
            PartyBoardImage firstImage = partyBoard.getImages().get(0);
            if(firstImage != null) {
                firstImageStr = firstImage.getPartyBoardImg();
            }
            partyBoardDTO.setFirstImage(firstImageStr);
            boolean isLiked = user != null && likeRepository.existsByUserAndPartyBoard(user, partyBoard);
            partyBoardDTO.setLiked(isLiked);
            return partyBoardDTO;
        }).toList();
        return partyBoardDTOList;
    }

    /* 인기 추천 모임 (평점 높은 순, 리뷰 개수 N개 이상, 모집중 >> M개 제한)*/
    public List<PartyBoardDTOForCard> getPopularPartyBoards(int minReviewCount, int size, int pageNumber) {

        Page<PartyBoard> partyBoardList = partyBoardRepository.findPopularPartyBoards(minReviewCount, PageRequest.of(pageNumber, size));

        List<PartyBoardDTOForCard> partyBoardDTOForCardList = partyBoardList.stream().map(partyBoard -> {

            int partyMemberCnt = partyMemberRepository.countAllByPartyBoard(partyBoard);
            int partyReviewCnt = partyReviewRepository.countAllByPartyBoard(partyBoard);
            double partyAvgRate = partyReviewRepository.findAvgRateByPartyBoard(partyBoard);

            String partyImageThumbnail = null;
            List<PartyBoardImage> partyBoardImageList = partyBoard.getImages();
            if (partyBoardImageList != null && !partyBoardImageList.isEmpty()) {
                partyImageThumbnail = partyBoardImageList.get(0).getPartyBoardImg();
            }

            PartyBoardDTOForCard partyBoardCard = partyBoardMapper.toPartyBoardDTOForCard(partyBoard);
            partyBoardCard.setMemberCount(partyMemberCnt);
            partyBoardCard.setReviewCount(partyReviewCnt);
            partyBoardCard.setAverageRating(partyAvgRate);
            partyBoardCard.setFirstImage(partyImageThumbnail);

            return partyBoardCard;

        }).toList();

        return partyBoardDTOForCardList;
    }


    /* 모임 검색 및 정렬 */
    public List<PartyBoardDTOForCard> searchPartyBoard(String sortMethod, String keyword, Integer cursor, int size, boolean isLikedOnly, Integer userNumber) {

        Sort.Order numberAsc = Sort.Order.asc("partyBoardNumber");
        Sort.Order numberDesc = Sort.Order.desc("partyBoardNumber");


        String sortColumn = null;
        Pageable pageable = null;
        boolean isAscending = false;
        boolean isIntegerSort = true;


        switch (sortMethod) {
            /* 최신순 */
            case "lastest":
                sortColumn = "partyBoardOpenDate";
                pageable = PageRequest.of(0, size
                        , Sort.by(Sort.Order.desc(sortColumn), numberDesc));
                isIntegerSort = false;
                break;

            /* 오래된 순*/
            case "oldest":
                sortColumn = "partyBoardOpenDate";
                pageable = PageRequest.of(0, size
                        , Sort.by(Sort.Order.asc(sortColumn), numberAsc));
                isAscending = true;
                isIntegerSort = false;
                break;

            /* 좋아요순 */
            case "mostLiked":
                sortColumn = "partyBoardLikeCnt";
                pageable = PageRequest.of(0, size
                        , Sort.by(Sort.Order.desc(sortColumn), numberDesc));
                break;

            /* 조회수순 */
            case "mostViewed":
                sortColumn = "partyBoardViewCnt";
                pageable = PageRequest.of(0, size
                        , Sort.by(Sort.Order.desc(sortColumn), numberDesc));
                break;
            default:
        }

        /* 유저 number 필요 */
        User user;
        if(userNumber != null) {
            user = userRepository.findById(userNumber).orElseThrow(IllegalArgumentException::new);
        } else {
            user = null;
        }

        /* 관심사 기반 추천 확인 */
        // 관심사 기반 추천 시 최소 빈 리스트 / 미추천 시 null
        List<Hobby> hobbyList = null;
        if(isLikedOnly && user != null) {
            List<Favorites> favoritesList = favoritesRepository.findAllByUser(user);

            /* 관심사 존재하는지 체크 */
            if(!favoritesList.isEmpty()) {
                hobbyList = favoritesList.stream().map(Favorites::getHobby).toList();
            } else {
                hobbyList = new ArrayList<>();
            }
        }


        Page<PartyBoard> partyBoardList = null;
        /* Specification 쿼리문 실행 */
        Specification<PartyBoard> spec = null;
        if(isIntegerSort) {
            spec = PartyBoardSpecification.searchLoadInteger(sortColumn, keyword, cursor, isAscending, hobbyList);
        } else {
            spec = PartyBoardSpecification.searchLoadLocalDate(sortColumn, keyword, cursor, isAscending, hobbyList);
        }
        partyBoardList = partyBoardRepository.findAll(spec, pageable);


        // if(!sortMethod.equals("mostRated")) {
        //
        // } else {
        //     partyBoardList = partyBoardRepository.findPopularPartyBoards();
        // }


        List<PartyBoardDTOForCard> partyBoardDTOForCardList = partyBoardList.stream()
                .map(partyBoard -> {
                    PartyBoardDTOForCard partyBoardCard = partyBoardMapper.toPartyBoardDTOForCard(partyBoard);

                    int partyMemberCnt = partyMemberRepository.countAllByPartyBoard(partyBoard);
                    int partyReviewCnt = partyReviewRepository.countAllByPartyBoard(partyBoard);
                    double partyAvgRate = partyReviewRepository.findAvgRateByPartyBoard(partyBoard);
                    boolean isLiked = user != null && likeRepository.existsByUserAndPartyBoard(user, partyBoard);   // user == null 비로그인 시 좋아요 표시 false

                    String partyImageThumbnail = null;
                    List<PartyBoardImage> partyBoardImageList = partyBoard.getImages();
                    if (partyBoardImageList != null && !partyBoardImageList.isEmpty()) {
                        partyImageThumbnail = partyBoardImageList.get(0).getPartyBoardImg();
                    }

                    partyBoardCard.setMemberCount(partyMemberCnt);
                    partyBoardCard.setReviewCount(partyReviewCnt);
                    partyBoardCard.setAverageRating(partyAvgRate);
                    partyBoardCard.setFirstImage(partyImageThumbnail);
                    partyBoardCard.setLiked(isLiked);

                    return partyBoardCard;
                }).collect(Collectors.toList());

        return partyBoardDTOForCardList;
    }


    // 모임 상세 조회
    public PartyBoardDTOForDetail getPartyBoardByNumber(int partyBoardNumber) {

        PartyBoard partyBoard = partyBoardRepository.findByPartyBoardNumber(partyBoardNumber);

        if(partyBoard == null) {
            throw new RuntimeException("[Error] 잘못된 모임 번호 요청");
        }

        PartyBoardDTOForDetail partyBoardDTO = partyBoardMapper.toPartyBoardDTOForDetail(partyBoard);
        partyBoardDTO.setUserNumber(partyBoard.getUser().getUserNumber());
        partyBoardDTO.setHobbyNumber(partyBoard.getHobby().getHobbyNumber());
        partyBoardDTO.setCategoryNumber(partyBoard.getHobby().getCategory().getCategoryNumber());
        partyBoardDTO.setCategoryName(partyBoard.getHobby().getCategory().getCategoryName());
        partyBoardDTO.setImages(partyBoard.getImages().stream().map(partyBoardMapper::toPartyBoardImageDTO).toList());
        partyBoardDTO.setPartyMemberCnt(partyBoard.getPartyMembers().size());

        return partyBoardDTO;
    }


    /* 모임 글 작성 */
    @Transactional
    public int registerPartyBoard(int userNumber, List<MultipartFile> imageFiles, PartyBoardDTOForWrite newPartyBoardDTO) {

        // 1. userNumber로 User 엔티티 조회
        User user = userRepository.findById(userNumber)
                .orElseThrow(IllegalArgumentException::new);

        // 2. hobbyNumber로 Hobby 엔티티 조회
        Hobby hobby = hobbyRepository.findById(newPartyBoardDTO.getHobbyNumber())
                .orElseThrow(IllegalArgumentException::new);

        // 3. PartyBoardDTOForWrite -> PartyBoard 엔티티 생성
        PartyBoard newPartyBoard = PartyBoard.builder()
                .partyBoardNumber(0)
                .user(user)
                .hobby(hobby)
                .partyBoardName(newPartyBoardDTO.getPartyBoardName())
                .partyBoardDetail(newPartyBoardDTO.getPartyBoardDetail())
                .partyBoardOpenDate(LocalDate.now())
                .build();


        /* 글 정보 저장 & 자동 증가 된 partyBoardNumber */
        PartyBoard savedPartyBoard = partyBoardRepository.save(newPartyBoard);
        int partyBoardNumber = savedPartyBoard.getPartyBoardNumber();


        // 4. 이미지 저장
        Resource resource = resourceLoader.getResource("classpath:static/img/party_board/" + partyBoardNumber + "/thumbnail");

        /* 파일 경로 지정 (없으면 디렉터리 생성) */
        String filePath = null;
        try {
            if (!resource.exists()) {

                String root = partyImagePath + "/" + partyBoardNumber + "/thumbnail";
                File file = new File(root);

                if( !file.mkdirs() ) {
                    throw new IOException();
                }
                filePath = file.getAbsolutePath();

            } else {
                filePath = resource.getFile().getAbsolutePath();
            }
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패");
        }


        /* 파일 실제 저장 */
        List<PartyBoardImage> partyBoardImages = new ArrayList<>();
        for (MultipartFile imageFile : imageFiles) {
            String randomId = UUID.randomUUID().toString().replace("-", "");

            String originalName = imageFile.getOriginalFilename();
            String ext = originalName.substring(originalName.lastIndexOf("."));
            String savedName = randomId + ext;
            
            try {
                imageFile.transferTo(new File(filePath + "/" + savedName));
            } catch (IOException e) {
                throw new RuntimeException("이미지 저장 실패");
            }
            
            // PartyBoardImage 엔티티 생성
            PartyBoardImage partyBoardImage = PartyBoardImage.builder()
                    .partyBoard(savedPartyBoard)
                    .partyBoardImg(savedName)
                    .build();
            
            partyBoardImages.add(partyBoardImage);
        }


        // 5. 파일이미지 엔티티 저장
        savedPartyBoard.updateImages(partyBoardImages);
        return partyBoardRepository.save(newPartyBoard).getPartyBoardNumber();
    }


    /* 모임 글 수정 */
    @Transactional
    public void modifyPartyBoard(int userNumber, List<MultipartFile> imageFiles, int partyBoardNumber, PartyBoardDTOForModify newPartyBoardDTO) {


        // 1. userNumber로 User 엔티티 조회
        User user = userRepository.findById(userNumber)
                .orElseThrow(() -> new IllegalArgumentException("서버 내부 오류"));

        // 2. hobbyNumber로 Hobby 엔티티 조회
        Hobby hobby = hobbyRepository.findById(newPartyBoardDTO.getHobbyNumber())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

        // 3. 기존 엔티티 로드 및 유저 검사 후 수정
        PartyBoard partyBoard = partyBoardRepository.findById(partyBoardNumber)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

        if(partyBoard.getUser().getUserNumber() != userNumber) {
            throw new IllegalArgumentException("잘못된 요청입니다. (모임장 아님)");
        }

        partyBoard.updatePartyBoardName(newPartyBoardDTO.getPartyBoardName());
        partyBoard.updatePartyBoardDetail(newPartyBoardDTO.getPartyBoardDetail());
        partyBoard.updateHobby(hobby);
        partyBoard.updatePartyBoardStatus(newPartyBoardDTO.getPartyBoardStatus());


        // 4. 이미지 저장
        String imgBoardPath = partyImagePath + "/" + partyBoardNumber + "/thumbnail";
        Resource resource = resourceLoader.getResource("classpath:static/img/party_board/" + partyBoardNumber + "/thumbnail");

        /* 파일 경로 지정 (없으면 디렉터리 생성) */
        String filePath = null;
        try {
            if (!resource.exists()) {

                File file = new File(imgBoardPath);

                if( !file.mkdirs() ) {
                    throw new IOException();
                }
                filePath = file.getAbsolutePath();

            } else {
                filePath = resource.getFile().getAbsolutePath();
            }
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패");
        }


        /* 파일 실제 저장 */
        if(imageFiles != null && !imageFiles.isEmpty()) {

            /* 이미지 새롭게 업로드 된 경우 기존 이미지 삭제 */
            File[] deletedFiles = new File(imgBoardPath).listFiles();
            for(File deletedFile : deletedFiles) {
                deletedFile.delete();
            }


            /* 엔티티에서도 삭제 */
            List<PartyBoardImage> partyBoardImages = partyBoard.getImages();
            partyBoardImages.clear();


            for (MultipartFile imageFile : imageFiles) {
                String randomId = UUID.randomUUID().toString().replace("-", "");

                String originalName = imageFile.getOriginalFilename();
                String ext = originalName.substring(originalName.lastIndexOf("."));
                String savedName = randomId + ext;

                try {
                    imageFile.transferTo(new File(filePath + "/" + savedName));
                } catch (IOException e) {
                    throw new RuntimeException("이미지 저장 실패");
                }

                // PartyBoardImage 엔티티 생성
                PartyBoardImage partyBoardImage = PartyBoardImage.builder()
                        .partyBoard(partyBoard)
                        .partyBoardImg(savedName)
                        .build();

                partyBoardImages.add(partyBoardImage);
            }
        }
    }

    /* 모임 글 삭제 */
    @Transactional
    public void removePartyBoard(Integer userNumber, int partyBoardNumber) {


        if(userNumber != null ){
            User user = userRepository.findById(userNumber)
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));


            PartyBoard partyBoard = partyBoardRepository.findById(partyBoardNumber)
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));


            if(partyBoard.getUser().getUserNumber() != userNumber) {
                throw new IllegalArgumentException("잘못된 요청입니다.");
            } else {

                String imgBoardPath = partyImagePath + "/" + partyBoardNumber + "/thumbnail";
                Resource resource = resourceLoader.getResource("classpath:static/img/party_board/" + partyBoardNumber + "/thumbnail");

                /* 파일 경로 지정 (없으면 디렉터리 생성) */
                String filePath = null;
                try {
                    if (!resource.exists()) {

                        File file = new File(imgBoardPath);

                        if( !file.mkdirs() ) {
                            throw new IOException();
                        }
                        filePath = file.getAbsolutePath();

                    } else {
                        filePath = resource.getFile().getAbsolutePath();
                    }
                } catch (IOException e) {
                    throw new RuntimeException("글 삭제 실패");
                }

                File[] deletedFiles = new File(imgBoardPath).listFiles();
                for(File deletedFile : deletedFiles) {
                    deletedFile.delete();
                }

                partyBoardRepository.deleteById(partyBoardNumber);
            }

        }else {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }

    }

    //키워드를 통한 모임 이름 필터링 후 페이지네이션으로 조회
    public List<PartyBoardDTOForCard> searchPartyByKeyword(String keyword, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<PartyBoard> partyBoardPage = partyBoardRepository.findPartyByKeyword(keyword,pageable);

        List<PartyBoardDTOForCard> dtoList = partyBoardPage.getContent().stream()
                .map(partyBoard -> {
                    PartyBoardDTOForCard partyBoardCard = partyBoardMapper.toPartyBoardDTOForCard(partyBoard);

                    String partyImageThumbnail = null;
                    List<PartyBoardImage> partyBoardImageList = partyBoard.getImages();
                    if (partyBoardImageList != null && !partyBoardImageList.isEmpty()) {
                        partyImageThumbnail = partyBoardImageList.get(0).getPartyBoardImg();
                    }

                    partyBoardCard.setMemberCount(partyMemberRepository.countAllByPartyBoard(partyBoard));
                    partyBoardCard.setReviewCount(partyReviewRepository.countAllByPartyBoard(partyBoard));
                    partyBoardCard.setAverageRating(partyReviewRepository.findAvgRateByPartyBoard(partyBoard));
                    partyBoardCard.setFirstImage(partyImageThumbnail);

                    return partyBoardCard;
                })
                .toList();

        return dtoList;
    }
}
