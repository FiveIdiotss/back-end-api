package mementee.mementee.service;

import lombok.RequiredArgsConstructor;
import mementee.mementee.domain.Member;
import mementee.mementee.domain.School;
import mementee.mementee.repository.MemberRepository;
import mementee.mementee.repository.SchoolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;

    //학교 id로 조회
    public School findOne(Long schoolId) {
        return schoolRepository.findOne(schoolId);
    }

    //학교 이름으로 조회
    public School findNameOne(String schoolName){
        return schoolRepository.findNameOne(schoolName);
    }


    //학교 전체 조회
    public List<School> findSchools() {
        return schoolRepository.findAll();
    }
}
