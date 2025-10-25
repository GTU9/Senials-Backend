package com.senials.partyboardimage.controller;

import com.senials.common.ResponseMessage;
import com.senials.partyboardimage.dto.FileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@RestController
public class PartyBoardImageController {

    private final String imgPath = "classpath:static/img";

    private final ResourceLoader resourceLoader;


    @Autowired
    public PartyBoardImageController(
            ResourceLoader resourceLoader
    ) {
        this.resourceLoader = resourceLoader;
    }


    // 모임 섬네일(대표) 이미지 추가
    @PutMapping("/partyboardimages/{partyBoardNumber}")
    public ResponseEntity<ResponseMessage> addPartyBoardThumbnails(
            @PathVariable int partyBoardNumber,
            @RequestParam List<MultipartFile> addedFiles
    ) throws IOException {

        String root = System.getProperty("user.dir") + "/src/main/resources/static" + "/img/party_board/" + partyBoardNumber + "/thumbnail";
        String filePath = null;
        File fileDir = new File(root);

        if (!fileDir.exists()) {

            fileDir.mkdirs();
            filePath = fileDir.getAbsolutePath();

        } else {

            filePath = fileDir.getAbsolutePath();

        }

        Map<String, Object> responseMap = new HashMap<>();

        List<FileDTO> files = new ArrayList<>();
        List<String> savedFiles = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            for(MultipartFile addedFile  : addedFiles) {

                /* 파일명 랜덤 UUID 변경 처리 */
                String originFileName = addedFile.getOriginalFilename();
                String ext = originFileName.substring(originFileName.lastIndexOf("."));
                String savedFileName = UUID.randomUUID().toString().replace("-", "")
                        + ext;

                /* 파일 정보 등록 */
                files.add(new FileDTO(originFileName, savedFileName, filePath));

                /* 파일 저장 */
                addedFile.transferTo(new File(filePath + "/" + savedFileName));

                savedFiles.add(savedFileName);
            }

            responseMap.put("savedFiles", savedFiles);

            /* created로 바꾸고 사진 출력하는 API 주소 전달하기 */
            return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "업로드 성공", responseMap));

        } catch (Exception e) {

            for (FileDTO file : files) {
                new File(filePath + "/" + file.getSavedFileName()).delete();
            }

            return ResponseEntity.internalServerError().headers(headers).body(new ResponseMessage(500, "업로드 실패", null));
        }
    }

    /* 모임 썸네일 출력 */
    @GetMapping("/img/partyboard/{partyBoardNumber}/thumbnail/{imageName}")
    public ResponseEntity<Resource> getPartyBoardImage(
            @PathVariable Integer partyBoardNumber
            ,@PathVariable String imageName
    ) {

        Resource resource = resourceLoader.getResource("classpath:static/img/party_board/" + partyBoardNumber + "/thumbnail/" + imageName);

        if(resource.exists()) {
            String contentType = "image/png"; // 기본 MIME 타입 설정
            if (resource.getFilename().endsWith(".jpg") || resource.getFilename().endsWith(".jpeg")) {
                contentType = "image/jpeg";
            }
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);

        } else {
            return ResponseEntity.notFound().build();
        }

    }


    /* 카테고리 이미지 출력 */
    @GetMapping("/img/category/{categoryNumber}")
    public ResponseEntity<?> getCategoryImage(
            @PathVariable Integer categoryNumber
    ) {

        Resource resource = resourceLoader.getResource(imgPath + "/category/" + categoryNumber + "/thumbnail/");
        Resource foundResource = null;

        try {

            if(resource.exists()) {
                String imageDir = resource.getFile().getAbsolutePath();

                Optional<Path> optionalPath = Files.walk(Paths.get(imageDir))
                        .filter(Files::isRegularFile)
                        .findFirst();

                if (optionalPath.isPresent()) {
                    String foundPath = optionalPath.get().toString();
                    foundResource = resourceLoader.getResource("file:" + foundPath);

                    if(foundResource.exists()) {
                        String contentType = "image/png"; // 기본 MIME 타입 설정
                        if (foundResource.getFilename().endsWith(".jpg") || foundResource.getFilename().endsWith(".jpeg")) {
                            contentType = "image/jpeg";
                        }
                        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(foundResource);

                    } else {
                        return ResponseEntity.notFound().build();
                    }

                } else {
                    return ResponseEntity.notFound().build();
                }

            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
  
      //취미 썸네일 이미지 조회
    @GetMapping("/img/hobbyboard/{hobbyNumber}")
    public ResponseEntity<Resource> getHobbyImg(@PathVariable String hobbyNumber) {
        try {
            // 확장자를 동적으로 확인
            String[] extensions = {".png", ".jpg", ".jpeg"};
            Resource resource = null;

            for (String ext : extensions) {
                resource = resourceLoader.getResource("classpath:static/img/hobby_board/" + hobbyNumber + ext);
                if (resource.exists()) {
                    break; // 첫 번째로 발견된 파일 반환
                }
            }

            if (resource != null && resource.exists() && resource.isReadable()) {
                String contentType = "image/png"; // 기본 MIME 타입 설정
                if (resource.getFilename().endsWith(".jpg") || resource.getFilename().endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

}
