package com.milistock.develop.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


// (1) 순위를 위한 product의 좋아요 개수 계산 (product_id)
// (2) all product page에서 해당 유저가 좋아요 했는지 표시 (member_i, product_id)

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="heart")
@Table(name = "heart")
public class Heart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "heart_id")
    private int heartId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "product_number")
    private Product product;
}
