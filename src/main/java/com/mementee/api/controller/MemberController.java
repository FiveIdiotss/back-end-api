package com.mementee.api.controller;

import com.mementee.api.dto.memberDTO.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.mementee.api.domain.Major;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.School;
import com.mementee.api.service.MajorService;
import com.mementee.api.service.MemberService;
import com.mementee.api.service.SchoolService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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


    //회원가입--------------------------------------
    @Operation(description = "회원 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "등록 성공"),
            @ApiResponse(responseCode = "fail", description = "등록 실패")})
    @PostMapping("/api/member/signUp")
    public ResponseEntity<String> joinMember(@RequestBody @Valid CreateMemberRequest request) {
        try {
            memberService.join(request);
            return ResponseEntity.ok().body("회원 등록 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //목록 조회--------------------------------------
    @Operation(description = "학교 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/schools")
    public List<SchoolDTO> schoolList() {
        List<School> findSchools = schoolService.findSchools();
        List<SchoolDTO> collect = findSchools.stream()
                .map(s -> new SchoolDTO(s.getId(), s.getName()))
                .collect(Collectors.toList());

        return collect;
    }

    //학교 목록 조회 --------------------------------------
    @Operation(description = "검색으로 학교 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/schools/{keyWord}")
    public List<SchoolDTO> schoolListByKeyWord(@PathVariable String keyWord) {
        List<School> findSchools = schoolService.findSchoolsByKeyWord(keyWord);
        List<SchoolDTO> collect = findSchools.stream()
                .map(s -> new SchoolDTO(s.getId(), s.getName()))
                .collect(Collectors.toList());

        return collect;
    }

    //해당 학교에 속하는 과 --------------------------------------
    @Operation(description = "학교에 속한 전공 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/school/{schoolName}")
    public List<MajorDTO> majorList(@PathVariable String schoolName) {
        List<Major> findMajors = majorService.findMajors(schoolName);
        List<MajorDTO> collect = findMajors.stream()
                .map(m -> new MajorDTO(m.getId(), m.getName()))
                .collect(Collectors.toList());

        return collect;
    }

    //회원등록이 되어 있는 모든 회원 조회--------------------------------------
    @Operation(description = "모든 회원 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/members")
    public List<MemberDTO> memberList() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDTO> collect = findMembers.stream()
                .map(m -> new MemberDTO(m.getId(), m.getEmail(), m.getName(), m.getYear(), m.getGender(),
                        m.getSchool().getName(), m.getMajor().getName(), m.getMemberImageUrl())) //Member entity에서 꺼내와 dto에 넣음
                .collect(Collectors.toList());

        return collect;
    }

    //로그인--------------------------------------
    @Operation(description = "로그인 - (access token 기간 1시간)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "로그인 성공"),
            @ApiResponse(responseCode = "fail", description = "로그인 실패")})
    @PostMapping("/api/member/signIn")
    public ResponseEntity signIn(@Valid @RequestBody LoginMemberRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            LoginMemberResponse response = memberService.login(request);
            return ResponseEntity.ok(response);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("로그인 실패");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //로그아웃-----------------
    @Operation(description = "로그아웃")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "fail", description = "로그아웃 실패")})
    @PostMapping("/api/member/signOut")
    public ResponseEntity signOut(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            memberService.logout(authorizationHeader);
            return ResponseEntity.ok("로그아웃 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //회원 정보-----------------------------------
    //해당 멤버가 쓴 게시물 조회 추가?
    @Operation(description = "회원 정보")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "회원 조회 성공"),
            @ApiResponse(responseCode = "fail", description = "회원 조회 실패")})
    @GetMapping("/api/member/{memberId}")
    public ResponseEntity memberInfo(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long memberId) {
        try {
            //Member member = memberService.isCheckMe(authorizationHeader, memberId);
            Member member = memberService.getMemberById(memberId);
            MemberInfoResponse response = memberService.createMemberInfoResponse(member);
            return ResponseEntity.ok(response);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("조회 실패");
        }
    }

    //프로필 변경
    @Operation(description = "프로필 사진 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "프로필 변경 성공"),
            @ApiResponse(responseCode = "fail", description = "프로필 변경 실패")})
    @PostMapping(value = "/api/member/image" ,
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updatedMemberImage(@RequestHeader("Authorization") String authorizationHeader,
                                                     @RequestPart("imageFile")MultipartFile multipartFile) {
        try {
            String imageUrl = memberService.updatedMemberImage(authorizationHeader, multipartFile);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(description = "프로필 기본 사진으로 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "프로필 변경 성공"),
            @ApiResponse(responseCode = "fail", description = "프로필 변경 실패")})
    @PostMapping("/api/member/defaultImage")
    public ResponseEntity<String> updatedDefaultMemberImage(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String imageUrl = memberService.updatedDefaultMemberImage(authorizationHeader);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
