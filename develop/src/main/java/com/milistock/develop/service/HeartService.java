package com.milistock.develop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.milistock.develop.repository.HeartRepository;
import com.milistock.develop.repository.MemberRepository;
import com.milistock.develop.repository.ProductRepository;
import com.milistock.develop.domain.Heart;
import com.milistock.develop.domain.Member;
import com.milistock.develop.domain.Product;
import com.milistock.develop.dto.MakeHeartDto;

@Service
public class HeartService {
    private final HeartRepository heartRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public HeartService(HeartRepository heartRepository, ProductRepository productRepository, MemberRepository memberRepository) {
        this.heartRepository = heartRepository;
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
    }

    public Heart saveHeart(MakeHeartDto heartDto) {
        // Check if productNumber and member_id exists
        Member member = memberRepository.findById(heartDto.getMemberId()).orElse(null);
        Product product = productRepository.findById(heartDto.getProductNumber()).orElse(null);

        Heart heart = new Heart();
        heart.setMember(member);
        heart.setProduct(product);

        return heartRepository.save(heart);
    }

    public List<Heart> getAllHearts() {
        return heartRepository.findAll();
    }

    public Optional<Heart> getHeartById(int heartId) {
        return heartRepository.findById(heartId);
    }

    public List<Heart> getAllHeartsByUserId(Long userId) {
        return heartRepository.findAllByMemberMemberId(userId);
    }

    public Long getHeartCountForProduct(Product product) {
        return heartRepository.countByProduct(product);
    }

    public void deleteHeart(int heartId) {
        heartRepository.deleteById(heartId);
    }
}
