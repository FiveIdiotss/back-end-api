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
    private final MemberService memberService;
    private final BoardRepository boardRepository;
    private final FavoriteRepository favoriteRepository;
    private final BoardImageRepository boardImageRepository;

    public void isCheckBoardMember(Member member, Optional<Board> board){
        if(member != board.get().getMember())
            throw new IllegalArgumentException("권한이 없습니다.");        //작성자가 아닐경우
    }

    public List<BoardDTO> createBoardDTO(List<Board> boards, String authorizationHeader){
        if(authorizationHeader == null)
            return boards.stream()
                    .map(b -> new BoardDTO(b.getId(), b.getBoardCategory(), b.getTitle(), b.getIntroduce(), b.getTarget(), b.getContent(),
                            b.getMember().getYear(), b.getMember().getSchool().getName(), b.getMember().getMajor().getName(),
                            b.getMember().getId(), b.getMember().getName(), b.getWriteTime(), false))
                    .collect(Collectors.toList());

        Member member = memberService.getMemberByToken(authorizationHeader);
        return boards.stream()
                .map(b -> new BoardDTO(b.getId(), b.getBoardCategory(), b.getTitle(), b.getIntroduce(), b.getTarget(), b.getContent(),
                        b.getMember().getYear(), b.getMember().getSchool().getName(), b.getMember().getMajor().getName(),
                        b.getMember().getId(), b.getMember().getName(), b.getWriteTime(), isFavorite(member, b)))
                .collect(Collectors.toList());
    }

    public BoardInfoResponse createBoardInfoResponse(Board board, String authorizationHeader){
        List<BoardImage> boardImages = getBoardImages(board);
        List<BoardImageDTO> boardImageDTOS = boardImages.stream().
                map(b -> new BoardImageDTO(b.getBoardImageUrl()))
                .collect(Collectors.toList());

        BoardDTO boardDTO;
        if(authorizationHeader == null) {
           boardDTO = new BoardDTO(board.getId(), board.getBoardCategory(), board.getTitle(), board.getIntroduce(),
                    board.getTarget(), board.getContent(), board.getMember().getYear(),
                    board.getMember().getSchool().getName(), board.getMember().getMajor().getName(),
                    board.getMember().getId(), board.getMember().getName(), board.getWriteTime(), false);
        } else {
            Member member = memberService.getMemberByToken(authorizationHeader);
            boardDTO = new BoardDTO(board.getId(), board.getBoardCategory(), board.getTitle(), board.getIntroduce(),
                    board.getTarget(), board.getContent(), board.getMember().getYear(),
                    board.getMember().getSchool().getName(), board.getMember().getMajor().getName(),
                    board.getMember().getId(), board.getMember().getName(), board.getWriteTime(),isFavorite(member, board));
        }

        return new BoardInfoResponse(boardDTO, board.getConsultTime(), board.getTimes(),
                board.getAvailableDays(), board.getUnavailableTimes(), boardImageDTOS);
    }


    //이미지
    //이미지 조회
    public List<BoardImage> getBoardImages(Board board){
        return boardImageRepository.findBoardImagesByBoard(board);
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
            boardImageRepository.save(boardImage);
        }
        return boardImages;
    }

    //-------------
    @Transactional
    public Long save(WriteBoardRequest request, List<MultipartFile> multipartFiles, String authorizationHeader) throws IOException {
        Member member = memberService.getMemberByToken(authorizationHeader);
        List<BoardImage> boardImages = getBoardImageUrl(multipartFiles);
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
        return board.getId();
    }

    //-----------
    @Transactional
    public Long modifyBoard(WriteBoardRequest request, String authorizationHeader, Long boardId) {
        Member member = memberService.getMemberByToken(authorizationHeader);
        Optional<Board> board = findById(boardId);

        isCheckBoardMember(member, board);

        board.get().modifyBoards(request.getTitle(), request.getIntroduce(), request.getTarget(),
                request.getContent(), request.getConsultTime(), request.getBoardCategory(),
                 request.getTimes(), request.getAvailableDays());

        return board.get().getId();
    }

    public Optional<Board> findById(Long boardId){
        return boardRepository.findById(boardId);
    }

    public Board findOne(Long boardId){
        return boardRepository.findOne(boardId);
    }



    //즐겨찾기
    //즐겨찾기 검증
    public boolean isFavorite(Member member, Board board){
        Optional<Favorite> favorite = favoriteRepository.findFavoriteByMemberAndBoard(member, board);
        return favorite.isPresent();
    }

    public void isCheckFavorite(Member member, Board board){
        Optional<Favorite> favorite = favoriteRepository.findFavoriteByMemberAndBoard(member, board);
        if(favorite.isPresent())
            throw new IllegalArgumentException("이미 즐겨찾기한 게시물 입니다.");
    }

    public Optional<Favorite> isCheckMyFavorite(Member member, Board board){
        Optional<Favorite> favorite = favoriteRepository.findFavoriteByMemberAndBoard(member, board);
        if(favorite.isEmpty())
            throw new IllegalArgumentException("즐겨찾기에 존재하지 않는 게시글입니다.");
        return favorite;
    }

    //즐겨찾기 추가
    @Transactional
    public void addFavoriteBoard(String authorizationHeader, Long boardId){
        Member member = memberService.getMemberByToken(authorizationHeader);
        Board board = boardRepository.findOne(boardId);

        isCheckFavorite(member, board);

        Favorite favorite = new Favorite(member, board);
        member.addFavoriteBoard(favorite);

        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavoriteBoard(String authorizationHeader, Long boardId){
        Member member = memberService.getMemberByToken(authorizationHeader);
        Board board = findOne(boardId);

        Optional<Favorite> favorite = isCheckMyFavorite(member, board);
        Favorite myFavorite = favoriteRepository.findOne(favorite.get().getId());

        member.removeFavoriteBoard(myFavorite);
        favoriteRepository.delete(myFavorite);
    }

    //Page 사용---------------------------------

    //잔체 목록
    public Page<Board> findAllByPage(Pageable pageable){
        return boardRepository.findAll(pageable);
    }

    //즐겨찾기 목록
    public Page<Board> findFavoriteBoards(String authorizationHeader, Pageable pageable){
        Member member = memberService.getMemberByToken(authorizationHeader);
        return boardRepository.findFavorite(member, pageable);
    }

    //멤버가 쓴 글목록
    public Page<Board> findMemberBoards(Long memberId, Pageable pageable){
        Member member = memberService.getMemberById(memberId);
        return boardRepository.findBoardsByMember(member, pageable);
    }

    public String isContainKeyWord(String keyWord){
        if(keyWord == null)
            return null;
        return '%' + keyWord + '%';
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
            member = memberService.getMemberByToken(authorizationHeader);
            school = member. getSchool();
        }

        String searchKeyWord = isContainKeyWord(keyWord);

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
            return boardRepository.findFavorite(member, pageable);

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
