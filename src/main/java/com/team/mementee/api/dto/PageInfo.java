package com.team.mementee.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PageInfo {
    private int page;
    private int size;
    private int totalElements;
    private int totalPages;
}
