package com.mementee.api.repository;

import com.mementee.api.domain.SubBoard;
import com.mementee.api.domain.SubBoardImage;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SubBoardRepository {

    private final EntityManager em;

    //글 등록
    public void saveSubBoard(SubBoard subBoard) {
        em.persist(subBoard);
    }

    //게시글 조회
    public SubBoard findSubBoard(Long subBoardId) {
        return em.find(SubBoard.class, subBoardId);
    }

    //-----------
    public void saveSubBoardImage(SubBoardImage subBoardImage){
        em.persist(subBoardImage);
    }

    public List<SubBoardImage> findSubBoardImages(Long subBoardId){
        return em.createQuery("select s from SubBoardImage s where s.subBoard.id =: subBoardId", SubBoardImage.class)
                .setParameter("subBoardId", subBoardId)
                .getResultList();
    }
}
