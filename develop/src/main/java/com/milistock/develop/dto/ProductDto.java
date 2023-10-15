package com.milistock.develop.dto;

import javax.validation.constraints.NotBlank;

import org.modelmapper.ModelMapper;

import com.milistock.develop.domain.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String productTitle;
    private int productPrice;
    private int productStock;
    private String productImageUrl;
    private String category;
    private Boolean isDiscountedProduct;
    private Boolean isNewProduct;
    private Boolean isPopularProduct;
    private int productDiscountPrice;

    // ModelMapper: 서로 다른 클래스의 값을! 필드의 이름과 자료형이 같을 때! getter, setter 를 통해 값을 복사해서 객체를 반환함
    private static ModelMapper modelMapper = new ModelMapper();

    // dto -> Product
    public Product createItem(){
        return modelMapper.map(this, Product.class);
    }
}
