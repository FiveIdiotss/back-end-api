package mementee.mementee.api.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import mementee.mementee.api.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ApplyRepository {

    private final EntityManager em;

    public void saveApplication(Apply apply){
        em.persist(apply);
    }

    public Apply findApplication(Long applicationId){
        return em.find(Apply.class, applicationId);
    }


    //중복 신청이 있는지
    public Optional<Apply> isDuplicateApply(Long sendMemberId, Long receiveMemberId, Long boardId){
        try {
            Apply apply = em.createQuery("select a from Apply a where a.sendMember.id = :sendMemberId and a.receiveMember.id =: receiveMemberId and a.board.id =: boardId", Apply.class)
                    .setParameter("sendMemberId", sendMemberId)
                    .setParameter("receiveMemberId", receiveMemberId)
                    .setParameter("boardId",boardId)
                    .getSingleResult();
            return Optional.ofNullable(apply);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    //아직 처리하지 않은 나의 신청한 목록
//    public List<Apply> findApplicationBySendMember(Long memberId){
//        return em.createQuery("select a from Apply a where a.sendMember.id = :memberId and a.applyState = 'HOLDING'", Apply.class)
//                .setParameter("memberId", memberId)
//                .getResultList();
//    }
//
//    //아직 처리하지 않은 나의 신청 받은 목록
//    public List<Apply> findApplicationByReceiveMember(Long memberId){
//        return em.createQuery("select a from Apply a where a.receiveMember.id = :memberId and a.applyState = 'HOLDING' ", Apply.class)
//                .setParameter("memberId", memberId)
//                .getResultList();
//    }

    public List<Apply> findApplicationBySendMember(Long memberId){
        return em.createQuery("select a from Apply a where a.sendMember.id = :memberId", Apply.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    //아직 처리하지 않은 나의 신청 받은 목록
    public List<Apply> findApplicationByReceiveMember(Long memberId){
        return em.createQuery("select a from Apply a where a.receiveMember.id = :memberId", Apply.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }


    public void removeSendApply(Long sendMemberId){
        em.createQuery("delete from Apply a where a.sendMember.id =: sendMemberId" , Apply.class)
                .setParameter("sendMemberId", sendMemberId)
                .executeUpdate();
    }

    public void removeReceiveApply(Long receiveMemberId){
        em.createQuery("delete from Apply a where a.receiveMember.id =: receiveMemberId" , Apply.class)
                .setParameter("receiveMemberId", receiveMemberId)
                .executeUpdate();
    }
}
