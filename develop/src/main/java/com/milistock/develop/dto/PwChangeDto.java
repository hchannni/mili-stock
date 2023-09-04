package com.milistock.develop.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class PwChangeDto {

    @NotBlank(message = "아이디는 필수 입력 항목입니다.")
    private String userId;

    @NotBlank(message = "새로운 비밀번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$",
            message = "비밀번호는 영문, 특수문자 8자 이상 20자 이하여야합니다.")
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
