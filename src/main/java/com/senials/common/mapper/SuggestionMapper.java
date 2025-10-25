package com.senials.common.mapper;

import com.senials.suggestion.dto.SuggestionDTO;
import com.senials.suggestion.entity.Suggestion;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SuggestionMapper {
    SuggestionDTO toSuggestionDTO(Suggestion suggestion);
    Suggestion toSuggestionEntity(SuggestionDTO suggestionDTO);
}
