package com.milistock.develop.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class EditMemberPwChangeDto {

    @NotNull(message = "비밀번호는 필수입력 항목입니다.")
    private String currentPassword;

    @NotBlank(message = "새로운 비밀번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$",
            message = "비밀번호는 영문, 특수문자 8자 이상 20자 이하여야합니다.")
    private String newPassword;

    @NotBlank(message = "비밀번호 확인은 필수 입력 항목입니다.")
    private String newPasswordConfirmation;

    // Getter와 Setter 메서드

    public String getcurrentPassword() {
        return currentPassword;
    }

    public void setcurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordConfirmation() {
        return newPasswordConfirmation;
    }

    public void setNewPasswordConfirmation(String newPasswordConfirmation) {
        this.newPasswordConfirmation = newPasswordConfirmation;
    }
}
