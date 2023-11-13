package com.milistock.develop.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BoardPostResponseDto {

    private Long boardId;
    private String title;
    private String content;
    private String name;
    private Long memberId;
    private LocalDateTime date;
    private boolean answered;
}
