package com.team.mementee.api.domain.enumtype;

import jakarta.persistence.Lob;
import lombok.Getter;

@Getter
public enum ApplyState {

    HOLDING("대기중"),
    COMPLETE("신청 수락"),
    REJECT("신청 거절");

    @Lob
    String content;

    ApplyState(String content) {
        this.content = content;
    }

    public void reasonOfReject(String content){
        this.content = content;
    }

}
