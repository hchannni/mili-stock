package com.milistock.develop.security.jwt.util;

import lombok.Data;

@Data
public class LoginInfoDto {
    private Long memberId;
    private String serviceNumber;
    private String name;
}