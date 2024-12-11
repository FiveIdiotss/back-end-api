package com.team.mementee.api.service;

import com.team.mementee.api.domain.*;
import com.team.mementee.api.domain.document.EsBoard;
import com.team.mementee.api.domain.document.EsBoardRepository;
import com.team.mementee.api.domain.enumtype.BoardCategory;
import com.team.mementee.api.dto.boardDTO.*;
import com.team.mementee.api.repository.board.BoardImageRepository;
import com.team.mementee.api.repository.board.BoardRepository;
import com.team.mementee.api.repository.board.FavoriteRepository;
import com.team.mementee.api.validation.BoardValidation;
import com.team.mementee.api.validation.MemberValidation;
import com.team.mementee.exception.notFound.BoardNotFound;
import com.team.mementee.exception.unauthorized.RequiredLoginException;
import com.team.mementee.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final S3Service s3Service;
    private final BoardRepository boardRepository;
    private final EsBoardRepository esBoardRepository;
    private final FavoriteRepository favoriteRepository;
    private final BoardImageRepository boardImageRepository;
    private final MemberService memberService;
    private final RedisTemplate<String, String> redisTemplate;

    public List<Board> findAllByTitleContaining(String query) {
        return boardRepository.findAllByTitleContaining(query);
    }

    public List<Board> findAllByContentContaining(String query) {
        return boardRepository.findAllByContentContaining(query);
    }

