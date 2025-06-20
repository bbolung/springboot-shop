package com.example.shop.controller;

import com.example.shop.dto.MemberFormDto;
import com.example.shop.entity.Member;
import com.example.shop.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    
    //로그인 페이지 연결
    @GetMapping(value = "/login")
    public String loginMember(){
        return "member/memberLoginForm";
    }

    //로그인 실패
    @GetMapping(value = "/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
        return "member/memberLoginForm";
    }

    //회원가입 폼 제공
    @GetMapping(value = "/new")
    public String MemberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());

        return "member/memberForm";
    }
    
    //전달받은 회원가입 폼 데이터 -> DB 저장(entity로 변환 후 저장하는데, 변환은 Service에서 처리해도 됨)
    @PostMapping(value = "/new")
    public String MemberForm(@Valid MemberFormDto memberFormDto,
                             BindingResult bindingResult, Model model) {

        if(bindingResult.hasErrors()) {     //Error존재 = MemberFormDto의 요구사항대로 입력X 경우
            return "member/memberForm";
        }

        try{
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.save(member);
        }catch (IllegalArgumentException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }
        return "redirect:/";
    }
}
