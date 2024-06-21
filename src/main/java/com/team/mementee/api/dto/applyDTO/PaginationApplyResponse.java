package com.team.mementee.api.dto.applyDTO;

import com.team.mementee.api.dto.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class PaginationApplyResponse {
    private List<ApplyDTO> data;
    private PageInfo pageInfo;
}
