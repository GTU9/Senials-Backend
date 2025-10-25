package com.senials.common.mapper;

import com.senials.hobbyboard.dto.HobbyDTOForCard;
import com.senials.hobbyboard.entity.Hobby;
import com.senials.hobbyboard.dto.HobbyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HobbyMapper {
    HobbyDTO toHobbyDTO(Hobby hobby);

    HobbyDTOForCard toHobbyDTOForCard(Hobby hobby);

}
