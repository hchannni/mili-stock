package com.milistock.develop.controller.Member;

import com.milistock.develop.domain.Member;
import com.milistock.develop.domain.RefreshToken;
import com.milistock.develop.domain.Role;
import com.milistock.develop.domain.IdentityVerification;
import com.milistock.develop.dto.*;
import com.milistock.develop.exception.BusinessExceptionHandler;
import com.milistock.develop.repository.MemberRepository;
//import com.milistock.develop.security.jwt.util.IfLogin;
import com.milistock.develop.security.jwt.util.JwtTokenizer;
//import com.milistock.develop.security.jwt.util.LoginUserDto;
import com.milistock.develop.service.MemberService;
import com.milistock.develop.service.IdentityVerificationService;
import com.milistock.develop.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.milistock.develop.code.*;

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
    private final MemberRepository memberRepository;

    @PostMapping("/identity")
    public ResponseEntity<?> identity(@RequestBody @Valid MemberIdentityVerificationDto identityVerificationDto) {
    
        
            IdentityVerification identityVerification = identityVerificationService.findByServiceNumber(identityVerificationDto.getServiceNumber());
    
            if (!identityVerificationDto.getName().equals(identityVerification.getName())) {
                throw new BusinessExceptionHandler("이름이 일치하지 않습니다.", ErrorCode.NAME_ERROR);
            }
    
            if (!identityVerificationDto.getAffiliation().equals(identityVerification.getAffiliation())) {
                throw new BusinessExceptionHandler("소속이 일치하지 않습니다.",ErrorCode.AFFILIATION_ERROR);
            }
    
            if (!identityVerificationDto.getJob().equals(identityVerification.getJob())) {
                throw new BusinessExceptionHandler("직군이 일치하지 않습니다.",ErrorCode.JOB_ERROR);
            }
    
            MemberIdentityVerificationResponseDto identityVerificationResponse = MemberIdentityVerificationResponseDto.builder()
                    .status(200)
                    .userNumber(identityVerification.getUserNumber())
                    .name(identityVerification.getName())
                    .serviceNumber(identityVerification.getServiceNumber())
                    .job(identityVerification.getJob())
                    .affiliation(identityVerification.getAffiliation())
                    .build();
            return new ResponseEntity<>(identityVerificationResponse, HttpStatus.OK);
    }

    @PostMapping("/idDuplicate")
    public ResponseEntity<?> idDuplicate(@RequestBody @Valid IdDuplicateCheckDto idDuplicateCheckDto) {

        String userId = idDuplicateCheckDto.getUserId();
        boolean isUserIdExists = memberService.isUserIdExists(userId);
        
        if (isUserIdExists) {
            throw new BusinessExceptionHandler("아이디가 이미 사용 중입니다.", ErrorCode.ID_CONFLICT); // 아이디 중복 시 CONFLICT 반환
        } else {
            IdDuplicateResponseDto idDuplicateResponseDto = IdDuplicateResponseDto.builder()
                    .status(200)
                    .userId(idDuplicateCheckDto.getUserId())
                    .build();
            return new ResponseEntity<>(idDuplicateResponseDto, HttpStatus.OK); // 아이디 사용 가능 시 OK 반환
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid MemberSignupDto memberSignupDto) {

        String userId = memberSignupDto.getUserId();
        boolean isUserIdExists = memberService.isUserIdExists(userId);

        if (isUserIdExists) {
            throw new BusinessExceptionHandler("아이디가 이미 사용 중입니다.", ErrorCode.ID_CONFLICT); // 아이디 중복 시 CONFLICT 반환
        }

        String serviceNumber = memberSignupDto.getServiceNumber();
        boolean isServiceNumberExists = memberService.isServiceNumberExists(serviceNumber);

        if (isServiceNumberExists) {
            throw new BusinessExceptionHandler("군번이 이미 사용 중입니다.", ErrorCode.SERVICENUMBER_CONFLICT); // 아이디 중복 시 CONFLICT 반환
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
        member.setGender(memberSignupDto.getGender());
        member.setAppointment(memberSignupDto.getAppointment());
        member.setDischarge(memberSignupDto.getDischarge());

        Member saveMember = memberService.addMember(member);

        MemberSignupResponseDto memberSignupResponse = new MemberSignupResponseDto();
        memberSignupResponse.setStatus(201);
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
        memberSignupResponse.setGender(saveMember.getGender());
        memberSignupResponse.setAppointment(saveMember.getAppointment());
        memberSignupResponse.setDischarge(saveMember.getDischarge());

        // 회원가입
        return new ResponseEntity<>(memberSignupResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid MemberLoginDto loginDto) {
        
        // UserId 없을 경우 Exception이 발생한다. Global Exception에 대한 처리가 필요하다.
        Member member = memberService.findByUserId(loginDto.getUserId());
        if (member == null) {
            throw new BusinessExceptionHandler("아이디 또는 비밀번호를 잘못 입력했습니다. 입력하신 내용을 다시 확인해주세요.", ErrorCode.UNAUTHORIZED);
        }

        if(!member.getUserId().equals(loginDto.getUserId())){
            throw new BusinessExceptionHandler("아이디 또는 비밀번호를 잘못 입력했습니다. 입력하신 내용을 다시 확인해주세요.", ErrorCode.UNAUTHORIZED);
        }

        if(loginDto.getPassword().isEmpty() || loginDto.getPassword().isBlank()){
            throw new BusinessExceptionHandler("비밀번호를 입력하세요.", ErrorCode.UNAUTHORIZED);
        }

        if(!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())){
            throw new BusinessExceptionHandler("아이디 또는 비밀번호를 잘못 입력했습니다. 입력하신 내용을 다시 확인해주세요.", ErrorCode.UNAUTHORIZED);
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
                .status(200)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberId(member.getMemberId())
                .name(member.getName())
                .build();
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenDto refreshTokenDto) {
        if (!refreshTokenService.isValidRefreshToken(refreshTokenDto.getRefreshToken())) {
            throw new BusinessExceptionHandler("Invalid refresh token", ErrorCode.UNAUTHORIZED);
        }

        refreshTokenService.deleteRefreshToken(refreshTokenDto.getRefreshToken());

        HttpOkResponseDto logoutResponseDto = HttpOkResponseDto.builder()
        .status(200)
        .build();

        return new ResponseEntity<>(logoutResponseDto, HttpStatus.OK);
    }


    @PostMapping("/refreshToken")
    public ResponseEntity<?> requestRefresh(@RequestBody RefreshTokenDto refreshTokenDto) {
        RefreshToken refreshToken = refreshTokenService.findRefreshToken(refreshTokenDto.getRefreshToken()).orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));
        Claims claims = jwtTokenizer.parseRefreshToken(refreshToken.getValue());

        Long memberId = Long.valueOf((Integer)claims.get("memberId"));

        Member member = memberService.getMember(memberId).orElseThrow(() -> new IllegalArgumentException("Member not found"));


        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.get("roles");

        String serviceNumber = claims.getSubject();

        String accessToken = jwtTokenizer.createAccessToken(memberId, serviceNumber, member.getName(), roles);

        MemberLoginResponseDto loginResponse = MemberLoginResponseDto.builder()
                .status(200)
                .accessToken(accessToken)
                .refreshToken(refreshTokenDto.getRefreshToken())
                .memberId(member.getMemberId())
                .name(member.getName())
                .build();
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    // @GetMapping("/info")
    // public ResponseEntity<?> userinfo(@IfLogin LoginUserDto loginUserDto) {
    //     Member member = memberService.findByUserId(loginUserDto.getServiceNumber());
    //     return new ResponseEntity<>(member, HttpStatus.OK);
    // }

    @GetMapping("/all")
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    

    
}
