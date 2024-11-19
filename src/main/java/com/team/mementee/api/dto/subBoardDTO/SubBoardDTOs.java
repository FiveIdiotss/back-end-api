package com.team.mementee.api.dto.subBoardDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class SubBoardDTOs {

    private List<SubBoardDTO> subBoardDTOS = new ArrayList<>();

    public SubBoardDTOs(List<SubBoardDTO> subBoardDTOS) {
        this.subBoardDTOS = subBoardDTOS;
    }
}
