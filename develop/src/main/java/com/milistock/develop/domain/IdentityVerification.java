package com.milistock.develop.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="identity_verification")
@NoArgsConstructor
@Setter
@Getter
public class IdentityVerification {
    @Id
    @Column(name="user_number")
    private Long userNumber;

    @Column(length = 30)
    private String name; // 이름

    @Column(length = 255, unique = true)
    private String serviceNumber; //군번

    @Column(length = 50)
    private String job; // 직군(ex 현역, 군무원, 직업군인)

    @Column(length = 50)
    private String affiliation; // 소속(ex 육군, 해군, 공군)



    @Override
    public String toString() {
        return "IdentityVerification{" +
                "userNumber=" + userNumber +
                ", name='" + name + '\'' +
                ", serviceNumber='" + serviceNumber + '\'' +
                ", job='" + job + '\'' +
                ", affiliation='" + affiliation + '\'' +
                '}';
    }
}