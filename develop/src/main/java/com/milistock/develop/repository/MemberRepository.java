package com.milistock.develop.repository;

import com.milistock.develop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(String userId);
    Optional<Member> findByServiceNumber(String serviceNumber);
    boolean existsByUserId(String userId); // 아이디가 존재하는지 확인
    boolean existsByServiceNumber(String serviceNumber); // 군번이 존재하는지 확인
}