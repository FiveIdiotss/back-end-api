package com.mementee.api.dto.subBoardDTO;

import com.mementee.api.dto.boardDTO.BoardDTO;
import com.mementee.api.dto.boardDTO.BoardImageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class SubBoardInfoResponse {
    private SubBoardDTO subBoardDTO;
    private List<SubBoardImageDTO> subBoardImageUrls;       //게시물에 등록된 이미지
}
