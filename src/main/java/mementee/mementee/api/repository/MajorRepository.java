package mementee.mementee.api.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import mementee.mementee.api.domain.Major;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MajorRepository {

    private final EntityManager em;

    //과목 조회
    public Major findOne(Long id) {
        return em.find(Major.class, id);
    }

    //학교에 속하는 과목 목록
//    public List<Major> findMajors(Long schoolId) {
//        return em.createQuery("SELECT m FROM Major m WHERE m.school.id = :schoolId", Major.class)
//                .setParameter("schoolId", schoolId)
//                .getResultList();
//    }

    //학교에 속하는 과목 목록
    public List<Major> findMajors(String name) {
        return em.createQuery("SELECT m FROM Major m WHERE m.school.name = :name", Major.class)
                .setParameter("name", name)
                .getResultList();
    }
}
