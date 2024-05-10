package com.mementee.api.dto.boardDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class PaginationResponseDto {
    private List<BoardDTO> data;
    private PageInfo pageInfo;
}
