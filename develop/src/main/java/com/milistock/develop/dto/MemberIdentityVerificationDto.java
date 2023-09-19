package com.milistock.develop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberIdentityVerificationDto {

    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z가-힣\\\\s]{2,15}",
            message = "이름은 영문자, 한글, 공백포함 2글자부터 15글자까지 가능합니다.")
    private String name;

    @NotEmpty
    @Pattern(regexp = "\\d{2}-\\d{4,}",
            message = "군번 형식을 맞춰야 합니다.")
    private String serviceNumber;

    @NotEmpty
    @Pattern(regexp = "(병사|간부|군무원)",
            message = "직군은 병사/간부/군무원만 가능합니다.")
    private String job;

    @NotEmpty
    @Pattern(regexp = "(공군|육군|해군)",
            message = "소속은 공군/육군/해군만 가능합니다.")
    private String affiliation;
    

    /*@NotNull
    @Pattern(regexp = "^\\d{4}$", message = "생년은 4자리 숫자로 입력해야 합니다")
    private String birthYear;

    @NotNull
    @Pattern(regexp = "^(0?[1-9]|1[012])$", message = "생월은 1부터 12까지의 숫자로 입력해야 합니다")
    private String birthMonth;

    @NotNull
    @Pattern(regexp = "^(0?[1-9]|[12][0-9]|3[01])$", message = "생일은 1부터 31까지의 숫자로 입력해야 합니다")
    private String birthDay;

    @NotEmpty
    @Pattern(regexp = "^[MF]{1}$", message = "성별은 'M' 또는 'F'로 입력해야 합니다")
    private String gender;*/
}