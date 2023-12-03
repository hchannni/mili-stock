package com.milistock.develop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.milistock.develop.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByIsDiscountedProduct(boolean isDiscountedProduct);
    List<Product> findByIsNewProduct(boolean isNewProduct);
    List<Product> findByIsPopularProduct(boolean isPopularProduct);
    List<Product> findByCategory(String category);
    boolean existsByProductTitle(String productTitle);
    List<Product> findByProductTitleContaining(String keyword);
    List<Product> findByCategoryContaining(String category);
}
