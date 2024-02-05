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
public class ApplicationRepository {

    private final EntityManager em;

    public void saveApplication(Application application){
        em.persist(application);
    }

    public Application findApplication(Long applicationId){
        return em.find(Application.class, applicationId);
    }


    //중복 신청이 있는지
    public Optional<Application> isDuplicateApply(Long sendMemberId, Long receiveMemberId, Long boardId){
        try {
            Application application = em.createQuery("select a from Application a where a.sendMember.id = :sendMemberId and a.receiveMember.id =: receiveMemberId and a.board.id =: boardId", Application.class)
                    .setParameter("sendMemberId", sendMemberId)
                    .setParameter("receiveMemberId", receiveMemberId)
                    .setParameter("boardId",boardId)
                    .getSingleResult();
            return Optional.ofNullable(application);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    //내가 신청한 목록
    public List<Application> findApplicationBySendMember(Long memberId){
        return em.createQuery("select a from Application a where a.sendMember.id = :memberId", Application.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    //내가 신청 받은 목록
    public List<Application> findApplicationByReceiveMember(Long memberId){
        return em.createQuery("select a from Application a where a.receiveMember.id = :memberId", Application.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }


    public void removeSendApply(Long sendMemberId){
        em.createQuery("delete from Application a where a.sendMember.id =: sendMemberId" , Application.class)
                .setParameter("sendMemberId", sendMemberId)
                .executeUpdate();
    }

    public void removeReceiveApply(Long receiveMemberId){
        em.createQuery("delete from Application a where a.receiveMember.id =: receiveMemberId" , Application.class)
                .setParameter("receiveMemberId", receiveMemberId)
                .executeUpdate();
    }
}
