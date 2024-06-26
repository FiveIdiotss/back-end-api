package com.team.mementee.api.dto.subBoardDTO;

import com.team.mementee.api.dto.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class PaginationReplyResponse {
    private List<ReplyDTO> data;
    private PageInfo pageInfo;
}
