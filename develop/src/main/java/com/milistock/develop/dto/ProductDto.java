package com.milistock.develop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private int productNumber;
    private String productTitle;
    private int productPrice;
    private int productStock;
    private String productImageUrl;
    private Boolean productDiscount;
    private int productDiscountPrice;
    private String productTimeAdded;
}
