package com.milistock.develop.controller.products;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.milistock.develop.domain.Product;
import com.milistock.develop.dto.ProductDto;
import com.milistock.develop.repository.CartItemRepository;
import com.milistock.develop.repository.HeartRepository;
import com.milistock.develop.repository.ProductRepository;
import com.milistock.develop.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private HeartRepository heartRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/search")
    public List<Product> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        List<Product> results;


        if (category != null) {
            if (keyword != null) {
                // 카테고리와 키워드 모두가 입력된 경우
                List<Product> categoryResults = productRepository.findByCategory(category);

                String[] keywords = keyword.split(" ");
                Set<Product> searchResults = new HashSet<>();

                for (String searchWord : keywords) {
                    List<Product> resultsForKeyword = categoryResults.stream()
                            .filter(product -> product.getProductTitle().contains(searchWord))
                            .collect(Collectors.toList());
                    searchResults.addAll(resultsForKeyword);
                }

                results = new ArrayList<>(searchResults);
            } else {
                // 카테고리만 입력된 경우
                results = productRepository.findByCategory(category);
            }
        } else {
            if (keyword != null) {
                // 키워드만 입력된 경우
                String[] keywords = keyword.split(" ");
                Set<Product> searchResults = new HashSet<>();

                for (String searchWord : keywords) {
                    List<Product> resultsForKeyword = productRepository.findByProductTitleContaining(searchWord);
                    searchResults.addAll(resultsForKeyword);
                }

                results = new ArrayList<>(searchResults);
            } else {
                // 아무 입력도 없는 경우
                results = productRepository.findAll();
            }
        }

        return results;
    }

    // 상품 등록 post
    @PostMapping
    public ResponseEntity<String> createProduct(@Valid @RequestBody ProductDto productDto) {

        try {
            productService.createProduct(productDto); // 상품 중복 확인 후 추가
        } catch (Exception e) {
            // String errorResponse = "상품 추가 중 에러가 발생했습니다";
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String successResponse = "상품" + productDto.getProductTitle() + "가 추가되었습니다";
        return new ResponseEntity<String>(successResponse, HttpStatus.OK);
    }

    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // @GetMapping("/popular")
    // public List<Product> getPopularProducts() {
    // return productRepository.findByIsPopularProduct(true);
    // }

    // @GetMapping("/discounted")
    // public List<Product> getDiscountedProducts() {
    // return productRepository.findByIsDiscountedProduct(true);
    // }

    // @GetMapping("/new")
    // public List<Product> getNewProducts() {
    // return productRepository.findByIsNewProduct(true);
    // }

    @GetMapping("/category/{category}")
    public List<Product> getCategory(@PathVariable String category) {
        return productRepository.findByCategory(category);
    }

    @GetMapping("/{productNumber}")
    public ResponseEntity<Product> getProductById(@PathVariable int productNumber) {
        Optional<Product> product = productRepository.findById(productNumber);

        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Sends JSON of the WHOLE updated product (not just the changed part)
    @PutMapping("/{productNumber}")
    public ResponseEntity<Product> updateProduct(@PathVariable int productNumber, @RequestBody Product updatedProduct) {
        Optional<Product> product = productRepository.findById(productNumber);

        if (product.isPresent()) {
            updatedProduct.setProductNumber(productNumber);
            productRepository.save(updatedProduct);
            return ResponseEntity.ok(updatedProduct);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Transactional
    @DeleteMapping("/{productNumber}")
    public ResponseEntity<String> deleteProduct(@PathVariable int productNumber) {
        System.out.println("before getProductById");
        Product product = productService.getProductById(productNumber); // exception 처리 함

        try{            
            System.out.println("before existsByProduct");
            if(cartItemRepository.existsByProduct(product)){
                cartItemRepository.deleteByProduct(product);
            }
            System.out.println("before deleting product from heartRepo");
            if(heartRepository.existsByProduct(product)){
                heartRepository.deleteByProduct(product);
            }
            System.out.println("before deleting product from productRepo");
            productRepository.delete(product);

            String successResponse = "상품id= " + productNumber + "가 삭제되었습니다";
            return new ResponseEntity<String>(successResponse, HttpStatus.OK);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(Pageable pageable) {
        Page<Product> productPage = productService.getAllProducts(pageable);
        List<Product> products = productPage.getContent();
        return ResponseEntity.ok(products);
    }

}
