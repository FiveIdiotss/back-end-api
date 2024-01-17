package mementee.mementee.service;

import lombok.RequiredArgsConstructor;
import mementee.mementee.domain.Major;
import mementee.mementee.domain.School;
import mementee.mementee.repository.MajorRepository;
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

    //학교에 대한 전공 전체 조회
//    public List<Major> findMajors(Long schoolId) {
//        return majorRepository.findMajors(schoolId);
//    }

    //학교에 속한 전공과목 조회
    public List<Major> findMajors(String name) {
        return majorRepository.findMajors(name);
    }
}
