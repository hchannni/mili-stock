package com.milistock.develop.service;

import com.milistock.develop.domain.Member;
import com.milistock.develop.domain.Role;
import com.milistock.develop.repository.MemberRepository;
import com.milistock.develop.repository.RoleRepository;
import com.milistock.develop.exception.*;
import com.milistock.develop.code.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Member findByUserId(String userid){
        Optional<Member> memberid = memberRepository.findByUserId(userid); // 이거는 바로 jpa 데이터베이스랑 비교하는 것
        if(memberid.isPresent()){
            if (userid.equals(memberid.get().getUserId())) {
                return memberid.get();
            } else{
                return null;
            }
        }else{
            return null;
        }

    }

    @Transactional
    public Member addMember(Member member) {
        Optional<Role> userRole = roleRepository.findByName("ROLE_USER");
        member.addRole(userRole.get());
        Member saveMember = memberRepository.save(member);
        return saveMember;
    }

    @Transactional(readOnly = true)
    public Optional<Member> getMember(Long memberId){
        return memberRepository.findById(memberId);
    }

    @Transactional(readOnly = true)
    public Optional<Member> getMember(String userid){
        return memberRepository.findByUserId(userid);
    }

    @Transactional(readOnly = true)
    public String findUserIdByServiceNumber(String serviceNumber) {
        Optional<Member> memberOptional = memberRepository.findByServiceNumber(serviceNumber);
        if (memberOptional.isPresent()) {
            return memberOptional.get().getUserId();
        } else {
            // 사용자가 없을 경우 null 또는 원하는 응답을 반환할 수 있습니다.
            return null;
        }
    }

    @Transactional(readOnly = true)
    public boolean isUserIdExists(String userId) {
        return memberRepository.existsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public boolean isServiceNumberExists(String serviceNumber) {
        return memberRepository.existsByServiceNumber(serviceNumber);
    }

    @Transactional
    public Member updateMemberPw(String userId, String newPassword) {
        Member existingMember = memberRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));

        // 새로운 비밀번호를 인코딩하여 저장
        existingMember.setPassword(passwordEncoder.encode(newPassword));

        // 회원 정보 업데이트
        Member saveMember = memberRepository.save(existingMember);
        return saveMember;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 태연 수정!
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Transactional(readOnly = true)
    public Member findByMemberId(Long memberId){
        Optional<Member> member = memberRepository.findByMemberId(memberId); // 현재 로그인한 회원의 장바구니 엔티티 조회
        
        // 회원이 장바구니 없으면, 에러
        if (member.isPresent()) {
            return member.get();
        } else {
            throw new BusinessExceptionHandler("유저가 존재 안 합니다", ErrorCode.NOT_FOUND_ERROR); 
        }
    }

    @Transactional(readOnly = true)
    public boolean isMemberIdExists(Long memberId) {
        boolean exist = memberRepository.existsByMemberId(memberId);
        if(exist){
            return true;
        } else{
            throw new BusinessExceptionHandler("유저가 존재 안 합니다", ErrorCode.NOT_FOUND_ERROR); 
        }
    }

    //@Transactional(readOnly = true)
    //public Member findByMemberId(Long userid){
    //    return memberRepository.findByMemberId(userid).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
        
    //}
}
