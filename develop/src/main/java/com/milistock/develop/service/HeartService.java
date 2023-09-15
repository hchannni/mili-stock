package com.milistock.develop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.milistock.develop.repository.HeartRepository;
import com.milistock.develop.domain.Heart;
import com.milistock.develop.domain.Product;

@Service
public class HeartService {
    private final HeartRepository heartRepository;

    @Autowired
    public HeartService(HeartRepository heartRepository) {
        this.heartRepository = heartRepository;
    }

    public Heart saveHeart(Heart heart) {
        return heartRepository.save(heart);
    }

    public List<Heart> getAllHearts() {
        return heartRepository.findAll();
    }

    public Optional<Heart> getHeartById(int heartId) {
        return heartRepository.findById(heartId);
    }

    public List<Heart> getAllHeartsByUserId(int userId) {
        return heartRepository.findAllByMember_Id(userId);
    }

    public Long getHeartCountForProduct(Product product) {
        return heartRepository.countByProduct(product);
    }

    public void deleteHeart(int heartId) {
        heartRepository.deleteById(heartId);
    }
}
