package com.milistock.develop.controller.Member;



import com.milistock.develop.domain.IdentityVerification;
import com.milistock.develop.service.MemberService;
import com.milistock.develop.dto.IdCheckDto;

import lombok.RequiredArgsConstructor;

import com.milistock.develop.service.IdentityVerificationService;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

import com.milistock.develop.dto.IdInquiryDto;
import com.milistock.develop.dto.PwInquiryDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.milistock.develop.domain.Member;
import com.milistock.develop.dto.PwChangeDto;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/members/help")
public class MemberHelpController {

    private final IdentityVerificationService identityVerificationService;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    /*@PostMapping("/pwChange")
    public ResponseEntity pwChange(@RequestBody @Valid PwChangeDto pwChangeDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        String userId = pwChangeDto.getUserId();
        String newPassword = pwChangeDto.getNewPassword();

        // userId로 회원 정보 조회
        Member member = memberService.findByUserId(userId);

        if (member != null) {
            // 회원이 존재하면 새로운 비밀번호로 업데이트
            member.setPassword(passwordEncoder.encode(newPassword));
            memberService.updateMember(member);

            return new ResponseEntity("비밀번호가 성공적으로 변경되었습니다.", HttpStatus.OK);
        } else {
            // 회원이 존재하지 않으면 오류 메시지 반환
            return new ResponseEntity("회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
    }*/
    
    @PostMapping("/pwInquiry")
    public ResponseEntity pwInquiry(@RequestBody @Valid PwInquiryDto pwInquiryDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        String name = pwInquiryDto.getName();
        String serviceNumber = pwInquiryDto.getServiceNumber();
        String job = pwInquiryDto.getJob();
        String affiliation = pwInquiryDto.getAffiliation();

        IdentityVerification identityVerification = identityVerificationService.findByServiceNumber(serviceNumber);

        if (identityVerification != null &&
            name.equals(identityVerification.getName()) &&
            job.equals(identityVerification.getJob()) &&
            affiliation.equals(identityVerification.getAffiliation())){
                return new ResponseEntity(HttpStatus.OK);
            }else{
                return new ResponseEntity("본인인증에 실패했습니다.", HttpStatus.UNAUTHORIZED);

                
            }
    }

    @PostMapping("/idCheck")
    public ResponseEntity idCheck(@RequestBody @Valid IdCheckDto iCheckDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        String userId = iCheckDto.getUserId();
        boolean isUserIdExists = memberService.isUserIdExists(userId);
        
        if (isUserIdExists) {
            return new ResponseEntity(HttpStatus.OK); // ID가 존재하면 HttpStatus.OK 반환
        } else {
            return new ResponseEntity("아이디를 찾을 수 없습니다.", HttpStatus.NOT_FOUND); // ID가 존재하지 않으면 HttpStatus.NOT_FOUND 반환
        }
    }
    
    @PostMapping("/idInquiry")
    public ResponseEntity<?> idInquiry(@RequestBody @Valid IdInquiryDto idInquiryDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        String name = idInquiryDto.getName();
        String serviceNumber = idInquiryDto.getServiceNumber();
        String job = idInquiryDto.getJob();
        String affiliation = idInquiryDto.getAffiliation();

        // 본인인증 DB에서 정보 확인
        IdentityVerification identityVerification = identityVerificationService.findByServiceNumber(serviceNumber);

        if (identityVerification != null &&
            name.equals(identityVerification.getName()) &&
            job.equals(identityVerification.getJob()) &&
            affiliation.equals(identityVerification.getAffiliation())) {
            // 본인인증 성공

            // Member DB에서 userId 조회
            String userId = memberService.findUserIdByServiceNumber(serviceNumber);

            if (userId != null) {
                // userId를 찾았을 경우
                return new ResponseEntity(userId, HttpStatus.OK);
            } else {
                // userId를 찾지 못했을 경우
                return new ResponseEntity("아이디를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
            }
        } else {
            // 본인인증 실패 또는 정보 불일치
            return new ResponseEntity("본인인증에 실패했습니다.", HttpStatus.UNAUTHORIZED);
        }
    }
}