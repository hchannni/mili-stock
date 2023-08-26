package com.milistock.develop.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
//import java.util.HashSet;
//import java.util.Set;

@Entity // Database Table과 맵핑하는 객체.
@Table(name="member") // Database 테이블 이름 user3 와 User라는 객체가 맵핑.
@NoArgsConstructor // 기본생성자가 필요하다.
@Setter
@Getter
public class Member {
    @Id // 이 필드가 Table의 PK.
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) // memberId는 자동으로 생성되도록 한다. 1,2,3,4
    private Long memberId;

    @Column(length = 255, unique = true)
    private String serviceNumber; // 군번

    @Column(length = 50)
    private String name;

    @Column(length = 255, unique = true)
    private String userId;

    @JsonIgnore
    @Column(length = 500)
    private String password;

    @Column(length = 50, nullable = false)
    private String job; // (병사,간부,군무원)

    @Column(length = 50, nullable = false)
    private String affiliation; // (육군,해군,공군)

    @Column(length = 50, nullable = false)
    private String militaryRank; // (일병,상병,하사,중사.대위,소령...)

    @Column(length = 50, nullable = false)
    private String birth; // (생년월일)

    @Column(length = 50, nullable = false)
    private String phoneNumber; // (전화번호)

    @Column(length = 255, nullable = false)
    private String email; // (이메일)

    @Column(length = 50)
    private int child; // (자녀수)  

    @Column(length = 10, nullable = false)
    private String gender; //(성별)

    @Column(length = 255)
    private String appointment; // (임관일자)  

    @Column(length = 50)
    private String discharge; // (전역일자)  



   // @CreationTimestamp // 현재시간이 저장될 때 자동으로 생성.
    //private LocalDateTime regdate;

    @Override
    public String toString() {
        return "User{" +
                "memberId=" + memberId +
                ", serviceNumber='" + serviceNumber + '\'' +
                ", name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                ", job='" + job + '\'' +
                ", affiliation='" + affiliation + '\'' +
                ", militaryRank='" + militaryRank + '\'' +
                ", birth='" + birth + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", child='" + child + '\'' +
                ", gender='" + gender + '\'' +
                ", appointment='" + appointment + '\'' +
                ", discharge=" + discharge +
                '}';
    }
    
/*    public void addRole(Role role) {
        roles.add(role);
    }
*/
}
