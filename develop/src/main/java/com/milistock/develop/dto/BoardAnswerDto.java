package com.milistock.develop.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardAnswerDto {
    
    private Long boardId;
    private String answer;
}
