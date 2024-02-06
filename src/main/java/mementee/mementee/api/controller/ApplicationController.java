package mementee.mementee.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mementee.mementee.api.controller.applicationDTO.ApplicationDTO;
import mementee.mementee.api.controller.applicationDTO.ApplicationInfo;
import mementee.mementee.api.domain.Application;
import mementee.mementee.api.service.ApplicationService;
import mementee.mementee.api.service.BoardService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "멘토/멘티  신청 리스트 조회, 수락/거절 기능")
@Slf4j
public class ApplicationController {

    private final ApplicationService applicationService;

    //신청 한 글인지 아닌지 체크하는 api 필요


    //내가 신청 한 리스트
//    @Operation(description = "내가 신청한 글 리스트")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "success", description = "성공"),
//            @ApiResponse(responseCode = "fail")})
//    @GetMapping("/api/sendApply/{memberId}")
//    public List<ApplicationDTO> mySendApplyList(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long memberId){
//        List<Application> list = applicationService.findApplicationBySendMember(memberId);
//        List<ApplicationDTO> collect = list.stream()
//                .map(a -> new ApplicationDTO(a.getId(), new BoardDTO(a.getBoard().getId(), a.getBoard().getTitle(), a.getBoard().getContent(),
//                        a.getBoard().getMember().getYear(), a.getBoard().getMember().getSchool().getName(), a.getBoard().getMember().getMajor().getName(),
//                        a.getBoard().getMember().getId(), a.getBoard().getMember().getName())) )
//                .toList();
//        return collect;
//    }

    @Operation(description = "내가 신청한 글 리스트  - 여기서 신청한 글로 바로 이동시켜도 됨")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/sendApply/{memberId}")
    public List<ApplicationDTO> mySendApplyList(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long memberId){
        List<Application> list = applicationService.findApplicationBySendMember(memberId);
        List<ApplicationDTO> collect = list.stream()
                .map(a -> new ApplicationDTO(a.getId(), a.getBoard().getId(), a.getContent()))
                .toList();
        return collect;
    }


    //내가 신청 받은 리스트
//    @Operation(description = "내가 신청 받은 글 리스트")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "success", description = "성공"),
//            @ApiResponse(responseCode = "fail")})
//    @GetMapping("/api/receiveApply/{memberId}")
//    public List<ApplicationDTO> myReceiveApplyList(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long memberId){
//        List<Application> list = applicationService.findApplicationByReceiveMember(memberId);
//        List<ApplicationDTO> collect = list.stream()
//                .map(a -> new ApplicationDTO(a.getId(), new BoardDTO(a.getBoard().getId(), a.getBoard().getTitle(), a.getBoard().getContent(),
//                        a.getBoard().getMember().getYear(), a.getBoard().getMember().getSchool().getName(), a.getBoard().getMember().getMajor().getName(),
//                        a.getBoard().getMember().getId(), a.getBoard().getMember().getName())) )
//                .toList();
//        return collect;
//    }

    @Operation(description = "내가 신청 받은 글 리스트 - 여기서 신청 받은 글로 바로 이동시켜도 됨")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/receiveApply/{memberId}")
    public List<ApplicationDTO> myReceiveApplyList(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long memberId){
        List<Application> list = applicationService.findApplicationByReceiveMember(memberId);
        List<ApplicationDTO> collect = list.stream()
                .map(a -> new ApplicationDTO(a.getId(), a.getBoard().getId(), a.getContent()))
                .toList();
        return collect;
    }

    //신청 받기
//    @Operation(description = "멘토/멘티 신청 수락 (미 구현)")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "success", description = "신청 성공"),
//            @ApiResponse(responseCode = "fail", description = "신청 실패")})
//    @PostMapping("/api/apply/{applyId}")
//    public ResponseEntity<String> boardApply(@PathVariable Long applyId, @RequestHeader("Authorization") String authorizationHeader){
//        try {
//            Application application = applicationService.findApplication(applyId);
//            Board board = boardService.findBoard(application.getId());                              //신청 받을 게시글
//            Member member = memberService.getMemberByToken(authorizationHeader);                    //신청 할 사람
//
//            applicationService.sendApply(member, board);
//
//            return ResponseEntity.ok("신청 수락 성공");
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("신청 수락 실패");
//        }
//    }



    //신청한 글 조회
    @Operation(description = "신청 글 조회 - (여기서 신청한 글로 이동할 수 있게 해주세요)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "지원 글 조회 성공"),
            @ApiResponse(responseCode = "fail", description = "지원 글 조회 실패")})
    @GetMapping("/api/apply/{applyId}")
    public ResponseEntity<?> applicationInfo(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long applyId){
        try {
           Application application = applicationService.isCheckMyApplication(authorizationHeader, applyId);

            ApplicationInfo response = new ApplicationInfo(new ApplicationDTO(application.getId(), application.getBoard().getId(),
                    application.getContent()));
            return ResponseEntity.ok(response);

        }catch (EmptyResultDataAccessException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("지원 글 조회 실패");

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("지원 글 조회 실패");
        }
    }
}
