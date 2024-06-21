package com.team.mementee.api.dto.notificationDTO;

import com.team.mementee.api.dto.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class PaginationFcmResponse {
    private List<NotificationDTO> data;
    private PageInfo pageInfo;
}
