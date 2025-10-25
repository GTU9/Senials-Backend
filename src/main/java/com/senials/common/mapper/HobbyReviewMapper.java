package com.senials.common.mapper;

import com.senials.hobbyreview.dto.HobbyReviewDTO;
import com.senials.hobbyreview.entity.HobbyReview;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HobbyReviewMapper {

    HobbyReviewDTO toHobbyReviewDTO(HobbyReview hobbyReview);

    HobbyReview toHobbyReviewEntity(HobbyReviewDTO hobbyReviewDTO);

}
