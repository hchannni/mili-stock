package com.milistock.develop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberIdentityVerificationResponseDto {
    private Integer status;
    private Long userNumber;
    private String name;
    private String serviceNumber;
    private String job;
    private String affiliation;
}