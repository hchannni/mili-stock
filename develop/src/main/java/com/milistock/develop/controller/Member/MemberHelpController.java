package com.milistock.develop.controller.Member;



import com.milistock.develop.domain.IdentityVerification;
import com.milistock.develop.service.MemberService;
import com.milistock.develop.dto.*;
import com.milistock.develop.exception.*;
import com.milistock.develop.code.*;

import lombok.RequiredArgsConstructor;

import com.milistock.develop.service.IdentityVerificationService;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import com.milistock.develop.dto.IdInquiryDto;
import com.milistock.develop.dto.PwInquiryDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.milistock.develop.domain.Member;
import com.milistock.develop.dto.PwChangeDto;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/members/help")
public class MemberHelpController {

    private final IdentityVerificationService identityVerificationService;
    private final MemberService memberService;
    
    @PostMapping("/idCheck") // 비밀번호 찾기위한 아이디 확인
    public ResponseEntity<?> idCheck(@RequestBody @Valid IdCheckDto iCheckDto) {

        String userId = iCheckDto.getUserId();
        Member member = memberService.findByUserId(userId); // ID존재 확인
        if (member!=null) {
            PwInquiryResponseDto idCheckResponseDto = PwInquiryResponseDto.builder()
            .status(200)
            .userId(userId)
            .build();
            return new ResponseEntity<>(idCheckResponseDto, HttpStatus.OK); // ID가 존재하면 userID와 HttpStatus.OK 반환
        } else {
            throw new BusinessExceptionHandler("인증 오류", ErrorCode.UNAUTHORIZED); // ID가 존재하지 않으면 HttpStatus.NOT_FOUND 반환
        }
    }
    

    @PostMapping("/pwInquiry") // 비밀번호 찾기위한 본인인증
    public ResponseEntity<?> pwInquiry(@RequestBody @Valid PwInquiryDto pwInquiryDto) {

        String userId = pwInquiryDto.getUserId();
        String name = pwInquiryDto.getName();
        String serviceNumber = pwInquiryDto.getServiceNumber();
        String job = pwInquiryDto.getJob();
        String affiliation = pwInquiryDto.getAffiliation();

        IdentityVerification identityVerification = identityVerificationService.findByServiceNumber(serviceNumber); //군번으로 본인인증 DB조사
        String userIdForIdentity  = memberService.findUserIdByServiceNumber(serviceNumber); // 군번으로 회원가입 내역 조사

        if (identityVerification != null &&
            name.equals(identityVerification.getName()) &&
            job.equals(identityVerification.getJob()) &&
            affiliation.equals(identityVerification.getAffiliation())&&
            userId.equals(userIdForIdentity)){
                    PwInquiryResponseDto pwInquiryResponseDto = PwInquiryResponseDto.builder()
                    .status(200)
                    .userId(userIdForIdentity)
                    .build();
                    return new ResponseEntity<>(pwInquiryResponseDto, HttpStatus.OK);
                }else{
                throw new BusinessExceptionHandler("아이디와 본인인증 정보가 일치하지 않습니다.", ErrorCode.UNAUTHORIZED);
            }
    }

    
    @PostMapping("/pwChange") // 비밀번호 변경 메소드
    public ResponseEntity<?> pwChange(@RequestBody @Valid PwChangeDto pwChangeDto) {

        String userId = pwChangeDto.getUserId();
        String newPassword = pwChangeDto.getNewPassword();

        Member member = memberService.findByUserId(userId); // userId로 회원 정보 조회

        if(!member.getUserId().equals(userId)){             // ID 대소문자 비교
            throw new BusinessExceptionHandler("존재하지 않는 아이디 입니다.", ErrorCode.UNAUTHORIZED); // front 오류
        }else{
            memberService.updateMemberPw(userId,newPassword);
            HttpOkResponseDto pwChangeResponseDto = HttpOkResponseDto.builder()
            .status(200)
            .build();
            return new ResponseEntity<>(pwChangeResponseDto, HttpStatus.OK);
        }        // userId로 회원 정보 조회
    }
    
    @PostMapping("/idInquiry") // 아이디 찾기 메소드(본인인증)
    public ResponseEntity<?> idInquiry(@RequestBody @Valid IdInquiryDto idInquiryDto) {

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
                PwInquiryResponseDto idInquiryResponseDto = PwInquiryResponseDto.builder()
                    .status(200)
                    .userId(userId)
                    .build();
                return new ResponseEntity<>(idInquiryResponseDto, HttpStatus.OK);
            } else {
                // userId를 찾지 못했을 경우
                throw new BusinessExceptionHandler("회원가입 내역이 없습니다.", ErrorCode.NOT_FOUND_ERROR);
            }
        } else {
            // 본인인증 실패 또는 정보 불일치
            throw new BusinessExceptionHandler("본인인증에 실패했습니다.", ErrorCode.UNAUTHORIZED);
        }
    }
}