//    public List<Board> findAllByTitleContaining(String query) {
//        List<Long> ids = esBoardRepository.findAllByTitle(query)
//                .stream()
//                .map(EsBoard::getBoardId)
//                .collect(Collectors.toList());
//
//        return boardRepository.findAllById(ids);
//    }
//
//    public List<Board> findAllByContentContaining(String query) {
//        List<Long> ids = esBoardRepository.findAllByContent(query)
//                .stream()
//                .map(EsBoard::getBoardId)
//                .collect(Collectors.toList());
//
//        return boardRepository.findAllById(ids);
//    }

    //게시글 조회시 필요한 Info
    public BoardInfoResponse createBoardInfoResponse(Long boardId, String authorizationHeader) {
        Board board = findBoardById(boardId);
        List<BoardImageDTO> boardImageDTOs = BoardImageDTO.createBoardImageDTOs(findBoarImagesByBoard(board));
        BoardDTO boardDTO = createBoardDTO(board, authorizationHeader);
        return BoardInfoResponse.createBoardInfoResponse(boardDTO, boardImageDTOs, board);
    }

    //게시글 조회시 필요한 DTO
    public BoardDTO createBoardDTO(Board board, String authorizationHeader) {
        if (authorizationHeader == null)
            return BoardDTO.createBoardDTO(board, false, findRepresentImage(board));

        Member member = memberService.findMemberByToken(authorizationHeader);
        return BoardDTO.createBoardDTO(board, BoardValidation.isFavorite(findFavoriteByMemberAndBoard(member, board)), findRepresentImage(board));
    }

    //게시글 목록시 필요한 DTO List
    public List<BoardDTO> createBoardDTOs(List<Board> boards, String authorizationHeader) {
        if (authorizationHeader == null) {
            return boards.stream()
                    .map(b -> BoardDTO.createBoardDTO(b, false, findRepresentImage(b)))
                    .collect(Collectors.toList());
        }
        Member member = memberService.findMemberByToken(authorizationHeader);
        return boards.stream()
                .map(b -> BoardDTO.createBoardDTO(b, BoardValidation.isFavorite(findFavoriteByMemberAndBoard(member, b)), findRepresentImage(b)))
                .collect(Collectors.toList());
    }

    //id로 Board 조회
    public Board findBoardById(Long boardId) {
        Optional<Board> board = boardRepository.findBoardById(boardId);
        if (board.isEmpty())
            throw new BoardNotFound();
        return board.get();
    }

    //게시물에 속한 이미지 조회
    public List<BoardImage> findBoarImagesByBoard(Board board) {
        return boardImageRepository.findBoardImagesByBoard(board);
    }

    //게시물의 대표 이미지
    public String findRepresentImage(Board board) {
        List<BoardImage> images = boardImageRepository.findFirstByBoardOrderByIdAsc(board);
        if (!images.isEmpty())
            return images.get(0).getBoardImageUrl();
        return "";
    }


    //로그인한 유저가 즐겨찾기한 게시물 목록
    public Page<Board> findFavoritesByMember(String authorizationHeader, Pageable pageable) {
        Member member = memberService.findMemberByToken(authorizationHeader);
        return boardRepository.findFavoritesByMember(member, pageable);
    }

    //특정 멤버가 쓴 글목록
    public Page<Board> findBoardsByMember(Long memberId, Pageable pageable) {
        Member member = memberService.findMemberById(memberId);
        return boardRepository.findBoardsByMember(member, pageable);
    }

    //Member 와 Board 에 대한 즐겨찾기 조회
    public Optional<Favorite> findFavoriteByMemberAndBoard(Member member, Board board) {
        return favoriteRepository.findFavoriteByMemberAndBoard(member, board);
    }

    //게시물 등록시 이미지 추출 후 엔티티 생성
    public void saveBoardImageUrl(List<MultipartFile> multipartFiles, Board board) {
        if (multipartFiles == null) return;
        for (MultipartFile multipartFile : multipartFiles) {
            String url = s3Service.saveFile(multipartFile);
            BoardImage boardImage = new BoardImage(board, url);
            boardImageRepository.save(boardImage);
        }
    }

    //게시글에 속한 이미지 수정
    public void modifyBoardImage(List<MultipartFile> multipartFiles, Board board) {
        boardImageRepository.deleteBoardImageByBoard(board);

        if (multipartFiles == null) return;
        for (MultipartFile multipartFile : multipartFiles) {
            String url = s3Service.saveFile(multipartFile);
            BoardImage boardImage = new BoardImage(board, url);
            boardImageRepository.save(boardImage);
        }
    }

    //게시물 등록
    @Transactional
    public Long saveBoard(WriteBoardRequest request, List<MultipartFile> multipartFiles, String authorizationHeader) {
        Member member = memberService.findMemberByToken(authorizationHeader);
        Board board = new Board(request.getTitle(), request.getIntroduce(), request.getTarget(), request.getContent(), request.getConsultTime(),
                request.getBoardCategory(), request.getPlatform(), member, request.getTimes(), request.getAvailableDays());
        saveBoardImageUrl(multipartFiles, board);
        boardRepository.save(board);
        esBoardRepository.save(EsBoard.toDocument(board));
        return board.getId();
    }

    //게시물 수정
    @Transactional
    public void modifyBoard(WriteBoardRequest request, String authorizationHeader, Long boardId, List<MultipartFile> multipartFiles) {
        Board board = findBoardById(boardId);
        MemberValidation.isCheckMe(memberService.findMemberByToken(authorizationHeader), board.getMember());
        modifyBoardImage(multipartFiles, board);
        board.modifyBoard(request);

        EsBoard esBoard = esBoardRepository.findByBoardId(boardId);
        EsBoard updateEsBoard = EsBoard.updatedEsBoard(esBoard.getId(), board);

        esBoardRepository.save(updateEsBoard);
    }

    //즐겨찾기 추가
    @Transactional
    public void addFavoriteBoard(String authorizationHeader, Long boardId) {
        Member member = memberService.findMemberByToken(authorizationHeader);
        Board board = findBoardById(boardId);

        BoardValidation.isCheckAddFavorite(findFavoriteByMemberAndBoard(member, board));

        Favorite favorite = new Favorite(member, board);
        favoriteRepository.save(favorite);
    }

    //즐겨찾기 제거
    @Transactional
    public void removeFavoriteBoard(String authorizationHeader, Long boardId) {
        Member member = memberService.findMemberByToken(authorizationHeader);
        Board board = findBoardById(boardId);

        Favorite favorite = BoardValidation.isCheckRemoveFavorite(findFavoriteByMemberAndBoard(member, board));
        favoriteRepository.delete(favorite);
    }


    public Page<Board> findBoardsByFilter(String authorizationHeader,
                                          boolean schoolFilter,
                                          boolean favoriteFilter,
                                          BoardCategory boardCategory,
                                          String keyWord,
                                          Pageable pageable) {
        Member member = null;
        School school = null;

        if (schoolFilter || favoriteFilter) {
            if (authorizationHeader == null)
                throw new RequiredLoginException();
            member = memberService.findMemberByToken(authorizationHeader);
            school = member.getSchool();
        }

        String searchKeyWord = BoardValidation.isContainKeyWord(keyWord);

        //카테고리
        if (boardCategory != null && searchKeyWord == null && !schoolFilter && !favoriteFilter)
            return boardRepository.findBoardsByBoardCategory(boardCategory, pageable);

        //검색
        if (boardCategory == null && searchKeyWord != null && !schoolFilter && !favoriteFilter) {
            redisTemplate.opsForZSet().incrementScore("popular_keywords", keyWord, 1);
            return boardRepository.findBoardsByKeyWord(searchKeyWord, pageable);
        }


        //내 학교
        if (boardCategory == null && searchKeyWord == null && schoolFilter && !favoriteFilter)
            return boardRepository.findBoardsByMemberSchool(school, pageable);

        //즐겨찾기
        if (boardCategory == null && searchKeyWord == null && !schoolFilter && favoriteFilter)
            return boardRepository.findFavoritesByMember(member, pageable);

        //카테고리, 검색
        if (boardCategory != null && searchKeyWord != null && !schoolFilter && !favoriteFilter) {
            redisTemplate.opsForZSet().incrementScore("popular_keywords", keyWord, 1);
            return boardRepository.findBoardsByBoardCategoryAndKeyWord(boardCategory, searchKeyWord, pageable);
        }

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
        if (boardCategory == null && searchKeyWord != null && !schoolFilter && favoriteFilter) {
            redisTemplate.opsForZSet().incrementScore("popular_keywords", keyWord, 1);
            return boardRepository.findBoardsByKeyWordAndFavorite(searchKeyWord, member, pageable);
        }

        //내 학교, 즐겨찾기
        if (boardCategory == null && searchKeyWord == null && schoolFilter && favoriteFilter)
            return boardRepository.findBoardsByMemberSchoolAndFavorite(school, member, pageable);

        //카테고리, 내 학교, 즐겨찾기
        if (boardCategory != null && searchKeyWord == null && schoolFilter && favoriteFilter)
            return boardRepository.findBoardsByBoardCategoryAndMemberSchoolAndFavorite(boardCategory, school, member, pageable);

        //카테고리, 검색, 즐거찾기
        if (boardCategory != null && searchKeyWord != null && !schoolFilter && favoriteFilter) {
            redisTemplate.opsForZSet().incrementScore("popular_keywords", keyWord, 1);
            return boardRepository.findBoardsByBoardCategoryAndKeyWordAndFavorite(boardCategory, searchKeyWord, member, pageable);
        }

        //카테고리, 검색, 내 학교
        if (boardCategory != null && searchKeyWord != null && schoolFilter && !favoriteFilter) {
            redisTemplate.opsForZSet().incrementScore("popular_keywords", keyWord, 1);
            return boardRepository.findBoardsByBoardCategoryAndMemberSchoolAndKeyWord(boardCategory, school, searchKeyWord, pageable);
        }

        //검색, 내 학교, 즐겨찾기;
        if (boardCategory == null && searchKeyWord != null && schoolFilter && favoriteFilter) {
            redisTemplate.opsForZSet().incrementScore("popular_keywords", keyWord, 1);
            return boardRepository.findBoardsByMemberSchoolAndKeyWordAndFavorite(school, searchKeyWord, member, pageable);
        }

        //카테고리, 검색, 내 학교, 즐거챶기
        if (boardCategory != null && searchKeyWord != null && schoolFilter && favoriteFilter) {
            redisTemplate.opsForZSet().incrementScore("popular_keywords", keyWord, 1);
            return boardRepository.findBoardsByMemberSchoolAndKeyWordAndBoardCategoryAndFavorite(school, searchKeyWord, boardCategory, member, pageable);
        }
        //필터 없는 상태
        return boardRepository.findAll(pageable);
    }
}
