package com.milistock.develop.dto;

import javax.validation.constraints.NotBlank;

public class PwInquiryDto {


    @NotBlank(message = "아이디는 필수 입력 항목입니다.")
    private String userId;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    @NotBlank(message = "군번은 필수 입력 항목입니다.")
    private String serviceNumber;

    @NotBlank(message = "직업은 필수 입력 항목입니다.")
    private String job;

    @NotBlank(message = "소속은 필수 입력 항목입니다.")
    private String affiliation;

    // Getter와 Setter 메서드

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceNumber() {
        return serviceNumber;
    }

    public void setServiceNumber(String serviceNumber) {
        this.serviceNumber = serviceNumber;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }
}
