package com.mementee.api.validation;

import com.mementee.api.domain.Favorite;
import com.mementee.exception.conflict.FavoriteConflictException;
import com.mementee.exception.notFound.FavoriteNotFound;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BoardValidation {

    //게시물 목록 조회 시, 즐겨찾기 되어있는지 여부에 대한 정보를 뿌리는 메소드
    public static boolean isFavorite(Optional<Favorite> favorite){
        return favorite.isPresent();
    }

    //즐겨찾기 추가 시 이미 즐겨찾기 되어 있는지 검증
    public static void isCheckAddFavorite(Optional<Favorite> favorite){
        if(favorite.isPresent())
            throw new FavoriteConflictException();
    }

    //즐겨찾기 삭제 시 로그인한 유저에 즐겨찾기가 되어 있는지 검증
    public static Favorite isCheckRemoveFavorite(Optional<Favorite> favorite){
        if(favorite.isEmpty())
            throw new FavoriteNotFound();
        return favorite.get();
    }

    //필터 검색 키워드 유무 검증
    public static String isContainKeyWord(String keyWord){
        if(keyWord == null)
            return null;
        return '%' + keyWord + '%';
    }
}
