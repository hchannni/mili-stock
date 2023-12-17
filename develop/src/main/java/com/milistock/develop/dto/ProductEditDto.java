package com.milistock.develop.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEditDto {
    
    @NotNull
    private int productNumber;
    @NotBlank
    private String productTitle;
    @NotNull
    private int productPrice;
    @NotNull
    private int productStock;
    @NotBlank
    private String productImageUrl;
    @NotBlank
    private String category;
    @NotNull
    private Boolean isDiscountedProduct;
    @NotNull
    private int productDiscountPrice;

}
