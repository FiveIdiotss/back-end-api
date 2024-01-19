package mementee.mementee.api.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import mementee.mementee.api.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class MemberRepository{

    private final EntityManager em;

    //회원가입
    public void save(Member member) {
        em.persist(member);
    }

    //회원 id로 조회 조회
    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    //회원 목록
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    //로그인시 이메일로 회원 조회
    public Member findMemberByEmail(String email){
        return em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    //이메일 중복체크를 위한 로직
    public List<Member> emailDuplicateCheck(String email){
        return em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getResultList();
    }
}
