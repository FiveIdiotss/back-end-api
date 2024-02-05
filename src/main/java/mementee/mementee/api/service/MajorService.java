package mementee.mementee.api.service;

import lombok.RequiredArgsConstructor;
import mementee.mementee.api.domain.Major;
import mementee.mementee.api.repository.MajorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MajorService {

    private final MajorRepository majorRepository;

    public Major findOne(Long majorId) {
        return majorRepository.findOne(majorId);
    }

    //학교에 속한 전공 과목 조회 -
    public List<Major> findMajors(String name) {
        return majorRepository.findMajors(name);
    }

    //학교에 속한 전공 과목 조회
//    public List<Major> findMajors(Long schoolId) {
//        return majorRepository.findMajors(schoolId);
//    }
}
