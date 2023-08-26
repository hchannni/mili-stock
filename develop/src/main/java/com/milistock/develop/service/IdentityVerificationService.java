package com.milistock.develop.service;

import com.milistock.develop.domain.IdentityVerification;
import com.milistock.develop.repository.IdentityVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdentityVerificationService {
    private final IdentityVerificationRepository identityVerificationRepository;
    //private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public IdentityVerification findByServiceNumber(String serviceNumber){
        return identityVerificationRepository.findByServiceNumber(serviceNumber).orElseThrow(() -> new IllegalArgumentException("본인인증에 실패하였습니다."));
    }

    /*@Transactional
    public Member addMember(Member member) {
        Optional<Role> userRole = roleRepository.findByName("ROLE_USER");
        member.addRole(userRole.get());
        Member saveMember = memberRepository.save(member);
        return saveMember;
    }
*/
    /*@Transactional(readOnly = true)
    public Optional<Member> getMember(Long memberId){
        return memberRepository.findById(memberId);
    }
    

    @Transactional(readOnly = true)
    public Optional<Member> getMember(String serveiceNumber){
        return memberRepository.findByServiceNumber(serveiceNumber);
    }*/
}
