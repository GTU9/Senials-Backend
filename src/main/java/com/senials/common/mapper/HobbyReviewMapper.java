package com.senials.common.mapper;

import com.senials.hobbyreview.dto.HobbyReviewDTO;
import com.senials.hobbyreview.entity.HobbyReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HobbyReviewMapper {

    @Mapping(source = "user.userNumber", target = "userNumber")
    @Mapping(source = "user.userNickname", target = "userName")
    @Mapping(source = "hobby.hobbyNumber", target = "hobbyNumber")
    HobbyReviewDTO toHobbyReviewDTO(HobbyReview hobbyReview);

    HobbyReview toHobbyReviewEntity(HobbyReviewDTO hobbyReviewDTO);

}
