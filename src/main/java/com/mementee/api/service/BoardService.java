package com.mementee.api.service;

import com.mementee.api.domain.BoardImage;
import com.mementee.api.domain.Favorite;
import com.mementee.api.dto.boardDTO.WriteBoardRequest;
import com.mementee.api.domain.Board;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.enumtype.BoardType;
import com.mementee.api.repository.BoardRepository;
import com.mementee.api.repository.BoardRepositorySub;
import com.mementee.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final S3Service s3Service;
    private final MemberService memberService;
    private final BoardRepository boardRepository;
    private final BoardRepositorySub boardRepositorySub;

    public void isCheckBoardMember(Member member, Board board){
        if(member != board.getMember())
            throw new IllegalArgumentException("권한이 없습니다.");        //작성자가 아닐경우
    }

    //이미지
    //이미지 조회
    public List<BoardImage> getBoardImages(Long boardId){
        return boardRepository.findBoardImages(boardId);
    }

    //게시물 등록시 이미지 추출 후 엔티티 생성
    @Transactional
    public List<BoardImage> getBoardImageUrl(List<MultipartFile> multipartFiles) throws IOException {
        List<BoardImage> boardImages = new ArrayList<>();
        if (multipartFiles == null) {
            return boardImages;
        }
        for(MultipartFile multipartFile : multipartFiles){
            String url = s3Service.saveFile(multipartFile);
            BoardImage boardImage = new BoardImage(url);
            boardImages.add(boardImage);
            boardRepository.saveBoardImage(boardImage);
        }
        return boardImages;
    }

    //-------------
    @Transactional
    public Long saveBoard(WriteBoardRequest request, List<MultipartFile> multipartFiles, String authorizationHeader) throws IOException {
        Member member = memberService.getMemberByToken(authorizationHeader);
        List<BoardImage> boardImages = getBoardImageUrl(multipartFiles);
        Board board;
        if(boardImages.isEmpty()){
            board = new Board(request.getTitle(), request.getIntroduce(), request.getTarget(), request.getContent(), request.getConsultTime(),
                    request.getBoardCategory(), request.getBoardType(), member, request.getTimes(), request.getAvailableDays());
        }else {
            board = new Board(request.getTitle(), request.getIntroduce(), request.getTarget(), request.getContent(), request.getConsultTime(),
                    request.getBoardCategory(), request.getBoardType(), member, request.getTimes(), request.getAvailableDays(), boardImages);
            board.addBoardImage(boardImages);

            for(BoardImage boardImage : boardImages){
                boardImage.setBoard(board);
            }
        }

        member.addBoard(board);
        boardRepository.saveBoard(board);
        return board.getId();
    }

    //-----------
    @Transactional
    public Long modifyBoard(WriteBoardRequest request, String authorizationHeader, Long boardId) {
        Member member = memberService.getMemberByToken(authorizationHeader);
        Board board = findBoard(boardId);

        isCheckBoardMember(member, board);

        board.modifyBoards(request.getTitle(), request.getIntroduce(), request.getTarget(),
                request.getContent(), request.getConsultTime(), request.getBoardCategory(),
                request.getBoardType(), request.getTimes(), request.getAvailableDays());

        return board.getId();
    }

    public Board findBoard(Long boardId){
        return boardRepository.findBoard(boardId);
    }

    //Slice 사용---------------------------------
    //멘토, 멘티 별로 전체 게시물 조회(무한 스크롤 이용)
    public Slice<Board>findAllByBoardType(BoardType boardType, Pageable pageable){
        return boardRepositorySub.findAllByBoardType(boardType, pageable);
    }

    //멘토, 멘티 학교 별로 게시물 조회(무한 스크롤 이용)
    public Slice<Board>findAllByBoardTypeAndSchoolName(BoardType boardType, String schoolName,Pageable pageable){
        return boardRepositorySub.findAllByBoardTypeAndSchoolName(boardType, schoolName, pageable);
    }

    //Page 사용---------------------------------
    public Page<Board> findAllByBoardTypeByPage(BoardType boardType, Pageable pageable){
        return boardRepositorySub.findAllByBoardTypeByPage(boardType, pageable);
    }

    public Page<Board> findAllByBoardTypeAndSchoolNameByPage(BoardType boardType, String schoolName, Pageable pageable){
        return boardRepositorySub.findAllByBoardTypeAndSchoolNameByPage(boardType, schoolName, pageable);
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
