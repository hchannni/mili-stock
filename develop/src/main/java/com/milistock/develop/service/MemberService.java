package com.milistock.develop.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.milistock.develop.domain.Member;
import com.milistock.develop.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Member findByMemberId(Long userid){
        return memberRepository.findByMemberId(userid).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
    }
}
