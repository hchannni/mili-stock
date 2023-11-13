package com.milistock.develop.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity // Database Table과 맵핑하는 객체.
@Table(name="board") // Database 테이블 이름 user3 와 User라는 객체가 맵핑.
@NoArgsConstructor // 기본생성자가 필요하다.
@Setter
@Getter
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    private String title;
    private String content;
    private String name;
    private Long memberId;

    @Column(columnDefinition = "TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime date;

    private boolean answered;

    @Override
    public String toString() {
        return "Board{" +
                "boardId=" + boardId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", name='" + name + '\'' +
                ", memberId='" + memberId + '\'' +
                ", date='" + date + '\'' +
                ", answered='" + answered +
                '}';
    }
    // Constructors, getters, setters
}
