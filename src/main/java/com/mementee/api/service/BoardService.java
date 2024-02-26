package com.mementee.api.service;

import com.mementee.api.controller.boardDTO.WriteBoardRequest;
import com.mementee.api.domain.Board;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.enumtype.BoardType;
import com.mementee.api.domain.subdomain.ScheduleTime;
import com.mementee.api.repository.BoardRepository;
import com.mementee.api.repository.BoardRepositorySub;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {

    private final MemberService memberService;
    private final BoardRepository boardRepository;
    private final BoardRepositorySub boardRepositorySub;

    //멘토가 자신의 스케줄을 입력시 중복되는 시간을 고를경우 ex) 01:00:00 ~ 03:00:00 와 02:00:00 ~ 05:00:00를 동시에 할수 없음
    public void isDuplicateTime(List<ScheduleTime> times) {
        Set<LocalTime> timeSet = new HashSet<>();
        for (ScheduleTime time : times) {
            LocalTime startTime = time.getStartTime();
            LocalTime finishTime = time.getEndTime();
            for (LocalTime existingTime : timeSet) {
                if ((startTime.isAfter(existingTime) && startTime.isBefore(existingTime.plusHours(2))) ||
                        (finishTime.isAfter(existingTime) && finishTime.isBefore(existingTime.plusHours(2)))) {
                    throw new IllegalArgumentException("상담 시간이 중복됩니다.");
                }
            }
            timeSet.add(startTime);
            timeSet.add(finishTime);
        }
    }

    public void isCheckBoardMember(Member member, Board board){
        if(member != board.getMember())
            throw new IllegalArgumentException("권한이 없습니다.");        //작성자가 아닐경우
    }

    @Transactional
    public Long saveBoard(WriteBoardRequest request, String authorizationHeader) {
        Member member = memberService.getMemberByToken(authorizationHeader);

        Board board = new Board(request.getTitle(), request.getContent(), request.getConsultTime(), request.getBoardType(), member,
                request.getTimes(), request.getAvailableDays());

        member.getBoards().add(board);

        boardRepository.saveBoard(board);
        return board.getId();
    }


    @Transactional
    public Long modifyBoard(WriteBoardRequest request, String authorizationHeader, Long boardId) {
        Member member = memberService.getMemberByToken(authorizationHeader);
        Board board = findBoard(boardId);

        isCheckBoardMember(member, board);

        board.modifyBoards(request.getTitle(), request.getContent(), request.getConsultTime(),
                request.getBoardType(), request.getTimes(), request.getAvailableDays());

        boardRepository.saveBoard(board);
        return board.getId();
    }

    @Transactional
    public void deleteBoard(String authorizationHeader, Long boardId) {
        Member member = memberService.getMemberByToken(authorizationHeader);
        Board board = findBoard(boardId);

        isCheckBoardMember(member, board);

        board.getTimes().clear();
        board.getUnavailableTimes().clear();

        boardRepository.deleteBoard(board);
    }
    public Board findBoard(Long boardId){
        return boardRepository.findBoard(boardId);
    }

    //멘토, 멘티 별로 전체 게시물 조회(무한 스크롤 이용)
    public Slice<Board>findAllByBoardType(BoardType boardType, Pageable pageable){
        return boardRepositorySub.findAllByBoardType(boardType, pageable);
    }

    //멘토, 멘티 학교 별로 게시물 조회(무한 스크롤 이용)
    public Slice<Board>findAllByBoardTypeAndSchoolName(BoardType boardType, String schoolName,Pageable pageable){
        return boardRepositorySub.findAllByBoardTypeAndSchoolName(boardType, schoolName, pageable);
    }
}
