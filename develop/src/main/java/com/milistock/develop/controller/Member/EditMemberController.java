package com.milistock.develop.controller.Member;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.milistock.develop.code.ErrorCode;
import com.milistock.develop.domain.Member;
import com.milistock.develop.dto.EditMemberInfoDto;
import com.milistock.develop.dto.EditMemberPasswordDto;
import com.milistock.develop.dto.EditMemberPwChangeDto;
import com.milistock.develop.dto.HttpOkResponseDto;
import com.milistock.develop.dto.MemberSignupResponseDto;
import com.milistock.develop.exception.BusinessExceptionHandler;
import com.milistock.develop.security.jwt.util.IfLogin;
import com.milistock.develop.security.jwt.util.LoginUserDto;
import com.milistock.develop.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/members/edit")
public class EditMemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/pwCheck")
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

    @GetMapping("/getInfo")
    public ResponseEntity<?> getInfo(@IfLogin LoginUserDto loginUserDto) {
        Long tokenMemberId = loginUserDto.getMemberId();
        Member member = memberService.findByMemberId(tokenMemberId);

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

    @PostMapping("/infoChange") // 개인정보 변경 메소드
    public ResponseEntity<?> pwChange(@IfLogin LoginUserDto loginUserDto,
            @RequestBody @Valid EditMemberInfoDto infoChangeDto) {

        Long tokenMemberId = loginUserDto.getMemberId();
        String clientServiceNumber = infoChangeDto.getServiceNumber();
        Member tokenMember = memberService.findByMemberId(tokenMemberId);
        Member clientMember = memberService.findByServiceNumber(clientServiceNumber);

        if (clientMember == null) {
            throw new BusinessExceptionHandler("존재하지 않는 아이디 입니다.", ErrorCode.ID_ERROR);
        }

        if (!tokenMember.getUserId().equals(clientMember.getUserId())) { // ID 대소문자 비교
            throw new BusinessExceptionHandler("존재하지 않는 아이디 입니다.", ErrorCode.ID_ERROR); // front 오류
        } else {
            memberService.updateMember(tokenMemberId, infoChangeDto.getAffiliation(), infoChangeDto.getBirth(),
                    infoChangeDto.getDischarge(), infoChangeDto.getEmail(), infoChangeDto.getMilitaryRank(),
                    infoChangeDto.getPhoneNumber(), infoChangeDto.getGender());
            HttpOkResponseDto infoChangeResponseDto = HttpOkResponseDto.builder()
                    .status(200)
                    .build();
            return new ResponseEntity<>(infoChangeResponseDto, HttpStatus.OK);
        }
    }

    @PostMapping("/pwChange") // 비밀번호 변경 메소드
    public ResponseEntity<?> pwChange(@IfLogin LoginUserDto loginUserDto,
            @RequestBody @Valid EditMemberPwChangeDto pwChangeDto) {

        Long memberId = loginUserDto.getMemberId();
        String newPassword = pwChangeDto.getNewPassword();
        String currentPassword = pwChangeDto.getcurrentPassword();

        Member member = memberService.findByMemberId(memberId); // memberId로 회원 정보 조회

        if (currentPassword.isEmpty() || member.getPassword().isBlank()) {
            throw new BusinessExceptionHandler("비밀번호를 입력하세요.", ErrorCode.UNAUTHORIZED);
        }

        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new BusinessExceptionHandler("비밀번호가 틀렸습니다.", ErrorCode.UNAUTHORIZED);
        }

        memberService.updateMemberPw(member.getUserId(), newPassword);
        HttpOkResponseDto pwChangeResponseDto = HttpOkResponseDto.builder()
                .status(200)
                .build();
        return new ResponseEntity<>(pwChangeResponseDto, HttpStatus.OK);

    }

}
