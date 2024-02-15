package mementee.mementee.api.service;

import lombok.RequiredArgsConstructor;
import mementee.mementee.api.controller.boardDTO.WriteBoardRequest;
import mementee.mementee.api.domain.Member;
import mementee.mementee.api.domain.Board;
import mementee.mementee.api.domain.enumtype.BoardType;
import mementee.mementee.api.domain.subdomain.ScheduleTime;
import mementee.mementee.api.repository.BoardRepository;
import mementee.mementee.api.repository.BoardRepositorySub;
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


    @Transactional
    public String saveBoard(WriteBoardRequest request, String authorizationHeader) {
        Member member = memberService.getMemberByToken(authorizationHeader);

        Board board = new Board(request.getTitle(), request.getContent(), request.getConsultTime(), request.getBoardType(), member,
                request.getTimes(), request.getAvailableDays());

        member.getBoards().add(board);

        boardRepository.saveBoard(board);
        return member.getName();
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

    //멘토 게시글 전체 조회
    public List<Board> findMentorBoards() {
        return boardRepository.findAllMentorBoards();
    }

    //멘티 게시글 전체 조회
    public List<Board> findMenteeBoards() {
        return boardRepository.findAllMenteeBoards();
    }

    //학교 별로 멘토 게시글 전체 조회
    public List<Board> findSchoolMentorBoards(String schoolName) {
        return boardRepository.findSchoolMentorBoards(schoolName);
    }

    //학교 별로 멘티 게시글 전체 조회
    public List<Board> findSchoolMenteeBoards(String schoolName) {
        return boardRepository.findSchoolMenteeBoards(schoolName);
    }
}
