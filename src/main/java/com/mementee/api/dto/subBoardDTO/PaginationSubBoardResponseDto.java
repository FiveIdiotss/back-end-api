package com.mementee.api.dto.subBoardDTO;

import com.mementee.api.dto.boardDTO.BoardDTO;
import com.mementee.api.dto.boardDTO.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class PaginationSubBoardResponseDto {
    private List<SubBoardDTO> data;
    private PageInfo pageInfo;
}
