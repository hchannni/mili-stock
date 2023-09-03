package com.milistock.develop.controller.Member;

import com.milistock.develop.domain.Member;
import com.milistock.develop.domain.RefreshToken;
import com.milistock.develop.domain.Role;
import com.milistock.develop.domain.IdentityVerification;
import com.milistock.develop.dto.*;
import com.milistock.develop.security.jwt.util.IfLogin;
import com.milistock.develop.security.jwt.util.JwtTokenizer;
import com.milistock.develop.security.jwt.util.LoginUserDto;
import com.milistock.develop.service.MemberService;
import com.milistock.develop.service.IdentityVerificationService;
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

    @PostMapping("/identity")
    public ResponseEntity identity(@RequestBody @Valid MemberIdentityVerificationDto identityVerificationDto, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        // ServiceNumber가 없을 경우 Exception이 발생한다. Global Exception에 대한 처리가 필요하다.
        IdentityVerification identityVerification = identityVerificationService.findByServiceNumber(identityVerificationDto.getServiceNumber());

        if(!identityVerificationDto.getName().equals(identityVerification.getName())){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    
        if(!identityVerificationDto.getAffiliation().equals(identityVerification.getAffiliation())){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    
        if(!identityVerificationDto.getJob().equals(identityVerification.getJob())){
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

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody @Valid MemberSignupDto memberSignupDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        Member member = new Member();
        member.setServiceNumber(memberSignupDto.getServiceNumber());
        member.setName(memberSignupDto.getName());
        member.setUserId(memberSignupDto.getUserId());
        member.setPassword(passwordEncoder.encode(memberSignupDto.getPassword()));
        member.setJob(memberSignupDto.getJob());
        member.setAffiliation(memberSignupDto.getAffiliation());
        member.setMilitaryRank(memberSignupDto.getMilitaryRank());
        member.setBirth(memberSignupDto.getBirth());
        member.setPhoneNumber(memberSignupDto.getPhoneNumber());
        member.setEmail(memberSignupDto.getEmail());
        member.setChild(memberSignupDto.getChild());
        member.setGender(memberSignupDto.getGender());
        member.setAppointment(memberSignupDto.getAppointment());
        member.setDischarge(memberSignupDto.getDischarge());

        Member saveMember = memberService.addMember(member);

        MemberSignupResponseDto memberSignupResponse = new MemberSignupResponseDto();
        memberSignupResponse.setMemberId(saveMember.getMemberId());
        memberSignupResponse.setServiceNumber(saveMember.getServiceNumber());
        memberSignupResponse.setName(saveMember.getName());
        memberSignupResponse.setUserId(saveMember.getUserId());
        memberSignupResponse.setJob(saveMember.getJob());
        memberSignupResponse.setAffiliation(saveMember.getAffiliation());
        memberSignupResponse.setMilitaryRank(saveMember.getMilitaryRank());
        memberSignupResponse.setBirth(saveMember.getBirth());
        memberSignupResponse.setPhoneNumber(saveMember.getPhoneNumber());
        memberSignupResponse.setEmail(saveMember.getEmail());
        memberSignupResponse.setChild(saveMember.getChild());
        memberSignupResponse.setGender(saveMember.getGender());
        memberSignupResponse.setAppointment(saveMember.getAppointment());
        memberSignupResponse.setDischarge(saveMember.getDischarge());

        // 회원가입
        return new ResponseEntity(memberSignupResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid MemberLoginDto loginDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        // UserId 없을 경우 Exception이 발생한다. Global Exception에 대한 처리가 필요하다.
        Member member = memberService.findByUserId(loginDto.getUserId());

        if(!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        
        // List<Role> ===> List<String>
        List<String> roles = member.getRoles().stream().map(Role::getName).collect(Collectors.toList());

        // JWT토큰을 생성하였다. jwt라이브러리를 이용하여 생성.
        String accessToken = jwtTokenizer.createAccessToken(member.getMemberId(), member.getServiceNumber(), member.getName(), roles);
        String refreshToken = jwtTokenizer.createRefreshToken(member.getMemberId(), member.getServiceNumber(), member.getName(), roles);

        // RefreshToken을 DB에 저장한다. 성능 때문에 DB가 아니라 Redis에 저장하는 것이 좋다.
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setValue(refreshToken);
        refreshTokenEntity.setMemberId(member.getMemberId());
        refreshTokenService.addRefreshToken(refreshTokenEntity);

        MemberLoginResponseDto loginResponse = MemberLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberId(member.getMemberId())
                .name(member.getName())
                .build();
        return new ResponseEntity(loginResponse, HttpStatus.OK);
    }

    @DeleteMapping("/logout")
    public ResponseEntity logout(@RequestBody RefreshTokenDto refreshTokenDto) {
        if (!refreshTokenService.isValidRefreshToken(refreshTokenDto.getRefreshToken())) {
            return new ResponseEntity("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }
        refreshTokenService.deleteRefreshToken(refreshTokenDto.getRefreshToken());
        return new ResponseEntity(HttpStatus.OK);
    }


    @PostMapping("/refreshToken")
    public ResponseEntity requestRefresh(@RequestBody RefreshTokenDto refreshTokenDto) {
        RefreshToken refreshToken = refreshTokenService.findRefreshToken(refreshTokenDto.getRefreshToken()).orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));
        Claims claims = jwtTokenizer.parseRefreshToken(refreshToken.getValue());

        Long memberId = Long.valueOf((Integer)claims.get("memberId"));

        Member member = memberService.getMember(memberId).orElseThrow(() -> new IllegalArgumentException("Member not found"));


        List roles = (List) claims.get("roles");
        String serviceNumber = claims.getSubject();

        String accessToken = jwtTokenizer.createAccessToken(memberId, serviceNumber, member.getName(), roles);

        MemberLoginResponseDto loginResponse = MemberLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenDto.getRefreshToken())
                .memberId(member.getMemberId())
                .name(member.getName())
                .build();
        return new ResponseEntity(loginResponse, HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity userinfo(@IfLogin LoginUserDto loginUserDto) {
        Member member = memberService.findByUserId(loginUserDto.getServiceNumber());
        return new ResponseEntity(member, HttpStatus.OK);
    }

    

    
}