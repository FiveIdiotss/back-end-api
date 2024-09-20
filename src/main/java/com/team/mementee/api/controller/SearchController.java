package com.team.mementee.api.controller;

import com.team.mementee.api.domain.Board;
import com.team.mementee.api.domain.SubBoard;
import com.team.mementee.api.dto.CommonApiResponse;
import com.team.mementee.api.dto.searchDTO.SearchDTO;
import com.team.mementee.api.service.BoardService;
import com.team.mementee.api.service.SubBoardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "제목 검색 필터")
public class SearchController {

    private final BoardService boardService;
    private final SubBoardService subBoardService;

    // 제목, 내용, 학과
    @GetMapping("/api/search")
    public CommonApiResponse<?> getSearchFilters(@RequestParam String query) {
        List<Board> boards_title = boardService.findAllByTitleContaining(query);
        List<Board> boards_content = boardService.findAllByContentContaining(query);
        List<SubBoard> subBoards_title = subBoardService.findAllByTitleContaining(query);
        List<SubBoard> subBoards_content = subBoardService.findAllByContentContaining(query);
        return CommonApiResponse.createSuccess(SearchDTO.toEntity(boards_title, subBoards_title, boards_content, subBoards_content));
    }

}
