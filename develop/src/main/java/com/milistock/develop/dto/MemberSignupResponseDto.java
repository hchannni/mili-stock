package com.milistock.develop.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberSignupResponseDto {
    private Long memberId;
    private String serviceNumber;
    private String name;
    private String userId;
    private String job;
    private String affiliation;
    private String rank;
    private String birth;
    private String phoneNumber;
    private String email;
    private int child;
    private String gender;
    private String appointment;
    private String discharge;
    private LocalDateTime regdate;
}