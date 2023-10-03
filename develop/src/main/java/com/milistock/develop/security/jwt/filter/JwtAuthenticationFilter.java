package com.milistock.develop.security.jwt.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.milistock.develop.security.jwt.exception.JwtExceptionCode;
import com.milistock.develop.security.jwt.token.JwtAuthenticationToken;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = "";
        try {
            token = getToken(request);
            if (StringUtils.hasText(token)) {
                getAuthentication(token);
            }

        } catch (NullPointerException | IllegalStateException e) {
            request.setAttribute("exception", JwtExceptionCode.NOT_FOUND_TOKEN.getCode());

        } catch (SecurityException | MalformedJwtException e) {
            request.setAttribute("exception", JwtExceptionCode.INVALID_TOKEN.getCode());

        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", JwtExceptionCode.EXPIRED_TOKEN.getCode());

        } catch (UnsupportedJwtException e) {
            request.setAttribute("exception", JwtExceptionCode.UNSUPPORTED_TOKEN.getCode());
            
        } catch (Exception e) {
            request.setAttribute("exception", JwtExceptionCode.UNKNOWN_ERROR.getCode());
        }
        filterChain.doFilter(request, response);

    }

    private void getAuthentication(String token) {
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(token); // 객체생성
        Authentication authenticate = authenticationManager.authenticate(authenticationToken); // 객체 분석
        // 이 객체에는 JWT안의 내용을 가지고 name,role, memberId를 보내줌

        SecurityContextHolder.getContext().setAuthentication(authenticate); // 현재 요청에서 언제든지 인증정보를 꺼낼 수 있도록 해준다.
    }

    // token값 에서 beark 분리
    private String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer")) {
            String[] arr = authorization.split(" ");
            return arr[1];
        }
        return null;
    }
}