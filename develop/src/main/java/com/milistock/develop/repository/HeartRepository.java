package com.milistock.develop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.milistock.develop.domain.Heart;
import com.milistock.develop.domain.Product;

@Repository
public interface HeartRepository extends JpaRepository<Heart, Integer> {
    List<Heart> findAllByMemberMemberId(Long memberId);
    Long countByProduct(Product product); // number of hearts
    boolean existsByMemberIdAndProductNumber(Long memberId, int productNumber);
}
