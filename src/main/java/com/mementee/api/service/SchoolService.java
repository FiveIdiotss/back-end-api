package com.mementee.api.service;

import com.mementee.api.domain.School;
import com.mementee.api.repository.member.SchoolRepository;
import com.mementee.exception.notFound.SchoolNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;

    //학교 id로 조회
    public School findSchoolById(Long schoolId) {
        Optional<School> school = schoolRepository.findById(schoolId);
        if (school.isEmpty())
            throw new SchoolNotFound();
        return school.get();
    }

    //학교 이름으로 조회
    public School findSchoolByName(String schoolName){
        Optional<School> school = schoolRepository.findSchoolByName(schoolName);
        if(school.isEmpty())
            throw new SchoolNotFound();
        return school.get();
    }

    //학교 전체 조회
    public List<School> findAll() {
        return schoolRepository.findAll();
    }
}
