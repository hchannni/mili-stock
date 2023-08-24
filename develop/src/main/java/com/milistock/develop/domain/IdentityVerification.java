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
    private String name;

    @Column(length = 50)
    private String serviceNumber;

    @Column(length = 50)
    private String job;

    @Column(length = 50)
    private String affiliation;



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