package com.mementee.api.repository;

import com.mementee.api.domain.Matching;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.enumtype.BoardType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MatchingRepository {
    private final EntityManager em;

    //매칭 저장
    public void saveMatch(Matching matching){
        em.persist(matching);
    }

    //매칭 조회
    public Matching findMatching(Long id) {
        return em.find(Matching.class, id);
    }

    //내가 멘토/멘티 매칭 목록
    public List<Matching> findMyMentor(Long memberId){
        return em.createQuery("select m from Matching m where m.mentee.id =: memberId ", Matching.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public List<Matching> findMyMentee(Long memberId){
        return em.createQuery("select m from Matching m where m.mentor.id =: memberId ", Matching.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }
}
