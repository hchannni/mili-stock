package com.milistock.develop.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.milistock.develop.code.ErrorCode;
import com.milistock.develop.domain.Product;
import com.milistock.develop.dto.ProductDto;
import com.milistock.develop.dto.ProductResponseDto;
import com.milistock.develop.exception.BusinessExceptionHandler;
import com.milistock.develop.repository.HeartRepository;
import com.milistock.develop.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    private HeartRepository heartRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Constructor or method to initialize products


    // 새 상품 생성
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


    //전체 상품 찾기
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    //상품 업데이트(관리자)
    @Transactional
    public Product updateProduct(int productNumber,String productTitle,int productPrice,
                                int productStock,String productImageUrl,String category,
                                Boolean isDiscountedProduct,int productDiscountPrice){
        Product existingProduct = productRepository.findByproductNumber(productNumber).orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        existingProduct.setProductTitle(productTitle);
        existingProduct.setProductPrice(productPrice);
        existingProduct.setProductStock(productStock);
        existingProduct.setProductImageUrl(productImageUrl);
        existingProduct.setCategory(category);
        existingProduct.setIsDiscountedProduct(isDiscountedProduct);
        existingProduct.setProductDiscountPrice(productDiscountPrice);
        
        Product saveProduct = productRepository.save(existingProduct);
        return saveProduct;
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
    

    //신상품 업데이트
    @Transactional
    public void updateProductStatus() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        List<Product> products= productRepository.findByProductTimeAddedBeforeAndIsNewProductIsTrue(oneWeekAgo);
        
        for (Product product : products) {
            product.setIsNewProduct(false);
        }

        productRepository.saveAll(products);

        List<Product> errorProducts= productRepository.findByProductTimeAddedAfterAndIsNewProductIsFalse(oneWeekAgo);

        for (Product product : errorProducts) {
            product.setIsNewProduct(true);
        }

        productRepository.saveAll(errorProducts);
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

    //해당 유저가 상품에 하트를 눌렀는지 안눌렀는지 확인
    public boolean hasUserLikedProduct(Long memberId, Product product) {
        int productNumber = product.getProductNumber();
        return heartRepository.existsByMemberMemberIdAndProductProductNumber(memberId, productNumber);
    }

    //Product -> ProductResponseDto 변경 메소드
    public ProductResponseDto convertToDto(Product product) {
        return ProductResponseDto.builder()
                .productNumber(product.getProductNumber())
                .productTitle(product.getProductTitle())
                .productPrice(product.getProductPrice())
                .productStock(product.getProductStock())
                .productImageUrl(product.getProductImageUrl())
                .category(product.getCategory())
                .isDiscountedProduct(product.getIsDiscountedProduct())
                .isNewProduct(product.getIsNewProduct())
                .isPopularProduct(product.getIsPopularProduct())
                .productDiscountPrice(product.getProductDiscountPrice())
                .productHeartCount(product.getProductHeartCount())
                .productTimeAdde(product.getProductTimeAdded())
                .isHeart(false)
                .build();
    }
    
}
