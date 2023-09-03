package com.milistock.develop.dto;

import javax.validation.constraints.NotBlank;

public class PwChangeDto {

    @NotBlank(message = "아이디는 필수 입력 항목입니다.")
    private String userId;

    @NotBlank(message = "새로운 비밀번호는 필수 입력 항목입니다.")
    private String newPassword;

    // Getter와 Setter 메서드

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
