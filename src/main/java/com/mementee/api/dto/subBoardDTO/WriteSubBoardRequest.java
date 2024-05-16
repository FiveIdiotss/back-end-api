package com.mementee.api.dto.subBoardDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WriteSubBoardRequest {
    private String title;
    private String content;
}
