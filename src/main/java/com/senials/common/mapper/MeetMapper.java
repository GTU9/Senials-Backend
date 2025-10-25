package com.senials.common.mapper;

import com.senials.meet.dto.MeetDTO;
import com.senials.meet.dto.MeetDTOForMember;
import com.senials.meet.entity.Meet;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MeetMapper {

    // Meet -> MeetDTO
    MeetDTO toMeetDTO(Meet meet);

    /* Meet -> MeetDTOForMember */
    MeetDTOForMember toMeetDTOForMember(Meet meet);

    /* MeetDTO -> Meet */
    Meet toMeet(MeetDTO meetDTO);
}
