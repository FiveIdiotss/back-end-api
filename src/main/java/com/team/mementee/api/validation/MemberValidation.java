package com.team.mementee.api.validation;

import com.team.mementee.api.domain.Member;
import com.team.mementee.exception.ForbiddenException;
import com.team.mementee.exception.conflict.EmailConflictException;
import com.team.mementee.exception.conflict.ProfileConflictException;
import com.team.mementee.exception.unauthorized.LoginFailedException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MemberValidation {

    //현재 로그인한 유저와 조회 하려는 정보 작성자에 대한 일치 여부
    public static Member isCheckMe(Member loginMember, Member checkMember) {
        if (!checkMember.equals(loginMember))
            throw new ForbiddenException();

        return checkMember;
    }

    //로그인 시 비밀번호 맞는지 체크
    public static void isMatchPassWord(boolean isCheck) {
        if (!isCheck)
            throw new LoginFailedException();
    }


    //회원가입시 중복 이메일 검증
    public static void isDuplicateCheck(Optional<Member> member) {
        if (member.isPresent()) {
            throw new EmailConflictException();
        }
    }

    //기본 이미지로 변경 시 이미 기본 이미지인지 검증
    public static void isCheckDefaultImage(Member member, String imageUrl){
        if(member.getMemberImageUrl().equals(imageUrl))
            throw new ProfileConflictException();
    }

}
