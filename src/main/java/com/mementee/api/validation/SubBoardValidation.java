package com.mementee.api.validation;

import com.mementee.api.domain.Favorite;
import com.mementee.api.domain.SubBoardLike;
import com.mementee.exception.conflict.FavoriteConflictException;
import com.mementee.exception.conflict.SubBoardLikeConflictException;
import com.mementee.exception.notFound.FavoriteNotFound;
import com.mementee.exception.notFound.SubBoardLikeNotFound;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SubBoardValidation {

    //자유 게시물 목록 조회 시, 좋아요 되어있는지 여부에 대한 정보를 뿌리는 메소드
    public static boolean isSubBoardLike(Optional<SubBoardLike> subBoardLike){
        return subBoardLike.isPresent();
    }

    //좋아요 누를 시 이미 좋아요 되어 있는지 검증
    public static void isCheckAddSubBordLike(Optional<SubBoardLike> subBoardLike){
        if(subBoardLike.isPresent())
            throw new SubBoardLikeConflictException();
    }

    //좋아요 취소 시 로그인한 유저에 즐겨찾기가 되어 있는지 검증
    public static SubBoardLike isCheckRemoveSubBoardLike(Optional<SubBoardLike> subBoardLike){
        if(subBoardLike.isEmpty())
            throw new SubBoardLikeNotFound();
        return subBoardLike.get();
    }
}
