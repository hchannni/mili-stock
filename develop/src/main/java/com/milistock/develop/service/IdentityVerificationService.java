package com.milistock.develop.service;

import com.milistock.develop.domain.IdentityVerification;
import com.milistock.develop.repository.IdentityVerificationRepository;
import com.milistock.develop.exception.*;
import com.milistock.develop.code.*;
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
        throw new BusinessExceptionHandler("존재하지 않는 군번입니다.", ErrorCode.UNAUTHORIZED); // 예외를 UnauthorizedException으로 변경
    }
}

    @Transactional(readOnly = true)
    public Optional<IdentityVerification> getServiceNumber(String serviceNumber){
        return identityVerificationRepository.findByServiceNumber(serviceNumber);
    }
}
