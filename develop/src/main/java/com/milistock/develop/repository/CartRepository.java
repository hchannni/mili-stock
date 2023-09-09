package com.milistock.develop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.milistock.develop.domain.Cart;
import com.milistock.develop.domain.Member;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    Cart findByUser(Member user);
    Cart findByCartId(int id);
}

// @Repository
// public interface CartRepository extends JpaRepository<Cart, Integer> {
//     // You can add custom query methods here if needed
//     Optional<Cart> findByUserId(String userId);
// }