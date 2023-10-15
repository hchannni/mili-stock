package com.milistock.develop.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

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

    @Column(nullable = false, length = 50)
    private String productTitle;

    @NotNull
    private int productPrice;

    @NotNull
    private int productStock;

    @NotNull
    private String productImageUrl;

    @NotNull
    private String category;

    private Boolean isDiscountedProduct;
    private Boolean isNewProduct;
    private Boolean isPopularProduct;
    private int productDiscountPrice;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime productTimeAdded;
}