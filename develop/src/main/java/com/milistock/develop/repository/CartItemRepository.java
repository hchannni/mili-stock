package com.milistock.develop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.milistock.develop.domain.Cart;
import com.milistock.develop.domain.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    boolean existsByCartAndProduct_ProductNumber(Cart cart, int productNumber);
}
