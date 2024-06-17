package com.mementee.api.dto.subBoardDTO;


import com.mementee.api.domain.enumtype.BoardCategory;
import com.mementee.api.domain.enumtype.SubBoardType;
import lombok.Data;

@Data
public class WriteSubBoardRequest {
    private String title;
    private String content;
    private BoardCategory boardCategory;

    private SubBoardType subBoardType;
}
