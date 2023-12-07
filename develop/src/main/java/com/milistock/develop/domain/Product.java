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
    private int productNumber; //x
    private String productTitle; //0
    private int productPrice; //0
    private int productStock; //0
    private String productImageUrl; //0
    private String category; //0
    private Boolean isDiscountedProduct; //0
    private Boolean isNewProduct;
    private Boolean isPopularProduct;
    private int productDiscountPrice; //0

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime productTimeAdded;
}