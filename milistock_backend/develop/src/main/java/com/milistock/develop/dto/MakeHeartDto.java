package com.milistock.develop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MakeHeartDto {
    private Long memberId;
    private int productNumber;
}
