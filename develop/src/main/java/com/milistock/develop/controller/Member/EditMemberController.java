package com.milistock.develop.controller.Member;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.milistock.develop.code.ErrorCode;
import com.milistock.develop.domain.Member;
import com.milistock.develop.dto.EditMemberPasswordDto;
import com.milistock.develop.dto.MemberSignupResponseDto;
import com.milistock.develop.exception.BusinessExceptionHandler;
import com.milistock.develop.security.jwt.util.IfLogin;
import com.milistock.develop.security.jwt.util.LoginUserDto;
import com.milistock.develop.service.MemberService;
import org.springframework.http.HttpStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/members/edit")
public class EditMemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/pwCheck")
    public ResponseEntity<?> pwCheck(@IfLogin LoginUserDto loginUserDto,
            @RequestBody @Valid EditMemberPasswordDto password) {
        Long tokenMemberId = loginUserDto.getMemberId();
        Member member = memberService.findByMemberId(tokenMemberId);

        if (password.getPassword().isEmpty() || member.getPassword().isBlank()) {
            throw new BusinessExceptionHandler("비밀번호를 입력하세요.", ErrorCode.UNAUTHORIZED);
        }

        if (!passwordEncoder.matches(password.getPassword(), member.getPassword())) {
            throw new BusinessExceptionHandler("비밀번호가 틀렸습니다.", ErrorCode.UNAUTHORIZED);
        }

        MemberSignupResponseDto memberSignupResponse = new MemberSignupResponseDto();
        memberSignupResponse.setStatus(200);
        memberSignupResponse.setMemberId(member.getMemberId());
        memberSignupResponse.setServiceNumber(member.getServiceNumber());
        memberSignupResponse.setName(member.getName());
        memberSignupResponse.setUserId(member.getUserId());
        memberSignupResponse.setJob(member.getJob());
        memberSignupResponse.setAffiliation(member.getAffiliation());
        memberSignupResponse.setMilitaryRank(member.getMilitaryRank());
        memberSignupResponse.setBirth(member.getBirth());
        memberSignupResponse.setPhoneNumber(member.getPhoneNumber());
        memberSignupResponse.setEmail(member.getEmail());
        memberSignupResponse.setGender(member.getGender());
        memberSignupResponse.setAppointment(member.getAppointment());
        memberSignupResponse.setDischarge(member.getDischarge());


        return new ResponseEntity<>(memberSignupResponse, HttpStatus.OK);
    }

}
