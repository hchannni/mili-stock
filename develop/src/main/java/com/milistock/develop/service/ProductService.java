package com.milistock.develop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.milistock.develop.code.ErrorCode;
import com.milistock.develop.domain.Product;
import com.milistock.develop.dto.ProductDto;
import com.milistock.develop.exception.BusinessExceptionHandler;
import com.milistock.develop.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Constructor or method to initialize products

    // 새 상품 생성
    @Transactional
    public int createProduct(ProductDto productDto){
        // 중복 확인
        if(productRepository.existsByProductTitle( productDto.getProductTitle() )){
            throw new BusinessExceptionHandler("같은 이름의 상품이 이미 추가 돼 있습니다", ErrorCode.CONFLICT);
        }

        // 상품 추가
        Product product = productDto.createItem();
        productRepository.save(product);

        return product.getProductNumber();
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getProductById(int productId) {
        Optional<Product> product = productRepository.findById(productId); // 현재 로그인한 회원의 장바구니 엔티티 조회
            
        // 해당 id의 회원이 없으면, 에러
        if (product.isPresent()) {
            return product.get();
        } else {
            throw new BusinessExceptionHandler("상품이 존재 안 합니다", ErrorCode.NOT_FOUND_ERROR);
        }
        
    }

    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

}
