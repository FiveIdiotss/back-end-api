//package com.mementee.api.repository;
//
//import com.team.domain.api.mementee.Member;
//import com.team.service.api.mementee.MemberService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//class MemberRepositoryTest {
//
//    @Autowired
//    private MemberService memberService;
//    @Test
//    void findMemberByEmail() {
//        Member findMember = memberService.findMemberByEmail("1234");
//        assertEquals(findMember.getEmail(), "1234");
//    }
//
//    @Test
//    void findOne() {
//        Member findMember = memberService.getMemberById(1L);
//        assertEquals(findMember.getId(), 1L);
//    }
//}