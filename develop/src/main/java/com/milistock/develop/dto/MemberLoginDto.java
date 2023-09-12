package com.milistock.develop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginDto {   

    @NotNull
    private String userId;

    @NotEmpty // 영문, 특수문자 8자 이상 20자 이하
    private String password;
}