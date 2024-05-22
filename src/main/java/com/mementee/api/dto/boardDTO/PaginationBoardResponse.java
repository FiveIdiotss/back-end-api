package com.mementee.api.dto.boardDTO;

import com.mementee.api.dto.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class PaginationBoardResponse {
    private List<BoardDTO> data;
    private PageInfo pageInfo;
}
