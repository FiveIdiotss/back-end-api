package com.mementee.api.dto.subBoardDTO;


import com.mementee.api.domain.enumtype.BoardCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WriteSubBoardRequest {
    private String title;
    private String content;
    private BoardCategory boardCategory;
}
