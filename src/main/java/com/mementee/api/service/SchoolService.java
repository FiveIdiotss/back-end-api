package com.mementee.api.service;

import com.mementee.api.domain.School;
import com.mementee.api.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
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
        return schoolRepository.findSchools();
    }

    //학교 검색 조회
    public List<School> findSchoolsByKeyWord(String keyWord) {
        return schoolRepository.findSchoolsByKeyWord(keyWord);
    }
}
