package com.team.mementee.api.service;

import com.team.mementee.api.domain.Major;
import com.team.mementee.api.domain.School;
import com.team.mementee.api.repository.member.MajorRepository;
import com.team.mementee.exception.notFound.MajorNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MajorService {

    private final MajorRepository majorRepository;
    private final SchoolService schoolService;

    public Major findMajorById(Long majorId) {
        Optional<Major> major = majorRepository.findById(majorId);
        if(major.isEmpty())
            throw new MajorNotFound();
        return major.get();
    }

    //학교에 속한 전공 과목 조회 ------
    public List<Major> findAllBySchoolName(String name) {
        School school = schoolService.findSchoolByName(name);
        return majorRepository.findAllBySchool(school);
    }
}
