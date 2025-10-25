package com.senials.user.controller;

import com.senials.common.ResponseMessage;
import com.senials.common.TokenParser;
import com.senials.config.HttpHeadersFactory;
import com.senials.partyboard.dto.PartyBoardDTOForCard;
import com.senials.partyboardimage.dto.FileDTO;
import com.senials.user.dto.UserCommonDTO;
import com.senials.user.dto.UserDTO;
import com.senials.user.dto.UserDTOForManage;
import com.senials.user.entity.User;
import com.senials.user.repository.UserRepository;
import com.senials.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping
public class UserController {

    private final TokenParser tokenParser;
    private final HttpHeadersFactory httpHeadersFactory;
    private final UserRepository userRepository;
    private UserService userService;
    private final ResourceLoader resourceLoader;

    public UserController(
            TokenParser tokenParser
            , UserService userService
            , ResourceLoader resourceLoader
            , HttpHeadersFactory httpHeadersFactory,
            UserRepository userRepository) {
        this.tokenParser = tokenParser;
        this.userService = userService;
        this.resourceLoader = resourceLoader;
        this.httpHeadersFactory = httpHeadersFactory;
        this.userRepository = userRepository;
    }

    /* 유저 상태 변경 */
    @PutMapping("/users")
    public ResponseEntity<ResponseMessage> changeUsersStatus (
            @RequestBody UserStatusChangeRequest request
    ) {

        userService.changeUsersStatus(request.getCheckedUsers(), request.getStatus());

        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "유저 상태 변경 성공", null));
    }

    /* 관리자 페이지용 모든 사용자 조회 */
    @GetMapping("/users-manage")
    public ResponseEntity<ResponseMessage> getAllUsersForManage(
            @RequestParam(required = false) String keyword
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        List<UserDTOForManage> users = userService.getAllUsersForManage(keyword);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("users", users);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ResponseMessage(200, "전체 사용자 조회 성공", responseMap));
    }

    /* !! 비밀번호까지 가져가는데 이거 모임???? */
    // 모든 사용자 조회
    @GetMapping("/users")
    public ResponseEntity<ResponseMessage> getAllUsers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        List<UserDTO> users = userService.getAllUsers();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("users", users);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ResponseMessage(200, "전체 사용자 조회 성공", responseMap));
    }


    // 특정 사용자 조회
    @GetMapping("users/{userNumber}")
    public ResponseEntity<ResponseMessage> getUserByNumber(
            @PathVariable int userNumber
    ) {
        System.out.println("test");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        // userNumber로 데이터 조회
        UserCommonDTO user = userService.getUserByNumber(userNumber);

        if (user == null) {
            return ResponseEntity.status(404)
                    .headers(headers)
                    .body(new ResponseMessage(404, "사용자를 찾을 수 없습니다.", null));
        }

        // 응답 생성 (userNumber는 제외됨)
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("user", user);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ResponseMessage(200, "사용자 조회 성공", responseMap));
    }

    //특정 사용자 탈퇴
    @DeleteMapping("users/{userNumber}")
    public ResponseEntity<ResponseMessage> deleteUser(/*@PathVariable int userNumber*/@RequestHeader("Authorization") String token) {

        int userNumber = extractUserNumberFromToken(token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        boolean isDeleted = userService.deleteUser(userNumber);

        if (!isDeleted) {
            return ResponseEntity.status(404)
                    .headers(headers)
                    .body(new ResponseMessage(404, "회원 탈퇴 실패: 사용자를 찾을 수 없습니다.", null));
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ResponseMessage(200, "회원 탈퇴 성공", null));
    }

    // 특정 사용자 수정 put
    @PutMapping("users/{userNumber}/modify")
    public ResponseEntity<ResponseMessage> updateUserProfile(
            /*@PathVariable int userNumber*/
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> updatedFields) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        int userNumber = extractUserNumberFromToken(token);

        boolean isUpdated = userService.updateUserProfile(
                userNumber,
                updatedFields.get("userNickname"),
                updatedFields.get("userDetail")
                /*, updatedFields.get("userProfileImg")*/
        );

        if (!isUpdated) {
            return ResponseEntity.status(404)
                    .headers(headers)
                    .body(new ResponseMessage(404, "사용자를 찾을 수 없습니다.", null));
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ResponseMessage(200, "사용자 프로필 수정 성공", null));
    }


    // 사용자 프로필 출력
    @GetMapping("/img/userProfile/{userNumber}")
    public ResponseEntity<Resource> getUserImage(
            @PathVariable int userNumber
    ){
        try{
            UserCommonDTO foundUser = userService.getUserByNumber(userNumber);

            Resource resource = resourceLoader.getResource("classpath:static/img/user_profile/" + foundUser.getUserProfileImg());

            if (resource.exists()){
                String contentType = "image/png";//기본 MIME 타입 설정

                if(resource.getFilename().endsWith("jpg") || resource.getFilename().endsWith("jpeg")){
                    contentType = "image/jpeg";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);

            }else {
                resource = resourceLoader.getResource("classpath:static/img/user_profile/defaultProfile.png");
                return ResponseEntity.ok().contentType(MediaType.parseMediaType("image/png")).body(resource);
            }
        }catch (Exception e){
            Resource resource = resourceLoader.getResource("classpath:static/img/user_profile/defaultProfile.png");
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("image/png")).body(resource);
        }
    }


    //사용자 프로필 원래꺼는 삭제, 새로 등록된거 DB 에 추가
    // 사용자 프로필 이미지 업로드
    @PostMapping("users/{userNumber}/profile/upload")
    public ResponseEntity<ResponseMessage> uploadProfileImage(
            @PathVariable int userNumber
            , @RequestHeader(name = "Authorization") String token
            , @RequestPart("profileImage") MultipartFile profileImage
    ) {
        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();

        int tokenUserNumber = tokenParser.extractUserNumberFromToken(token);

        if(userNumber != tokenUserNumber){
            throw new IllegalArgumentException("잘못된 요청");
        }

        User user = userRepository.findById(tokenUserNumber)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청 (유저를 찾을 수 없음)"));

        try {
            // 기존 이미지 삭제
            Resource resource = resourceLoader.getResource("classpath:static/img/user_profile");
            String filePath = null;
            if(resource.exists()) {
                filePath = resource.getFile().getAbsolutePath();
            } else {
                File newDir = new File("src/main/resources/static/img/user_profile");
                if(!newDir.mkdir()) {
                    throw new IOException("이미지 저장 실패");
                } else {
                    filePath = newDir.getAbsolutePath();
                }
            }

            File prevFile = new File("src/main/resources/static/img/user_profile/" + user.getUserProfileImg());
            if (prevFile.exists()) {
                prevFile.delete();
            }

            // 새 이미지 저장 - 나중에 서비스 단으로 옮겨야함 여기서 저장 ㄴ
            String newFileName = userNumber + "." + getFileExtension(profileImage.getOriginalFilename());
            File newFile = new File(filePath + "/" + newFileName);
            profileImage.transferTo(newFile);

            // 데이터베이스에 새로운 이미지 경로 저장
            userService.updateUserProfileImage(tokenUserNumber, newFileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new ResponseMessage(200, "프로필 사진 업로드 성공", null));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .headers(headers)
                    .body(new ResponseMessage(500, "파일 저장 중 오류가 발생했습니다.", null));
        }
    }

    // 파일 확장자 추출
    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }



    //사용자 별 참여한 모임 출력
    // 사용자별 참여 모임 목록 조회
    @GetMapping("users/{userNumber}/parties")
    public ResponseEntity<ResponseMessage> getUserJoinedPartyBoards(
            /*@PathVariable int userNumber*/
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size) {

        int userNumber = extractUserNumberFromToken(token);
        // Service 호출
        List<PartyBoardDTOForCard> joinedParties = userService.getJoinedPartyBoardsByUserNumber(userNumber, page, size);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));


        // 응답 데이터 생성
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("joinedParties", joinedParties);
        responseMap.put("currentPage", page);
        responseMap.put("pageSize", size);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ResponseMessage(200, "사용자가 참여한 모임 조회 성공", responseMap));
    }

    @Value("${jwt.secret}")
    private String secretKey;
    // JWT에서 userNumber를 추출하는 메서드
    private int extractUserNumberFromToken(String token) {
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey) // 비밀 키 설정
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody();
            return claims.get("userNumber", Integer.class);
        }catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 토큰입니다."); // 적절한 예외 처리
        }

    }

    //사용자 별 자신이 만든 모임 조회
    @GetMapping("users/{userNumber}/made")
    public ResponseEntity<ResponseMessage> getUserMadeParties(
            /*@PathVariable int userNumber,*/
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestHeader("Authorization") String token) {

        int userNumber = extractUserNumberFromToken(token);
        // Service 호출
        List<PartyBoardDTOForCard> madeParties = userService.getMadePartyBoardsByUserNumber(userNumber, page, size);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        // if (madeParties.isEmpty()) {
        //     return ResponseEntity.status(404)
        //             .body(new ResponseMessage(404, "사용자가 만든 모임이 없습니다.", null));
        // }

        // 응답 데이터 생성
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("madeParties", madeParties);
        responseMap.put("currentPage", page);
        responseMap.put("pageSize", size);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ResponseMessage(200, "사용자가 만든 모임 조회 성공", responseMap));
    }


    /*모임 개수 api*/

    /*사용자 별 참여한 모임 개수*/
    @GetMapping("/users/{userNumber}/parties/count")
    public ResponseEntity<ResponseMessage> countUserJoinedParties(
            @PathVariable int userNumber
    ) {
        long count = userService.countPartiesPartyBoardsByUserNumber(userNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("partiesPartyCount", count);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ResponseMessage(200, "사용자가 참여한 모임 개수 조회 성공", responseMap));
    }
  
    /*사용자 별 만든 모임 개수*/
    @GetMapping("/users/{userNumber}/made/count")
    public ResponseEntity<ResponseMessage> countUserMadeParties(
            @PathVariable int userNumber
    ) {
        long count = userService.countMadePartyBoardsByUserNumber(userNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("madePartyCount", count);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ResponseMessage(200, "사용자가 만든 모임 개수 조회 성공", responseMap));

    }

}
