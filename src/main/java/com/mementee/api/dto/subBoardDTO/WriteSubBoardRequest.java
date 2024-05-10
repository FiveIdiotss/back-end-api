package com.mementee.api.dto.subBoardDTO;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WriteSubBoardRequest {
    private String title;
    private String content;
}
