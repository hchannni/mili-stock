package com.milistock.develop.controller.products;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.milistock.develop.domain.Product;
import com.milistock.develop.dto.ProductDto;
import com.milistock.develop.repository.ProductRepository;
import com.milistock.develop.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    // @Autowired
    // public ProductController(ProductRepository productRepository) {
    //     this.productRepository = productRepository;
    // }

    // 상품 등록 post
    @PostMapping
    public Product createProduct(@RequestBody ProductDto productDto) {
        Product product = productDto.createItem();
        return productRepository.save(product);
    }

    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // @GetMapping("/popular")
    // public List<Product> getPopularProducts() {
    //     return productRepository.findByIsPopularProduct(true);
    // }

    // @GetMapping("/discounted")
    // public List<Product> getDiscountedProducts() {
    //     return productRepository.findByIsDiscountedProduct(true);
    // }

    // @GetMapping("/new")
    // public List<Product> getNewProducts() {
    //     return productRepository.findByIsNewProduct(true);
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

    @DeleteMapping("/{productNumber}")
    public ResponseEntity<String> deleteProduct(@PathVariable int productNumber) {
        Product product = productService.getProductById(productNumber);

        productRepository.delete(product);
        
        String successResponse = "상품id= " + productNumber + "가 삭제되었습니다";
        return new ResponseEntity<String>(successResponse, HttpStatus.OK);
    }


}
