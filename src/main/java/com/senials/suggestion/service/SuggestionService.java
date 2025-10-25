package com.senials.suggestion.service;

import com.senials.common.mapper.SuggestionMapper;
import com.senials.suggestion.dto.SuggestionDTO;
import com.senials.suggestion.entity.Suggestion;
import com.senials.suggestion.repository.SuggestionRepository;
import com.senials.user.entity.User;
import com.senials.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SuggestionService {

    private final SuggestionRepository suggestionRepository;
    private final SuggestionMapper suggestionMapper;
    private final UserRepository userRepository;

    public SuggestionService(SuggestionRepository suggestionRepository,
                             SuggestionMapper suggestionMapper,
                             UserRepository userRepository) {
        this.suggestionRepository = suggestionRepository;
        this.suggestionMapper = suggestionMapper;
        this.userRepository = userRepository;
    }

    //건의사항을 생성
    public Suggestion saveSuggestion(SuggestionDTO suggestionDTO, int userNumber){
        suggestionDTO.setSuggestionDate(LocalDateTime.now());
        User user=userRepository.findById(userNumber).orElseThrow(()->new IllegalArgumentException("해당 유저 번호가 존재하지 않습니다: "));
        Suggestion suggestion=suggestionMapper.toSuggestionEntity(suggestionDTO);
        suggestion.initializeUser(user);
        Suggestion saveSuggestion=suggestionRepository.save(suggestion);

        return saveSuggestion;
    }

    //건의 전체 조회
    public List<SuggestionDTO> getSuggestionList() {
        List<Suggestion> suggestionList = suggestionRepository.findAll();
        List<SuggestionDTO> suggestionDTOList = suggestionList.stream()
                .map(suggestion ->{
                   SuggestionDTO suggestionDTO= suggestionMapper.toSuggestionDTO(suggestion);
                   suggestionDTO.setUserNumber(suggestion.getUser().getUserNumber());
                    suggestionDTO.setUserName(suggestion.getUser().getUserName());
                   return suggestionDTO;
                })
                .collect(Collectors.toList());
        return suggestionDTOList;
    }

    //특정 건의 조회
    public SuggestionDTO getSuggestionById(int suggestionNumber){
        Suggestion suggestion=suggestionRepository.findById(suggestionNumber).orElseThrow(() -> new IllegalArgumentException("해당 건의는 존재하지 않습니다: " + suggestionNumber));
        SuggestionDTO suggestionDTO=suggestionMapper.toSuggestionDTO(suggestion);

        return suggestionDTO;
    }

    //특정 건의 삭제
    public void deleteSuggestionById(int suggestionNumber){
        suggestionRepository.deleteById(suggestionNumber);
    }
}
