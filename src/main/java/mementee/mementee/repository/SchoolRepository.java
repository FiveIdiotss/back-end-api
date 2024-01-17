package mementee.mementee.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import mementee.mementee.domain.Member;
import mementee.mementee.domain.School;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SchoolRepository {

    private final EntityManager em;

    //학교 id로 조회
    public School findOne(Long id) {
        return em.find(School.class, id);
    }

    //학교 이름으로 조회
    public School findNameOne(String schoolName) {
        return em.createQuery("select s from School s where s.name = :schoolName", School.class)
                .setParameter("schoolName", schoolName)
                .getSingleResult();
    }

    //학교 목록
    public List<School> findAll() {
        return em.createQuery("select m from School m", School.class)
                .getResultList();
    }
}
