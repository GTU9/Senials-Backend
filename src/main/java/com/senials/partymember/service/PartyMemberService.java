package com.senials.partymember.service;

import com.senials.common.mapper.PartyMemberMapper;
import com.senials.common.mapper.PartyMemberMapperImpl;
import com.senials.common.mapper.UserMapper;
import com.senials.common.mapper.UserMapperImpl;
import com.senials.meetmember.repository.MeetMemberRepository;
import com.senials.partyboard.entity.PartyBoard;
import com.senials.partyboard.repository.PartyBoardRepository;
import com.senials.partymember.PartyMemberDTO;
import com.senials.partymember.entity.PartyMember;
import com.senials.partymember.repository.PartyMemberRepository;
import com.senials.user.dto.UserDTOForPublic;
import com.senials.user.entity.User;
import com.senials.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PartyMemberService {

    private final PartyMemberMapper partyMemberMapper;
    private final UserMapper userMapper;
    private final PartyBoardRepository partyBoardRepository;
    private final PartyMemberRepository partyMemberRepository;
    private final UserRepository userRepository;
    private final MeetMemberRepository meetMemberRepository;

    public PartyMemberService(
            PartyMemberMapperImpl partyMemberMapperImpl
            , UserMapperImpl userMapperImpl
            , PartyBoardRepository partyBoardRepository
            , PartyMemberRepository partyMemberRepository,
            UserRepository userRepository, MeetMemberRepository meetMemberRepository) {
        this.partyMemberMapper = partyMemberMapperImpl;
        this.userMapper = userMapperImpl;
        this.partyBoardRepository = partyBoardRepository;
        this.partyMemberRepository = partyMemberRepository;
        this.userRepository = userRepository;
        this.meetMemberRepository = meetMemberRepository;
    }


    /* 모임 멤버 랜덤 4명 조회 */
    public List<UserDTOForPublic> getRandomPartyMembers(int partyBoardNumber) {

        List<PartyMember> partyMemberList = partyMemberRepository.find4ByPartyBoardNumber(partyBoardNumber);

        List<UserDTOForPublic> userDTOList = partyMemberList.stream().map(partyMember -> {
            return userMapper.toUserDTOForPublic(partyMember.getUser());
        }).toList();

        return userDTOList;
    }

    /* 모임 멤버 페이지 조회 */
    public List<PartyMemberDTO> getPartyMembers (int userNumber, int partyBoardNumber, int pageNumber, int pageSize) {

        PartyBoard partyBoard = partyBoardRepository.findById(partyBoardNumber)
                .orElseThrow(IllegalArgumentException::new);

        User user = userRepository.findById(userNumber)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다. (로그인 필요)"));

        /* 모임장 확인 */
        if(partyBoard.getUser().getUserNumber() != userNumber) {
            PartyMember partyMember = partyMemberRepository.findByPartyBoardAndUser(partyBoard, user);
            if(partyMember == null) {
                throw new IllegalArgumentException("잘못된 요청입니다. (모임 멤버 아님)");
            }
        }

        /* 모임 번호에 해당하는 멤버 리스트 도출 */
        Page<PartyMember> partyMemberList = partyMemberRepository.findAllByPartyBoard(partyBoard, PageRequest.of(pageNumber, pageSize));

        /* 멤버 리스트를 통해 유저 정보 도출 */
        List<PartyMemberDTO> partyMemberDTOList = partyMemberList.stream()
                .map(partyMember -> {
                    PartyMemberDTO partyMemberDTO = partyMemberMapper.toPartyMemberDTO(partyMember);
                    partyMemberDTO.setMeetJoinedCnt(meetMemberRepository.countAllByPartyMember(partyMember));
                    return partyMemberDTO;
                }).toList();

        return partyMemberDTOList;
    }


    /* 모임 멤버 전체 조회 */
    public List<UserDTOForPublic> getPartyMembers (int partyBoardNumber) {

        PartyBoard partyBoard = partyBoardRepository.findById(partyBoardNumber)
                .orElseThrow(IllegalArgumentException::new);

        /* 모임 번호에 해당하는 멤버 리스트 도출 */
        List<PartyMember> partyMemberList = partyMemberRepository.findAllByPartyBoard(partyBoard);

        /* 멤버 리스트를 통해 유저 정보 도출 */
        List<UserDTOForPublic> userDTOForPublicList = partyMemberList.stream()
                .map(partyMember -> userMapper.toUserDTOForPublic(partyMember.getUser()))
                .toList();

        return userDTOForPublicList;
    }


    /* 모임 참가 */
    public int registerPartyMember (int userNumber, int partyBoardNumber) {
        User user = userRepository.findById(userNumber)
                .orElseThrow(IllegalArgumentException::new);

        PartyBoard partyBoard = partyBoardRepository.findById(partyBoardNumber)
                .orElseThrow(IllegalArgumentException::new);

        if (partyBoard.getPartyBoardStatus() == 1) {
            throw new IllegalArgumentException("잘못된 요청 (모집 완료 상태인 모임)");
        }

        PartyMember newMember = new PartyMember(0, partyBoard, user);

        // partyBoard.registerPartyMember(newMember);
        PartyMember savedMember = partyMemberRepository.save(newMember);
        if(savedMember != null) {
            return 1;
        } else {
            return 0;
        }
    }


    /* 모임 탈퇴 */
    public void unregisterPartyMember (int userNumber, int partyBoardNumber) {

        try {
            PartyBoard targetPartyBoard = partyBoardRepository.findById(partyBoardNumber)
                    .orElseThrow(IllegalArgumentException::new);

            User targetUser = userRepository.findById(userNumber)
                    .orElseThrow(IllegalArgumentException::new);


            PartyMember targetPartyMember = partyMemberRepository.findByPartyBoardAndUser(targetPartyBoard, targetUser);


            partyMemberRepository.delete(targetPartyMember);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("모임 탈퇴에 실패하였습니다.");
        }

    }


    /* 모임 추방 */
    public void kickPartyMember (int userNumber, List<Integer> kickList, int partyBoardNumber) {

        PartyBoard targetPartyBoard = partyBoardRepository.findById(partyBoardNumber)
                .orElseThrow(IllegalArgumentException::new);

        User user = userRepository.findById(userNumber)
                .orElseThrow(IllegalArgumentException::new);


        /* 모임장 검사 */
        if(user.getUserNumber() != targetPartyBoard.getUser().getUserNumber()) {
            throw new IllegalArgumentException("잘못된 요청입니다. (모임장 아님)");
        }


        for(int partyMemberNumber : kickList) {
            PartyMember targetPartyMember = partyMemberRepository.findById(partyMemberNumber)
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

            if (targetPartyMember.getPartyBoard().getPartyBoardNumber() != partyBoardNumber) {
                throw new IllegalArgumentException("잘못된 요청입니다.");
            }

            partyMemberRepository.delete(targetPartyMember);
        }

    }


    /* 모임 멤버 여부 */
    public boolean checkIsMember (int userNumber, int partyBoardNumber) {

        return partyMemberRepository.existsByUser_UserNumberAndPartyBoard_PartyBoardNumber(userNumber, partyBoardNumber);
    }
}
