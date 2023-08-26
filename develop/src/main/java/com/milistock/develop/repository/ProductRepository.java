package com.milistock.develop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.milistock.develop.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
}
