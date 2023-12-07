package com.milistock.develop.controller.Member;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.milistock.develop.code.ErrorCode;
import com.milistock.develop.domain.IdentityVerification;
import com.milistock.develop.domain.Member;
import com.milistock.develop.dto.HttpOkResponseDto;
import com.milistock.develop.dto.IdCheckDto;
import com.milistock.develop.dto.IdInquiryDto;
import com.milistock.develop.dto.PwChangeDto;
import com.milistock.develop.dto.PwInquiryDto;
import com.milistock.develop.dto.PwInquiryResponseDto;
import com.milistock.develop.exception.BusinessExceptionHandler;
import com.milistock.develop.service.IdentityVerificationService;
import com.milistock.develop.service.MemberService;

import lombok.RequiredArgsConstructor;

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
        if (member != null) {
            PwInquiryResponseDto idCheckResponseDto = PwInquiryResponseDto.builder()
                    .status(200)
                    .userId(userId)
                    .build();
            return new ResponseEntity<>(idCheckResponseDto, HttpStatus.OK); // ID가 존재하면 userID와 HttpStatus.OK 반환
        } else {
            throw new BusinessExceptionHandler("존재하지 않는 아이디 입니다.", ErrorCode.ID_ERROR); // ID가 존재하지 않으면
                                                                                        // HttpStatus.NOT_FOUND 반환
        }
    }

    @PostMapping("/pwInquiry") // 비밀번호 찾기위한 본인인증
    public ResponseEntity<?> pwInquiry(@RequestBody @Valid PwInquiryDto pwInquiryDto) {

        String userId = pwInquiryDto.getUserId();
        String name = pwInquiryDto.getName();
        String serviceNumber = pwInquiryDto.getServiceNumber();
        String job = pwInquiryDto.getJob();
        String affiliation = pwInquiryDto.getAffiliation();

        Member member = memberService.findByUserId(userId); // ID로 회원정보 조회
        String userIdForIdentity = memberService.findUserIdByServiceNumber(serviceNumber); // 군번으로 회원가입 내역 조사

        if (member == null) {
            throw new BusinessExceptionHandler("존재하지 않는 아이디 입니다.", ErrorCode.ID_ERROR);
        }
        if (!name.equals(member.getName())) {
            throw new BusinessExceptionHandler("이름이 일치하지 않습니다.", ErrorCode.NAME_ERROR);
        }
        if (!job.equals(member.getJob())) {
            throw new BusinessExceptionHandler("직군이 일치하지 않습니다.", ErrorCode.JOB_ERROR);
        }
        if (!affiliation.equals(member.getAffiliation())) {
            throw new BusinessExceptionHandler("소속이 일치하지 않습니다.", ErrorCode.AFFILIATION_ERROR);
        }
        if (!serviceNumber.equals(member.getServiceNumber())) {
            throw new BusinessExceptionHandler("군번이 일치하지 않습니다.", ErrorCode.SERVICENUMBER_ERROR);
        }
        PwInquiryResponseDto pwInquiryResponseDto = PwInquiryResponseDto.builder()
                .status(200)
                .userId(userIdForIdentity)
                .build();
        return new ResponseEntity<>(pwInquiryResponseDto, HttpStatus.OK);

    }

    @PostMapping("/pwChange") // 비밀번호 변경 메소드
    public ResponseEntity<?> pwChange(@RequestBody @Valid PwChangeDto pwChangeDto) {

        String userId = pwChangeDto.getUserId();
        String newPassword = pwChangeDto.getNewPassword();

        Member member = memberService.findByUserId(userId); // userId로 회원 정보 조회

        if (!member.getUserId().equals(userId)) { // ID 대소문자 비교
            throw new BusinessExceptionHandler("존재하지 않는 아이디 입니다.", ErrorCode.ID_ERROR); // front 오류
        } else {
            memberService.updateMemberPw(userId, newPassword);
            HttpOkResponseDto pwChangeResponseDto = HttpOkResponseDto.builder()
                    .status(200)
                    .build();
            return new ResponseEntity<>(pwChangeResponseDto, HttpStatus.OK);
        } // userId로 회원 정보 조회
    }

    @PostMapping("/idInquiry") // 아이디 찾기 메소드(본인인증)
    public ResponseEntity<?> idInquiry(@RequestBody @Valid IdInquiryDto idInquiryDto) {

        String name = idInquiryDto.getName();
        String serviceNumber = idInquiryDto.getServiceNumber();
        String job = idInquiryDto.getJob();
        String affiliation = idInquiryDto.getAffiliation();

        // 본인인증 DB에서 정보 확인
        IdentityVerification identityVerification = identityVerificationService.findByServiceNumber(serviceNumber);

        if (!name.equals(identityVerification.getName())) {
            throw new BusinessExceptionHandler("이름이 일치하지 않습니다.", ErrorCode.NAME_ERROR);
        }

        if (!affiliation.equals(identityVerification.getAffiliation())) {
            throw new BusinessExceptionHandler("소속이 일치하지 않습니다.", ErrorCode.AFFILIATION_ERROR);
        }

        if (!job.equals(identityVerification.getJob())) {
            throw new BusinessExceptionHandler("직업이 일치하지 않습니다.", ErrorCode.JOB_ERROR);
        }
        // 본인인증 성공

        // Member DB에서 serviceNumber로 userId 조회
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

    }
}