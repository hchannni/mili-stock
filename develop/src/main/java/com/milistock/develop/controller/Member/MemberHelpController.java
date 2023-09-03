package com.milistock.develop.controller.Member;



import com.milistock.develop.domain.IdentityVerification;
import com.milistock.develop.service.MemberService;

import lombok.RequiredArgsConstructor;

import com.milistock.develop.service.IdentityVerificationService;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

import com.milistock.develop.dto.IdInquiryDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/members/help")
public class MemberHelpController {

    private final IdentityVerificationService identityVerificationService;
    private final MemberService memberService;

    @PostMapping("/idInquiry")
    public ResponseEntity idInquiry(@RequestBody @Valid IdInquiryDto idInquiryDto, BindingResult bindingResult) {
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
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }
}