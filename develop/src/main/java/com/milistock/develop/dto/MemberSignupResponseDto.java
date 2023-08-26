package com.milistock.develop.dto;

import lombok.Data;

@Data
public class MemberSignupResponseDto {
    private Long memberId;
    private String serviceNumber;
    private String name;
    private String userId;
    private String job;
    private String affiliation;
    private String militaryRank;
    private String birth;
    private String phoneNumber;
    private String email;
    private int child;
    private String gender;
    private String appointment;
    private String discharge;
}