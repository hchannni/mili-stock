package com.milistock.develop.service;

import com.milistock.develop.domain.RefreshToken;
import com.milistock.develop.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import javax.swing.text.html.Option;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken addRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.findByValue(refreshToken).ifPresent(refreshTokenRepository::delete);
    }

    @Transactional(readOnly = true)
    public Optional<RefreshToken> findRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByValue(refreshToken);
    }

    public boolean isValidRefreshToken(String refreshTokenValue) {
        // 주어진 refresh 토큰 값으로 해당 토큰을 DB에서 찾습니다.
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByValue(refreshTokenValue);
        
        // 찾은 refresh 토큰이 존재하고, 해당 토큰의 memberId가 유효한지 검사합니다.
        if (refreshTokenOptional.isPresent()) {
            RefreshToken refreshToken = refreshTokenOptional.get();
            if (!isValidMemberId(refreshToken.getMemberId())) {
                return false;
            }
            
            // 여기에서 추가적인 유효성 검사 로직을 구현할 수 있음
            
            return true;
        }
        
        return false; // 토큰을 찾지 못한 경우
    }
    
    private boolean isValidMemberId(Long memberId) {
        // memberId의 유효성을 검사하는 로직을 구현
        // 예: 회원 ID가 존재하는지, 잘못된 경우 false 반환
        return true;
    }
}