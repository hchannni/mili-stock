package com.milistock.develop.service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.milistock.develop.repository.HeartRepository;
import com.milistock.develop.repository.ProductRepository;
import com.milistock.develop.utils.RegexFunctions;
import com.milistock.develop.code.ErrorCode;
import com.milistock.develop.domain.Heart;
import com.milistock.develop.domain.Member;
import com.milistock.develop.domain.Product;
import com.milistock.develop.exception.BusinessExceptionHandler;

@Service
public class HeartService {
    private final HeartRepository heartRepository;
    private final ProductRepository productRepository;

    @Autowired
    private MemberService memberService;

    @Autowired 
    private ProductService productService;

    public HeartService(HeartRepository heartRepository, ProductRepository productRepository) {
        this.heartRepository = heartRepository;
        this.productRepository = productRepository;
    }

    // not done -> product 예외방지 필요
    public Heart saveHeart(Principal principal, int productNumber) {
        Long memberId = RegexFunctions.extractMemberId(principal);

        // Check if productNumber and member_id exists
        Member member = memberService.findByMemberId(memberId);
        Product product = productService.getProductById(productNumber);

        // 하트 중복 체크
        if (heartRepository.existsByMemberMemberIdAndProductProductNumber(memberId,productNumber)){
            throw new BusinessExceptionHandler("하트가 이미 돼 있습니다", ErrorCode.CONFLICT); 
        }

        Heart heart = new Heart();
        heart.setMember(member);
        heart.setProduct(product);

        return heartRepository.save(heart);
    }

    // done
    public List<Heart> getHeart(Principal principal){
        Long memberId = RegexFunctions.extractMemberId(principal);
        
        return heartRepository.findAllByMemberMemberId(memberId);
    }

    // done
    public List<Heart> getAllHearts() {
        return heartRepository.findAll();
    }

    // done
    public Optional<Heart> getHeartById(int heartId) {
        Optional<Heart> heart = heartRepository.findById(heartId);
        if(heart.isPresent()){
            return heart;
        } else{
            throw new BusinessExceptionHandler("해당 id의 하트가 존재 안 합니다", ErrorCode.NOT_FOUND_ERROR);
        }
    }

    // done
    public List<Heart> getAllHeartsByMemberId(Long memberId) {        
        if(memberService.isMemberIdExists(memberId)){ // 유저가 존재하는지 확인
            return heartRepository.findAllByMemberMemberId(memberId); // 그 멤버의 모든 하트 찾아주기
        } else{
            throw new IllegalArgumentException("getAllHeartsByMemberId()에서 처리 못한 에러입니다");
        }
    }

    public void deleteHeart(Principal principal, int productNumber){
        Long memberId = RegexFunctions.extractMemberId(principal);

        Optional<Heart> heart = heartRepository.findByMemberMemberIdAndProductProductNumber(memberId,productNumber);

        if (heart.isPresent()){
            heartRepository.delete(heart.get());
        } else {
            throw new BusinessExceptionHandler("해당 상품의 하트가 존재 안 합니다", ErrorCode.NOT_FOUND_ERROR); 
        }

    }

    // done
    public void deleteHeartById(int heartId) {
        Optional<Heart> heart = heartRepository.findById(heartId);
        if(heart.isPresent()){
            heartRepository.deleteById(heartId);
        } else{
            throw new BusinessExceptionHandler("해당 id의 하트가 존재 안 합니다", ErrorCode.NOT_FOUND_ERROR);
        }        
    }

    // Extra (later)
    public Long getHeartCountForProduct(Product product) {
        return heartRepository.countByProduct(product);
    }
}
