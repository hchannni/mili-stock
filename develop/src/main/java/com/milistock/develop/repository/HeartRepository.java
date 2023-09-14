package com.milistock.develop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.milistock.develop.domain.Heart;

@Repository
public interface HeartRepository extends JpaRepository<Heart, Integer> {
    List<Heart> findAllByMember_Id(int userId);
}
