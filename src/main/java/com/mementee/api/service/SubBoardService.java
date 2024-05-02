package com.mementee.api.service;

import com.mementee.api.domain.*;
import com.mementee.api.dto.subBoardDTO.WriteSubBoardRequest;
import com.mementee.api.repository.SubBoardRepository;
import com.mementee.api.repository.SubBoardRepositorySub;
import com.mementee.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubBoardService {

    private final SubBoardRepository subBoardRepository;
    private final SubBoardRepositorySub subBoardRepositorySub;
    private final MemberService memberService;
    private final S3Service s3Service;


    public List<SubBoardImage> getSubBoardImages(Long subBoardId){
        return subBoardRepository.findSubBoardImages(subBoardId);
    }

    @Transactional
    public List<SubBoardImage> getSubBoardImageUrl(List<MultipartFile> multipartFiles) throws IOException {
        List<SubBoardImage> subBoardImages = new ArrayList<>();
        if (multipartFiles == null) {
            return subBoardImages;
        }
        for(MultipartFile multipartFile : multipartFiles){
            String url = s3Service.saveFile(multipartFile);
            SubBoardImage boardImage = new SubBoardImage(url);
            subBoardImages.add(boardImage);
            subBoardRepository.saveSubBoardImage(boardImage);
        }
        return subBoardImages;
    }

    @Transactional
    public Long saveSubBoard(WriteSubBoardRequest request, List<MultipartFile> multipartFiles, String authorizationHeader) throws IOException {
        Member member = memberService.getMemberByToken(authorizationHeader);
        List<SubBoardImage> subBoardImages = getSubBoardImageUrl(multipartFiles);
        SubBoard subBoard;
        if(subBoardImages.isEmpty()){
            subBoard = new SubBoard(request.getTitle(), request.getContent(), member);
        }else {
            subBoard = new SubBoard(request.getTitle(), request.getContent(), member, subBoardImages);
            subBoard.addSubBoardImage(subBoardImages);

            for(SubBoardImage subBoardImage : subBoardImages){
                subBoardImage.setSubBoard(subBoard);
            }
        }

        subBoardRepository.saveSubBoard(subBoard);
        return subBoard.getId();
    }


    public SubBoard findSubBoard(Long subBoardId){
        return subBoardRepository.findSubBoard(subBoardId);
    }

    //Page 사용---------------------------------
    public Page<SubBoard> findAllByBoardTypeByPage(Pageable pageable){
        return subBoardRepositorySub.findAll(pageable);
    }

    public Page<SubBoard> findAllByBoardTypeAndSchoolNameByPage(String schoolName, Pageable pageable){
        return subBoardRepositorySub.findAllSubBoardBySchoolNameByPage(schoolName, pageable);
    }
}
