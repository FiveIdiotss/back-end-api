package com.mementee.api.service;

import com.mementee.api.domain.Major;
import com.mementee.api.repository.member.MajorRepository;
import com.mementee.exception.notFound.MajorNotFound;
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

    public Major findMajorById(Long majorId) {
        Optional<Major> major = majorRepository.findById(majorId);
        if(major.isEmpty())
            throw new MajorNotFound();
        return major.get();
    }

    //학교에 속한 전공 과목 조회 ------
    public List<Major> findAllByName(String name) {
        return majorRepository.findAllByName(name);
    }
}
