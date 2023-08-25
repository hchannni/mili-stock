package com.milistock.develop.controller.Member;

import com.milistock.develop.domain.Member;
import com.milistock.develop.domain.RefreshToken;
import com.milistock.develop.domain.IdentityVerification;
import com.milistock.develop.dto.*;
import com.milistock.develop.security.jwt.util.IfLogin;
import com.milistock.develop.security.jwt.util.JwtTokenizer;
import com.milistock.develop.security.jwt.util.LoginUserDto;
import com.milistock.develop.service.IdentityVerificationService;
import com.milistock.develop.service.MemberService;
import com.milistock.develop.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/members")
public class MemberController {

    private final JwtTokenizer jwtTokenizer;
    private final MemberService memberService;
    private final IdentityVerificationService identityVerificationService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

//    public MemberController(JwtTokenizer jwtTokenizer, MemberService memberService, RefreshTokenService refreshTokenService, PasswordEncoder passwordEncoder) {
//        this.jwtTokenizer = jwtTokenizer;
//        this.memberService = memberService;
//        this.refreshTokenService = refreshTokenService;
//        this.passwordEncoder = passwordEncoder;
//    }
    @PostMapping("/Identity")
    public ResponseEntity identity(@RequestBody @Valid MemberIdentityVerificationDto identityVerificationDto, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        // ServiceNumber가 없을 경우 Exception이 발생한다. Global Exception에 대한 처리가 필요하다.
        IdentityVerification identityVerification = identityVerificationService.findByServiceNumber(identityVerificationDto.getServiceNumber());

        if(identityVerificationDto.getName() != identityVerificationDto.getName()){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        if(identityVerificationDto.getAffiliation() != identityVerificationDto.getAffiliation()){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        if(identityVerificationDto.getJob() != identityVerificationDto.getJob()){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        MemberIdentityVerificationResponseDto identityVerificationResponse = MemberIdentityVerificationResponseDto.builder()
                .userNumber(identityVerification.getUserNumber())
                .name(identityVerification.getName())
                .serviceNumber(identityVerification.getServiceNumber())
                .job(identityVerification.getJob())
                .affiliation(identityVerification.getAffiliation())
                .build();
        return new ResponseEntity(identityVerificationResponse, HttpStatus.OK);
    }


}