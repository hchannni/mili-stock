package com.milistock.develop.dto;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {

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
    private int productHeartCount;
    private Boolean isHeart;
    private LocalDateTime productTimeAdde;
}
