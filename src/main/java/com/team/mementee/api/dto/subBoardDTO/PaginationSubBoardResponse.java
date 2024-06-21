package com.team.mementee.api.dto.subBoardDTO;

import com.team.mementee.api.dto.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class PaginationSubBoardResponse {
    private List<SubBoardDTO> data;
    private PageInfo pageInfo;
}
