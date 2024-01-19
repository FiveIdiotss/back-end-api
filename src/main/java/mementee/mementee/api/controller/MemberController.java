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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "회원 관련 -(회원가입, 로그인, 학교 조회, 전공 조회)")
public class MemberController {

    private final PasswordEncoder passwordEncoder;

    private final MemberService memberService;
    private final SchoolService schoolService;
    private final MajorService majorService;


    //회원 등록--------------------------------------
    @Operation(description = "회원 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "등록 성공"),
            @ApiResponse(responseCode = "fail", description = "등록 실패")})
    @PostMapping("/api/member/signup")
    public ResponseEntity<String> joinMember(@RequestBody @Valid CreateMemberRequest request){
       try {
           //School school = schoolService.findOne(request.gnetSchoolId());
           School school = schoolService.findNameOne(request.getSchoolName());
           Major major = majorService.findOne(request.getMajorId());

           memberService.emailDuplicateCheck(request.getEmail());   //이메일 중복 체크

           String encodePw = passwordEncoder.encode(request.getPw()); //비밀번호 암호화

           Member member = new Member(request.getEmail(), request.getName(), encodePw, request.getYear(),
                   request.getGender(), school, major);

             memberService.join(member, school, major);

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

    @Operation(description = "검색(초성)으로 학교 목록 조회")
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
                .map(m -> new MemberDTO(m.getName())) //Member entity에서 꺼내와 dto에 넣음
                .collect(Collectors.toList());

        return collect;
    }

    //로그인--------------------------------------
    @Operation(description = "로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "로그인 성공"),
            @ApiResponse(responseCode = "fail", description = "로그인 실패")})
    @PostMapping("/api/member/signin")
    public ResponseEntity<LoginMemberResponse> signIn(@Valid @RequestBody LoginMemberRequest request){
        try {
            Member member = memberService.findMemberByEmail(request.getEmail());

                if(!passwordEncoder.matches(request.getPw(), member.getPw())){   //암호화 된 비밀번호와 일치 검사
                LoginMemberResponse response = new LoginMemberResponse(null,"null","비밀번호 틀림");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            //토큰 발행 로직
            String token = memberService.login(member.getEmail(), member.getPw());

            LoginMemberResponse response = new LoginMemberResponse(member.getId(), token, "로그인 성공");

            return ResponseEntity.ok(response);

        }catch (EmptyResultDataAccessException e){
            LoginMemberResponse response = new LoginMemberResponse(null, "null", "로그인 실패");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
