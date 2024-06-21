package com.team.mementee.api.validation;

import com.team.mementee.api.domain.Apply;
import com.team.mementee.api.domain.enumtype.ApplyState;
import com.team.mementee.exception.conflict.MatchingConflictException;
import org.springframework.stereotype.Component;

@Component
public class MatchingValidation {

    public static void isCheckCompleteApply(Apply apply) {
        if (apply.getApplyState().equals(ApplyState.COMPLETE))
            throw new MatchingConflictException();
    }
}
