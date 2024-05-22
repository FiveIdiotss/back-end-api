package com.mementee.api.dto.subBoardDTO;

import com.mementee.api.domain.Board;
import com.mementee.api.domain.SubBoard;
import com.mementee.api.dto.boardDTO.BoardDTO;
import com.mementee.api.dto.boardDTO.BoardImageDTO;
import com.mementee.api.dto.boardDTO.BoardInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class SubBoardInfoResponse {
    private SubBoardDTO subBoardDTO;
    private List<SubBoardImageDTO> subBoardImageUrls;       //게시물에 등록된 이미지

    public static SubBoardInfoResponse createSubBoardInfoResponse(SubBoardDTO subBoardDTO, List<SubBoardImageDTO> subBoardImageDTOs) {
        return new SubBoardInfoResponse(subBoardDTO, subBoardImageDTOs);
    }
}
