package com.milistock.develop.controller.products;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.milistock.develop.domain.Product;
import com.milistock.develop.dto.ProductDto;
import com.milistock.develop.repository.ProductRepository;
import com.milistock.develop.service.ProductService;
import com.milistock.develop.service.S3UploadService;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private S3UploadService s3UploadService;

    // @Autowired
    // public ProductController(ProductRepository productRepository) {
    // this.productRepository = productRepository;
    // }

    // // 상품 등록 post
    // @PostMapping
    // public ResponseEntity<String> createProduct(@Valid @RequestBody ProductDto
    // productDto) {

    // try {
    // productService.createProduct(productDto); // 상품 중복 확인 후 추가
    // }
    // catch (Exception e) {
    // // String errorResponse = "상품 추가 중 에러가 발생했습니다";
    // return new ResponseEntity<>(e.getMessage(),
    // HttpStatus.INTERNAL_SERVER_ERROR);
    // }

    // String successResponse = "상품" + productDto.getProductTitle() + "가 추가되었습니다";
    // return new ResponseEntity<String>(successResponse, HttpStatus.OK);
    // }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult bindingResult)
            throws IOException {
        
        // System.out.println(productDto);
        
        if (bindingResult.hasErrors()) {

            // 유저가 뭘 잘못 입력했는지 출력
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                // Get the name of the field that has an error
                String fieldName = fieldError.getField();
                // Get the error message for this field
                String errorMessage = fieldError.getDefaultMessage();                
                System.out.println("Validation error for field '" + fieldName + "': " + errorMessage);
            }

            return "product-form";
        }

        MultipartFile image = productDto.getImage();
        String uploadedUrl = null;

        // image 없어도 됨
        if (image != null){            
            uploadedUrl = s3UploadService.upload(image);
        }

        try {
            productService.createProduct(productDto, uploadedUrl); // 상품 중복 확인 후 추가
        } catch (Exception e) {
            return e.getMessage();
        }

        return "success-page";

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

    @DeleteMapping("/{productNumber}")
    public ResponseEntity<String> deleteProduct(@PathVariable int productNumber) {
        Product product = productService.getProductById(productNumber);

        productRepository.delete(product);

        String successResponse = "상품id= " + productNumber + "가 삭제되었습니다";
        return new ResponseEntity<String>(successResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(Pageable pageable) {
        Page<Product> productPage = productService.getAllProducts(pageable);
        List<Product> products = productPage.getContent();
        return ResponseEntity.ok(products);
    }

}
