package com.milistock.develop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.milistock.develop.code.ErrorCode;
import com.milistock.develop.domain.Product;
import com.milistock.develop.exception.BusinessExceptionHandler;
import com.milistock.develop.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Constructor or method to initialize products

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(int productId) {
        Optional<Product> product = productRepository.findById(productId); // 현재 로그인한 회원의 장바구니 엔티티 조회
            
        // 해당 id의 회원이 없으면, 에러
        if (product.isPresent()) {
            return product.get();            
        } else {
            throw new BusinessExceptionHandler("회원이 존재 안 합니다", ErrorCode.NOT_FOUND_ERROR); 
        }
        
    }

}
