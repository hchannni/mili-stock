package com.milistock.develop.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class IdDuplicateCheckDto {
    @NotBlank(message = "아이디를 입력 해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{2,20}$",
    message = "아이디는 영문자+숫자를 포함한 2~20자여야 합니다.")
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
