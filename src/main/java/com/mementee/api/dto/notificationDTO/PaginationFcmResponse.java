package com.mementee.api.dto.notificationDTO;

import com.mementee.api.dto.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class PaginationFcmResponse {
    private List<FcmDTO> data;
    private PageInfo pageInfo;
}
