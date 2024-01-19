package mementee.mementee.api.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import mementee.mementee.api.domain.School;
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
    public List<School> findSchools() {
        return em.createQuery("select m from School m", School.class)
                .getResultList();
    }

    //학교 초성 검색 추가 필요
    public List<School> findSchoolsByKeyWord(String keyWord){
        //String choSung = ChoSungConfig.extractChoSung(keyWord);
        //System.out.println(choSung);

        return em.createQuery("SELECT s FROM School s WHERE s.name LIKE :keyWord", School.class)
                .setParameter("keyWord", "%" + keyWord + "%")
                //.setParameter("choSung", "%" + choSung + "%")
                .getResultList();
    }
}
