package com.mementee.api.dto.applyDTO;

import com.mementee.api.dto.boardDTO.BoardDTO;
import com.mementee.api.dto.boardDTO.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class PaginationSendApplyResponseDto {
    private List<SendApplyDTO> data;
    private PageInfo pageInfo;
}
