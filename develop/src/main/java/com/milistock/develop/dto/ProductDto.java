package com.milistock.develop.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

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

    @Min(value = 1, message = "가격은 최소 1 이상이어야 합니다.")
    private int productPrice;

    @Min(value = 1, message = "재고는 최소 1 이상이어야 합니다.")
    private int productStock;

    private MultipartFile image;
    private String category;
    private Boolean isDiscountedProduct;
    private Boolean isNewProduct;
    private Boolean isPopularProduct;
    private int productDiscountPrice;

    // ModelMapper: 서로 다른 클래스의 값을! 필드의 이름과 자료형이 같을 때! getter, setter 를 통해 값을 복사해서 객체를 반환함
    private static ModelMapper modelMapper = new ModelMapper();

    // dto -> Product
    public Product createItem(String product_image_url){
        Product product = modelMapper.map(this, Product.class);

        // Set the productAddedTime to the current timestamp
        product.setProductTimeAdded(LocalDateTime.now());
        product.setProduct_image_url(product_image_url);

        return product;
    }
}
