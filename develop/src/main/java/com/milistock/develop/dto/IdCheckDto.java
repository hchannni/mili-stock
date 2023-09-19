package com.milistock.develop.dto;

import javax.validation.constraints.NotBlank;

public class IdCheckDto {
    @NotBlank(message = "아이디를 입력 해주세요")
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
