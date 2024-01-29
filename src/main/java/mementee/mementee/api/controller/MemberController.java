package mementee.mementee.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mementee.mementee.api.controller.memberDTO.*;
import mementee.mementee.api.domain.Major;
import mementee.mementee.api.domain.Member;
import mementee.mementee.api.domain.School;
import mementee.mementee.api.service.MajorService;
import mementee.mementee.api.service.MemberService;
import mementee.mementee.api.service.SchoolService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "회원 관련 -(회원 가입, 회원 조회, 로그인, 학교 조회, 전공 조회)")
public class MemberController {

    private final MemberService memberService;
    private final SchoolService schoolService;
    private final MajorService majorService;


    //회원 등록--------------------------------------
//    @Operation(description = "사용 가능한 이메일 인지 체크")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "success", description = "등록 성공"),
//            @ApiResponse(responseCode = "fail", description = "등록 실패")})
//    @PostMapping("/api/member/check")
//    public ResponseEntity<String> checkMember(@RequestBody @Valid CheckMemberRequest request){
//        try {
//            memberService.emailDuplicateCheck(request.getEmail());   //이메일 중복 체크
//            return ResponseEntity.ok().body("사용 가능한 이메일");
//        }catch (Exception e){
//            return ResponseEntity.ok().body("이미 사용중인 이메일");
//        }
//    }

    @Operation(description = "회원 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "등록 성공"),
            @ApiResponse(responseCode = "fail", description = "등록 실패")})
    @PostMapping("/api/member/signUp")
    public ResponseEntity<String> joinMember(@RequestBody @Valid CreateMemberRequest request){
       try {
           memberService.join(request);
           return ResponseEntity.ok().body("회원 등록 성공");
       }catch (Exception e){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원 등록 실패");
       }
    }

    //목록 조회--------------------------------------
    @Operation(description = "학교 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/schools")
    public List<SchoolDTO> schoolList(){
        List<School> findSchools = schoolService.findSchools();
        List<SchoolDTO> collect = findSchools.stream()
                .map(s -> new SchoolDTO(s.getId(), s.getName()))
                .collect(Collectors.toList());

        return collect;
    }

    @Operation(description = "검색으로 학교 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/schools/{keyWord}")
    public List<SchoolDTO> schoolListByKeyWord(@PathVariable String keyWord){
        List<School> findSchools = schoolService.findSchoolsByKeyWord(keyWord);
        List<SchoolDTO> collect = findSchools.stream()
                .map(s -> new SchoolDTO(s.getId(), s.getName()))
                .collect(Collectors.toList());

        return collect;
    }


//    @Operation(description = "학교에 속한 전공 목록")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "success", description = "성공"),
//            @ApiResponse(responseCode = "fail")})
//    @GetMapping("/api/{schoolId}")
//    public List<MajorDto> majorList(@PathVariable Long schoolId){
//        List<Major> findMajors = majorService.findMajors(schoolId);
//        List<MajorDto> collect = findMajors.stream()
//                .map(m -> new MajorDto(m.getId(), m.getName()))
//                .collect(Collectors.toList());
//
//        return collect;
//    }

    @Operation(description = "학교에 속한 전공 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/school/{schoolName}")
    public List<MajorDTO> majorList(@PathVariable String schoolName){
        List<Major> findMajors = majorService.findMajors(schoolName);
        List<MajorDTO> collect = findMajors.stream()
                .map(m -> new MajorDTO(m.getId(), m.getName()))
                .collect(Collectors.toList());

        return collect;
    }

    @Operation(description = "모든 회원 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/members")
    public List<MemberDTO> memberList(){
        List<Member> findMembers = memberService.findMembers();
        List<MemberDTO> collect = findMembers.stream()
                .map(m -> new MemberDTO(m.getId(), m.getEmail(), m.getName(), m.getYear(), m.getGender(), m.getSchool().getName(), m.getMajor().getName())) //Member entity에서 꺼내와 dto에 넣음
                .collect(Collectors.toList());

        return collect;
    }

    //로그인--------------------------------------
    @Operation(description = "로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "로그인 성공"),
            @ApiResponse(responseCode = "fail", description = "로그인 실패")})
    @PostMapping("/api/member/signIn")
    public ResponseEntity<LoginMemberResponse> signIn(@Valid @RequestBody LoginMemberRequest request, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            LoginMemberResponse response = memberService.login(request);
            return ResponseEntity.ok(response);
        }catch (EmptyResultDataAccessException e){
            LoginMemberResponse response = new LoginMemberResponse(null,null, "로그인 실패");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }catch (Exception e){
            LoginMemberResponse response = new LoginMemberResponse(null,null, "로그인 실패");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    //회원 정보-----------------------------------
    //해당 멤버가 쓴 게시물 조회 추가?
    @Operation(description = "회원 정보")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "회원 조회 성공"),
            @ApiResponse(responseCode = "fail", description = "회원 조회 실패")})
    @GetMapping("/api/member/{memberId}")
    public ResponseEntity<MemberInfoResponse> memberInfo(@PathVariable Long memberId){
        try {
            Member member = memberService.findOne(memberId);
            MemberInfoResponse response = new MemberInfoResponse(member.getId(), member.getEmail(), member.getName(),
                    member.getYear(), member.getGender(), member.getSchool().getName(), member.getMajor().getName());
            return ResponseEntity.ok(response);

        }catch (EmptyResultDataAccessException e){
            MemberInfoResponse response = new MemberInfoResponse(null , null, null, 0,
                    null , null, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        }catch (Exception e){
        MemberInfoResponse response = new MemberInfoResponse(null , null, null, 0,
                null , null, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    //친구 요청 기능------------
    @Operation(description = "친구 요청 (미구현)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "회원 조회 성공"),
            @ApiResponse(responseCode = "fail", description = "회원 조회 실패")})
    @PostMapping("/api/member/{memberId}")
    public ResponseEntity<String> requestFriend(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long memberId){
        try {
            Member member = memberService.getMemberByToken(authorizationHeader);
            Member addMember = memberService.findOne(memberId);


            return ResponseEntity.ok().body("친구 요청 성공");
        }catch (EmptyResultDataAccessException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("친구 요청 실패");

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("친구 요청 실패");
        }
    }



}
