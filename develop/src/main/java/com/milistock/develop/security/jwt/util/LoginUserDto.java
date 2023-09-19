package com.milistock.develop.security.jwt.util;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LoginUserDto {
    private String serviceNumber;
    private String name;
    private Long memberId;
    private List<String> roles = new ArrayList<>();

    public void addRole(String role){
        roles.add(role);
    }
}