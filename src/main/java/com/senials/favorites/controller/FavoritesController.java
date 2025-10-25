package com.senials.favorites.controller;

import com.senials.common.ResponseMessage;
import com.senials.favorites.dto.AddFavoriteDTO;
import com.senials.favorites.dto.FavoriteSelectDTO;
import com.senials.favorites.service.FavoritesService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/users")
public class FavoritesController {
    private final FavoritesService favoritesService;

    public FavoritesController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    // 사용자 관심사 제목 가져오기
    @GetMapping("/{userNumber}/favorites")
    public ResponseEntity<List<String>> getUserFavoriteTitles(@PathVariable int userNumber) {
        // ResponseHeader 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        List<String> favoriteTitles = favoritesService.getFavoriteTitlesByUser(userNumber);
        return ResponseEntity.ok().headers(headers).body(favoriteTitles);
    }

    //모든 취미, 카테고리명, 저장 여부 가져오기
    @GetMapping("/{userNumber}/favoritesAll")
    public ResponseEntity<List<FavoriteSelectDTO>> getAllHobbiesWithCategoryAndFavoriteStatus(@PathVariable int userNumber) {
        // ResponseHeader 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        List<FavoriteSelectDTO> favoriteSelectList = favoritesService.getAllHobbiesWithCategoryAndFavoriteStatus(userNumber);
        return ResponseEntity.ok().headers(headers).body(favoriteSelectList);
    }

    //관심사 등록
    @PostMapping("/{userNumber}/favorites")
    public ResponseEntity<ResponseMessage> addFavorite(@PathVariable int userNumber, @RequestBody AddFavoriteDTO addFavoriteDto) {
        // ResponseHeader 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        // 서비스 호출
        favoritesService.addFavorite(userNumber, addFavoriteDto.getHobbyNumber());
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "관심사 등록 성공", null));
    }

    //관심사 수정
    @PutMapping("/{userNumber}/favorites")
    public ResponseEntity<ResponseMessage> updateFavorite(@PathVariable int userNumber,
                                                 @RequestBody List<Integer> hobbyNumberList) {
        // ResponseHeader 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        for(int hobbyNumber : hobbyNumberList){
            System.out.println(hobbyNumber);
        }
            favoritesService.updateFavorite(userNumber, hobbyNumberList);
            return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "관심사가 수정되었습니다.",null));
    }
    //관심사 삭제
    @DeleteMapping("/{userNumber}/favorites/{hobbyNumber}")
    public ResponseEntity<ResponseMessage> removeFavorite(@PathVariable int userNumber, @PathVariable int hobbyNumber) {
        // ResponseHeader 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

            favoritesService.removeFavorite(userNumber, hobbyNumber);
            return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "관심사가 삭제되었습니다.",null));

    }
}
