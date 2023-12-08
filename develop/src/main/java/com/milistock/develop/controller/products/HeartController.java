package com.milistock.develop.controller.products;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        productService.productHeartPlus(productNumber);
        return heartService.saveHeart(principal, productNumber);
    }

    // Get all hearts of current user
    @GetMapping
    public List<Heart> getHeart(Principal principal) {
        return heartService.getHeart(principal);
    }

    @GetMapping("/products")
    public ResponseEntity<Page<Product>> getProducts(
            @RequestParam(required = false) String sortBy,
            @PageableDefault(size = 10) Pageable pageable,
            Principal principal) {

        Page<Product> results;
        List<Product> productList = new ArrayList<>();
        List<Heart> hearts = heartService.getHeart(principal);

        for (Heart heart : hearts) {
            productList.add(heart.getProduct());
        }

        List<Product> sortedList = getSort(productList, sortBy);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedList.size());
        results = new PageImpl<>(sortedList.subList(start, end), pageable, sortedList.size());

        return ResponseEntity.ok(results);

    }

    @GetMapping("/all")
    public List<Heart> getAllHearts() {
        return heartService.getAllHearts();
    }

    @GetMapping("/{heartId}")
    public Optional<Heart> getHeartById(@PathVariable int heartId) {
        return heartService.getHeartById(heartId);
    }

    // (2) all product page에서 해당 유저가 좋아요 했는지 표시 (member_id, product_id) -> get all
    // hearts for member_id
    @GetMapping("/user/{userId}")
    public List<Heart> getAllHeartsByMemberId(@PathVariable Long userId) {
        return heartService.getAllHeartsByMemberId(userId);
    }

    // (1) 순위를 위한 product의 좋아요 개수 계산 (product_id) -> get number of all hearts for
    // product_id
    @GetMapping("/product/{productNumber}/numHearts")
    public ResponseEntity<Long> getProductHeartCount(@PathVariable int productNumber) {
        Product product = productService.getProductById(productNumber);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        Long heartCount = heartService.getHeartCountForProduct(product);
        return ResponseEntity.ok(heartCount);
    }

    // productId로 해당 유저의 좋아요 취소하기 (main page에 하트 취소 누를때)
    @DeleteMapping("/product/{productNumber}")
    public void deleteHeart(Principal principal, @PathVariable int productNumber) {
        productService.productHeartMinus(productNumber);
        heartService.deleteHeart(principal, productNumber);
    }

    // heartId로 삭제하기 (카트 페이지에서 하트 취소 누를때)
    @DeleteMapping("/{heartId}")
    public void deleteHeart(@PathVariable int heartId) {
        // 하트아이디로 product id 구하는 방법 알려주세요.
        Optional<Heart> heart = heartService.getHeartById(heartId);
        int productNumber = heart.get().getProduct().getProductNumber();
        productService.productHeartMinus(productNumber);
        heartService.deleteHeartById(heartId);
    }

    private List<Product> getSort(List<Product> productList, String sortBy) {
        if (sortBy == null) {
            // 기본 정렬 조건 추가
            productList.sort(Comparator.comparing(Product::getProductHeartCount).reversed());
        } else {
            // 사용자가 전달한 정렬 조건에 따라 설정
            switch (sortBy) {
                case "stockHighToLow": // 재고 많은 순
                    productList.sort(Comparator.comparing(Product::getProductStock).reversed());
                    break;
                case "stockLowToHigh": // 재고 적은 순
                    productList.sort(Comparator.comparing(Product::getProductStock));
                    break;
                case "priceHighToLow": // 가격 높은 순
                    productList.sort(Comparator.comparing(Product::getProductPrice).reversed());
                    break;
                case "priceLowToHigh": // 가격 낮은 순
                    productList.sort(Comparator.comparing(Product::getProductPrice));
                    break;
                case "newer": // 신상품 순
                    productList.sort(Comparator.comparing(Product::getProductTimeAdded).reversed());
                    break;
                case "popular":
                    // 여기에 인기많은 순 정렬 조건 추가
                    productList.sort(Comparator.comparing(Product::getProductHeartCount).reversed());
                    break;
                default:
                productList.sort(Comparator.comparing(Product::getProductHeartCount).reversed());
            }
        }
        return productList;
    }

}
