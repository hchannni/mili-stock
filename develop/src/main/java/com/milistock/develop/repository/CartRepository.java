package com.milistock.develop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.milistock.develop.domain.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    Cart findByMemberId(int id);
    Cart findByCartId(int id);
}

// @Repository
// public interface CartRepository extends JpaRepository<Cart, Integer> {
//     // You can add custom query methods here if needed
//     Optional<Cart> findByUserId(String userId);
// }