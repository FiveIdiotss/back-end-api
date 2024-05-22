package com.mementee.api.validation;

import com.mementee.api.domain.Apply;
import com.mementee.api.domain.Board;
import com.mementee.api.domain.Member;
import com.mementee.exception.ForbiddenException;
import com.mementee.exception.conflict.ApplyConflictException;
import com.mementee.exception.conflict.MyApplyConflictException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ApplyValidation {

    //신청 중복 체크에 대한 검증
    public static void isCheckDuplicateApply(Optional<Apply> apply){
        if(apply.isPresent()){
            throw new ApplyConflictException();
        }
    }

    //자신의 글에 신청 체크할때 검증
    public static void isCheckApplyOfMyBoard(Member member, Board board){
        if(member.equals(board.getMember()))
            throw new MyApplyConflictException();
    }

    //신청 글 조회할 때 자신이 포함되어 있는지에 대한 검증
    public static void isCheckContainMyApply(Apply apply, Member member){
        if(!member.equals(apply.getSendMember()) && !member.equals(apply.getReceiveMember()))
            throw new ForbiddenException();
    }
}
