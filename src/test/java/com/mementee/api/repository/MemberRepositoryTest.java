//package com.mementee.api.repository;
//
//import com.mementee.api.domain.Member;
//import com.mementee.api.service.MemberService;
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
//
//    @Test
//    void findMemberByEmail() {
//        Member email = memberService.findMemberByEmail("이메일");
//        System.out.println(email.getName());
//    }
//
//    @Test
//    void findOne() {
//        Member memberById = memberService.getMemberById(52L);
//        System.out.println(memberById.getName());
//    }
//}