package com.milistock.develop.service;

import com.milistock.develop.domain.Member;
import com.milistock.develop.domain.Role;
import com.milistock.develop.repository.MemberRepository;
import com.milistock.develop.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Member findByUserId(String userid){
        return memberRepository.findByUserId(userid).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
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
}