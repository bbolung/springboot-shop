package com.example.shop.repository;

import com.example.shop.constant.Role;
import com.example.shop.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //DB 저장
    @Test
    public void memberInsertTest(){
        Member member = Member.builder()
                .email("test@test.com")
                .name("tester")
                .address("서울특별시 강동구")
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .build();

        memberRepository.save(member);
    }

    @Test
    public void memberSelectTest(){
        Member member = memberRepository.findByEmail("test@test.com");
        assertNotNull(member);
    }
}