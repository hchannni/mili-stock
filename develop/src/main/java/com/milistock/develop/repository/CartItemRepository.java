package com.milistock.develop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.milistock.develop.domain.Cart;
import com.milistock.develop.domain.CartItem;
import com.milistock.develop.domain.Product;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    boolean existsByCartAndProduct_ProductNumber(Cart cart, int productNumber);
    boolean existsByProduct(Product product);
    void deleteByProduct(Product product);
}
