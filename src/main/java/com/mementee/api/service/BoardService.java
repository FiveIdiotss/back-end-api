package com.mementee.api.service;

import com.mementee.api.domain.*;
import com.mementee.api.domain.enumtype.BoardCategory;
import com.mementee.api.dto.boardDTO.BoardDTO;
import com.mementee.api.dto.boardDTO.BoardImageDTO;
import com.mementee.api.dto.boardDTO.BoardInfoResponse;
import com.mementee.api.dto.boardDTO.WriteBoardRequest;
import com.mementee.api.repository.board.BoardImageRepository;
import com.mementee.api.repository.board.BoardRepository;
import com.mementee.api.repository.board.FavoriteRepository;
import com.mementee.api.validation.BoardValidation;
import com.mementee.api.validation.MemberValidation;
import com.mementee.exception.notFound.BoardNotFound;
import com.mementee.exception.unauthorized.RequiredLoginException;
import com.mementee.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final S3Service s3Service;
    private final BoardRepository boardRepository;
    private final FavoriteRepository favoriteRepository;
    private final BoardImageRepository boardImageRepository;
    private final MemberService memberService;

    //게시글 조회시 필요한 Info
    public BoardInfoResponse createBoardInfoResponse(Long boardId, String authorizationHeader){
        Board board = findBoardById(boardId);
        List<BoardImageDTO> boardImageDTOs = BoardImageDTO.createBoardImageDTOs(findBoarImagesByBoard(board));
        BoardDTO boardDTO = createBoardDTO(board, authorizationHeader);
        return BoardInfoResponse.createBoardInfoResponse(boardDTO, boardImageDTOs, board);
    }

    //게시글 조회시 필요한 DTO
    public BoardDTO createBoardDTO(Board board, String authorizationHeader){
        if(authorizationHeader == null)
            return BoardDTO.createBoardDTO(board, false);

        Member member = memberService.findMemberByToken(authorizationHeader);
        return BoardDTO.createBoardDTO(board, BoardValidation.isFavorite(findFavoriteByMemberAndBoard(member, board)));
    }

    //게시글 목록시 필요한 DTO List
    public List<BoardDTO> createBoardDTOs(List<Board> boards, String authorizationHeader){
        if(authorizationHeader == null)
            return BoardDTO.createBoardDTOs(boards, false);
        Member member = memberService.findMemberByToken(authorizationHeader);
        return boards.stream()
                .map(b -> BoardDTO.createBoardDTO(b, BoardValidation.isFavorite(findFavoriteByMemberAndBoard(member, b))))
                .collect(Collectors.toList());
    }

    //id로 Board 조회
    public Board findBoardById(Long boardId){
        Optional<Board> board = boardRepository.findById(boardId);
        if(board.isEmpty())
            throw new BoardNotFound();
        return board.get();
    }

    //게시물에 속한 이미지 조회
    public List<BoardImage> findBoarImagesByBoard(Board board){
        return boardImageRepository.findBoardImagesByBoard(board);
    }

    //잔체 게시물 조회
    public Page<Board> findAllByPage(Pageable pageable){
        return boardRepository.findAll(pageable);
    }

    //로그인한 유저가 즐겨찾기한 게시물 목록
    public Page<Board> findFavoritesByMember(String authorizationHeader, Pageable pageable){
        Member member = memberService.findMemberByToken(authorizationHeader);
        return boardRepository.findFavoritesByMember(member, pageable);
    }

    //특정 멤버가 쓴 글목록
    public Page<Board> findBoardsByMember(Long memberId, Pageable pageable){
        Member member = memberService.findMemberById(memberId);
        return boardRepository.findBoardsByMember(member, pageable);
    }

    //Member 와 Board 에 대한 즐겨찾기 조회
    public Optional<Favorite> findFavoriteByMemberAndBoard(Member member, Board board){
        return favoriteRepository.findFavoriteByMemberAndBoard(member, board);
    }

    //게시물 등록시 이미지 추출 후 엔티티 생성
    @Transactional
    public List<BoardImage> saveBoardImageUrl(List<MultipartFile> multipartFiles) {
        List<BoardImage> boardImages = new ArrayList<>();
        if (multipartFiles == null) {
            return boardImages;
        }
        for(MultipartFile multipartFile : multipartFiles){
            String url = s3Service.saveFile(multipartFile);
            BoardImage boardImage = new BoardImage(url);
            boardImages.add(boardImage);
            boardImageRepository.save(boardImage);
        }
        return boardImages;
    }

    //게시물 등록
    @Transactional
    public void saveBoard(WriteBoardRequest request, List<MultipartFile> multipartFiles, String authorizationHeader) throws IOException {
        Member member = memberService.findMemberByToken(authorizationHeader);
        List<BoardImage> boardImages = saveBoardImageUrl(multipartFiles);
        Board board;
        if(boardImages.isEmpty()){
            board = new Board(request.getTitle(), request.getIntroduce(), request.getTarget(), request.getContent(), request.getConsultTime(),
                    request.getBoardCategory(), member, request.getTimes(), request.getAvailableDays());
        }else {
            board = new Board(request.getTitle(), request.getIntroduce(), request.getTarget(), request.getContent(), request.getConsultTime(),
                    request.getBoardCategory(), member, request.getTimes(), request.getAvailableDays(), boardImages);
            board.addBoardImage(boardImages);

            for(BoardImage boardImage : boardImages){
                boardImage.setBoard(board);
            }
        }
        member.addBoard(board);
        boardRepository.save(board);
    }

    //게시물 수정
    @Transactional
    public void modifyBoard(WriteBoardRequest request, String authorizationHeader, Long boardId) {
        Board board = findBoardById(boardId);
        MemberValidation.isCheckMe(memberService.findMemberByToken(authorizationHeader), board.getMember());

        board.modifyBoards(request.getTitle(), request.getIntroduce(), request.getTarget(),
                request.getContent(), request.getConsultTime(), request.getBoardCategory(),
                 request.getTimes(), request.getAvailableDays());
    }


    //즐겨찾기 추가
    @Transactional
    public void addFavoriteBoard(String authorizationHeader, Long boardId){
        Member member = memberService.findMemberByToken(authorizationHeader);
        Board board = findBoardById(boardId);

        BoardValidation.isCheckAddFavorite(findFavoriteByMemberAndBoard(member,board));

        Favorite favorite = new Favorite(member, board);
        member.addFavoriteBoard(favorite);

        favoriteRepository.save(favorite);
    }

    //즐겨찾기 제거
    @Transactional
    public void removeFavoriteBoard(String authorizationHeader, Long boardId){
        Member member = memberService.findMemberByToken(authorizationHeader);
        Board board = findBoardById(boardId);

        Favorite favorite = BoardValidation.isCheckRemoveFavorite(findFavoriteByMemberAndBoard(member, board));

        member.removeFavoriteBoard(favorite);
        favoriteRepository.delete(favorite);
    }


    public Page<Board> findBoardsByFilter(String authorizationHeader,
                                          boolean schoolFilter,
                                          boolean favoriteFilter,
                                          BoardCategory boardCategory,
                                          String keyWord,
                                          Pageable pageable){
        Member member = null;
        School school = null;

        if(schoolFilter || favoriteFilter){
            if(authorizationHeader == null)
                throw new RequiredLoginException();
            member = memberService.findMemberByToken(authorizationHeader);
            school = member.getSchool();
        }

        String searchKeyWord = BoardValidation.isContainKeyWord(keyWord);

        //카테고리
        if (boardCategory != null && searchKeyWord == null && !schoolFilter && !favoriteFilter)
            return boardRepository.findBoardsByBoardCategory(boardCategory, pageable);

        //검색
        if (boardCategory == null && searchKeyWord != null && !schoolFilter && !favoriteFilter)
            return boardRepository.findBoardsByKeyWord(searchKeyWord, pageable);


        //내 학교
        if (boardCategory == null && searchKeyWord == null && schoolFilter && !favoriteFilter)
            return boardRepository.findBoardsByMemberSchool(school, pageable);

        //즐겨찾기
        if (boardCategory == null && searchKeyWord == null && !schoolFilter && favoriteFilter )
            return boardRepository.findFavoritesByMember(member, pageable);

        //카테고리, 검색
        if (boardCategory != null && searchKeyWord != null && !schoolFilter && !favoriteFilter)
            return boardRepository.findBoardsByBoardCategoryAndKeyWord(boardCategory, searchKeyWord, pageable);


        //카테고리, 내 학교
        if (boardCategory != null && searchKeyWord == null && schoolFilter && !favoriteFilter)
            return boardRepository.findBoardsByBoardCategoryAndMemberSchool(boardCategory, school, pageable);

        //카테고리, 즐겨찾기
        if (boardCategory != null && searchKeyWord == null && !schoolFilter && favoriteFilter)
            return boardRepository.findBoardsByBoardCategoryAndFavorite(boardCategory, member, pageable);

        //검색, 내 학교
        if (boardCategory == null && searchKeyWord != null && schoolFilter && !favoriteFilter)
            return boardRepository.findBoardsByMemberSchoolAndKeyWord(school, searchKeyWord, pageable);

        //검색, 즐거찾기
        if (boardCategory == null && searchKeyWord != null && !schoolFilter && favoriteFilter)
            return boardRepository.findBoardsByKeyWordAndFavorite(searchKeyWord, member, pageable);

        //내 학교, 즐겨찾기
        if (boardCategory == null && searchKeyWord == null && schoolFilter && favoriteFilter)
            return boardRepository.findBoardsByMemberSchoolAndFavorite(school, member, pageable);

        //카테고리, 내 학교, 즐겨찾기
        if (boardCategory != null && searchKeyWord == null && schoolFilter && favoriteFilter)
            return boardRepository.findBoardsByBoardCategoryAndMemberSchoolAndFavorite(boardCategory, school, member, pageable);

        //카테고리, 검색, 즐거찾기
        if (boardCategory != null && searchKeyWord != null && !schoolFilter && favoriteFilter)
            return boardRepository.findBoardsByBoardCategoryAndKeyWordAndFavorite(boardCategory, searchKeyWord, member, pageable);

        //카테고리, 검색, 내 학교
        if (boardCategory != null && searchKeyWord != null && schoolFilter && !favoriteFilter)
            return boardRepository.findBoardsByBoardCategoryAndMemberSchoolAndKeyWord(boardCategory, school, searchKeyWord, pageable);

        //검색, 내 학교, 즐겨찾기;
        if (boardCategory == null && searchKeyWord != null && schoolFilter && favoriteFilter)
            return boardRepository.findBoardsByMemberSchoolAndKeyWordAndFavorite(school, searchKeyWord, member, pageable);

        //카테고리, 검색, 내 학교, 즐거챶기
        if (boardCategory != null && searchKeyWord != null && schoolFilter && favoriteFilter)
            return boardRepository.findBoardsByMemberSchoolAndKeyWordAndBoardCategoryAndFavorite(school, searchKeyWord, boardCategory, member, pageable);

        //필터 없는 상태
        return boardRepository.findAll(pageable);
    }
}
