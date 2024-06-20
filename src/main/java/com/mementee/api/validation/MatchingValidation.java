package com.mementee.api.validation;

import com.mementee.api.domain.Apply;
import com.mementee.api.domain.enumtype.ApplyState;
import com.mementee.exception.conflict.MatchingConflictException;
import org.springframework.stereotype.Component;

@Component
public class MatchingValidation {

    public static void isCheckCompleteApply(Apply apply){
        if(apply.getApplyState().equals(ApplyState.COMPLETE))
            throw new MatchingConflictException();
    }


}
