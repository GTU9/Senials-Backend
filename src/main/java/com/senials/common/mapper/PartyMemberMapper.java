package com.senials.common.mapper;

import com.senials.partymember.PartyMemberDTO;
import com.senials.partymember.entity.PartyMember;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PartyMemberMapper {

    /* PartyMember -> PartyMemberDTO */
    PartyMemberDTO toPartyMemberDTO(PartyMember partyMember);
}
