package com.milistock.develop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.milistock.develop.domain.Cart;
import com.milistock.develop.domain.Member;
// import com.milistock.develop.domain.Product;

// 수정3: findByMember가 맞는지, findByMemberId는 왜 안되는지 잘 이해 안감
@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    Cart findByMember(Member user);
    Cart findByCartId(int id);
    // Cart findByProduct(Product p);
}

// @Repository
// public interface CartRepository extends JpaRepository<Cart, Integer> {
//     // You can add custom query methods here if needed
//     Optional<Cart> findByUserId(String userId);
// }