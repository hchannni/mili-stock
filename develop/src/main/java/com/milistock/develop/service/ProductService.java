package com.milistock.develop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


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

    public int createProduct(ProductDto productDto, String uploadedUrl){
        // 중복 확인
        if (productRepository.existsByProductTitle(productDto.getProductTitle())) {
            throw new BusinessExceptionHandler("같은 이름의 상품이 이미 추가 돼 있습니다", ErrorCode.CONFLICT);
        }

        // 상품 추가
        Product product = productDto.createItem(uploadedUrl);
        productRepository.save(product);

        return product.getProductNumber();
    }
    
    // // 새 상품 생성
    // @Transactional
    // public int createProduct(ProductDto productDto){
    //     // 중복 확인
    //     if(productRepository.existsByProductTitle( productDto.getProductTitle() )){
    //         throw new BusinessExceptionHandler("같은 이름의 상품이 이미 추가 돼 있습니다", ErrorCode.CONFLICT); 
    //     }

    //     // 상품 추가
    //     Product product = productDto.createItem();
    //     productRepository.save(product);

    //     return product.getProductNumber();
    // }

    @Transactional(readOnly = true)
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByProductTitleContaining(keyword);
    }

    @Transactional(readOnly = true)
    public List<Product> searchProductsByMultipleKeywords(String query) {
        String[] keywords = query.split(" ");
        Set<Product> searchResults = new HashSet<>();
    
        for (String keyword : keywords) {
            List<Product> resultsForKeyword = productRepository.findByProductTitleContaining(keyword);
            searchResults.addAll(resultsForKeyword);
        }
    
        return new ArrayList<>(searchResults);
    }
    
    //하트 개수 증가
    @Transactional
    public Product productHeartPlus(int productNumber){
        Product existingProduct = productRepository.findByproductNumber(productNumber).orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        existingProduct.setProductHeartCount(existingProduct.getProductHeartCount()+1);
        
        Product saveProduct = productRepository.save(existingProduct);
        return saveProduct;
    }

    //하트 개수 감소
    @Transactional
    public Product productHeartMinus(int productNumber){
        Product existingProduct = productRepository.findByproductNumber(productNumber).orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));
 
        existingProduct.setProductHeartCount(existingProduct.getProductHeartCount()-1);
        
        Product saveProduct = productRepository.save(existingProduct);
        return saveProduct;
    }
    
    @Transactional(readOnly = true)
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
