package com.milistock.develop.controller.products;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.milistock.develop.domain.Heart;
import com.milistock.develop.domain.Product;
import com.milistock.develop.service.HeartService;
import com.milistock.develop.service.ProductService;

// (1) 순위를 위한 product의 좋아요 개수 계산 (product_id) -> get number of all hearts for product_id
// (2) all product page에서 해당 유저가 좋아요 했는지 표시 (member_id, product_id) -> get all hearts for member_id

@RestController
@RequestMapping("/hearts")
public class HeartController {

    private final HeartService heartService;
    private final ProductService productService;

    public HeartController(HeartService heartService, ProductService productService) {
        this.heartService = heartService;
        this.productService = productService;
    }

    @PostMapping("/product/{productNumber}")
    public Heart saveHeart(Principal principal, @PathVariable int productNumber) {
        return heartService.saveHeart(principal, productNumber);
    }

    // Get all hearts of current user
    @GetMapping
    public List<Heart> getHeart(Principal principal) {
        return heartService.getHeart(principal);
    }

    @GetMapping("/all")
    public List<Heart> getAllHearts() {
        return heartService.getAllHearts();
    }
    
    
    @GetMapping("/{heartId}")
    public Optional<Heart> getHeartById(@PathVariable int heartId) {
        return heartService.getHeartById(heartId);
    }

    // (2) all product page에서 해당 유저가 좋아요 했는지 표시 (member_id, product_id) -> get all hearts for member_id
    @GetMapping("/user/{userId}")
    public List<Heart> getAllHeartsByMemberId(@PathVariable Long userId) {
        return heartService.getAllHeartsByMemberId(userId);
    }

    // (1) 순위를 위한 product의 좋아요 개수 계산 (product_id) -> get number of all hearts for product_id
    @GetMapping("/product/{productNumber}/numHearts")
    public ResponseEntity<Long> getProductHeartCount(@PathVariable int productNumber){
        Product product = productService.getProductById(productNumber);
        if (product==null){
            return ResponseEntity.notFound().build();
        }
        
        Long heartCount = heartService.getHeartCountForProduct(product);
        return ResponseEntity.ok(heartCount);
    }
    
    // productId로 해당 유저의 좋아요 취소하기 (main page에 하트 취소 누를때)
    @DeleteMapping("/product/{productNumber}")
    public void deleteHeart(Principal principal, @PathVariable int productNumber) {
        heartService.deleteHeart(principal, productNumber);
    }

    // heartId로 삭제하기 (카트 페이지에서 하트 취소 누를때)
    @DeleteMapping("/{heartId}")
    public void deleteHeart(@PathVariable int heartId) {
        heartService.deleteHeartById(heartId);
    }

}
