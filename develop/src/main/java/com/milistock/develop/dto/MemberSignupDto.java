package com.milistock.develop.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignupDto {

        @NotBlank
        @Pattern(regexp = "\\d{2}-\\d{4,}",
                message = "군번 형식을 맞춰야 합니다.")
        private String serviceNumber;

        @NotBlank
        @Pattern(regexp = "^[a-zA-Z가-힣\\\\s]{2,15}",
                message = "이름은 영문자, 한글, 공백포함 2글자부터 15글자까지 가능합니다.")
        private String name;

        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{2,20}$",
                message = "아이디는 영문자+숫자를 포함한 2~20자여야 합니다.")
        private String userId;

        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{7,16}$",
                message = "비밀번호는 영문+숫자+특수문자를 포함한 8~20자여야 합니다")
        private String password;

        @NotNull
        @Pattern(regexp = "(병사|간부|군무원)",
                message = "직군은 병사/간부/군무원만 가능합니다.")
        private String job;

        @NotNull
        @Pattern(regexp = "(공군|육군|해군)",
                message = "소속은 공군/육군/해군만 가능합니다.")
        private String affiliation;

        @NotNull
        private String militaryRank;

        @NotNull
        @Pattern(regexp = "^(19|20)\\d{2}\\.(0[1-9]|1[0-2])\\.(0[1-9]|[12][0-9]|3[01])$",
                message = "생년월일 형식을 맞춰야합니다.")
        private String birth;

        @NotNull
        private String phoneNumber;

        @NotNull
        @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
                message = "이메일 형식을 맞춰야합니다")
        private String email;

        @NotNull
        private int child;

        @NotNull
        private String gender;

        @NotNull
        @Pattern(regexp = "^(19|20)\\d{2}\\.(0[1-9]|1[0-2])\\.(0[1-9]|[12][0-9]|3[01])$",
                message = "임관일자 형식을 맞춰야합니다.")
        private String appointment;

        @NotNull
        @Pattern(regexp = "^(19|20)\\d{2}\\.(0[1-9]|1[0-2])\\.(0[1-9]|[12][0-9]|3[01])$",
                message = "전역일자 형식을 맞춰야합니다.")
        private String discharge;
}