package com.milistock.develop.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="product")
@Table(name="product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productNumber;
    private String productTitle;
    private int productPrice;
    private int productStock;
    private String productImageUrl;
    private String category;
    private Boolean isDiscountedProduct;
    private Boolean isNewProduct;
    private Boolean isPopularProduct;
    private int productDiscountPrice;
    //처음 하트개수 0개로 초기화
    @Builder.Default
    private int productHeartCount=0;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime productTimeAdded;
}