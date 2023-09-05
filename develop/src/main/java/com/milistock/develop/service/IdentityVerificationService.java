package com.milistock.develop.service;

import com.milistock.develop.domain.IdentityVerification;
import com.milistock.develop.repository.IdentityVerificationRepository;
import com.milistock.develop.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdentityVerificationService {
    private final IdentityVerificationRepository identityVerificationRepository;
    //private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
public IdentityVerification findByServiceNumber(String serviceNumber) {
    Optional<IdentityVerification> identityVerification = identityVerificationRepository.findByServiceNumber(serviceNumber);
    if (identityVerification.isPresent()) {
        return identityVerification.get();
    } else {
        throw new UnauthorizedException("군번이 일치하지 않습니다."); // 예외를 UnauthorizedException으로 변경
    }
}

    @Transactional(readOnly = true)
    public Optional<IdentityVerification> getServiceNumber(String serviceNumber){
        return identityVerificationRepository.findByServiceNumber(serviceNumber);
    }
}
