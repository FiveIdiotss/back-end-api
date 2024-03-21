package com.mementee.api.service;

import com.mementee.api.domain.Favorite;
import com.mementee.api.dto.boardDTO.WriteBoardRequest;
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
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {

    private final MemberService memberService;
    private final BoardRepository boardRepository;
    private final BoardRepositorySub boardRepositorySub;

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

        return board.getId();
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


    //즐겨찾기

    //즐겨찾기 검증
    public void isCheckFavorite(Long memberId, Long boardId){
        Optional<Favorite> favorite = boardRepository.findFavoriteByMemberIdAndBoardId(memberId, boardId);
        if(favorite.isPresent())
            throw new IllegalArgumentException("이미 즐겨찾기한 게시물 입니다.");
    }

    public Optional<Favorite> isCheckMyFavorite(Long memberId, Long boardId){
        Optional<Favorite> favorite = boardRepository.findFavoriteByMemberIdAndBoardId(memberId, boardId);
        if(favorite.isEmpty())
            throw new IllegalArgumentException("즐겨찾기에 존재하지 않는 게시글입니다.");
        return favorite;
    }

    //즐겨찾기 추가
    @Transactional
    public void addFavoriteBoard(String authorizationHeader, Long boardId){
        Member member = memberService.getMemberByToken(authorizationHeader);
        Board board = findBoard(boardId);

        isCheckFavorite(member.getId(), boardId);

        Favorite favorite = new Favorite(member, board);
        member.addFavoriteBoard(favorite);

        boardRepository.saveFavorite(favorite);
    }

    @Transactional
    public void removeFavoriteBoard(String authorizationHeader, Long boardId){
        Member member = memberService.getMemberByToken(authorizationHeader);

        Optional<Favorite> favorite = isCheckMyFavorite(member.getId(), boardId);
        Favorite myFavorite = boardRepository.findFavorite(favorite.get().getId());

        member.removeFavoriteBoard(myFavorite);
        boardRepository.deleteBoard(myFavorite);
    }

    //즐겨찾기 목록
    public List<Board> findFavoriteBoards(String authorizationHeader, BoardType boardType){
        Member member = memberService.getMemberByToken(authorizationHeader);
        return boardRepository.findFavoriteBoards(member.getId(), boardType);
    }

    //멤버가 쓴 글목록
    public List<Board> findMemberBoards(Long memberId, BoardType boardType){
        return boardRepository.findMemberBoards(memberId, boardType);
    }
}
