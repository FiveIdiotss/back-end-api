package com.team.mementee.api.dto.subBoardDTO;


import com.team.mementee.api.domain.enumtype.BoardCategory;
import com.team.mementee.api.domain.enumtype.Platform;
import com.team.mementee.api.domain.enumtype.SubBoardType;
import lombok.Data;

@Data
public class WriteSubBoardRequest {
    private String title;
    private String content;
    private BoardCategory boardCategory;

    private SubBoardType subBoardType;
    private Platform platform;
}
