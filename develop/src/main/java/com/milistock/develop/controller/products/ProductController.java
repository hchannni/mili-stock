package com.milistock.develop.controller.products;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.milistock.develop.domain.Product;
import com.milistock.develop.dto.ProductDto;
import com.milistock.develop.dto.ProductEditDto;
import com.milistock.develop.dto.ProductResponseDto;
import com.milistock.develop.repository.CartItemRepository;
import com.milistock.develop.repository.HeartRepository;
import com.milistock.develop.repository.ProductRepository;
import com.milistock.develop.response.ValidationResponse;
import com.milistock.develop.security.jwt.util.IfLogin;
import com.milistock.develop.security.jwt.util.LoginUserDto;
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
    private CartItemRepository cartItemRepository;

    @Autowired
    private S3UploadService s3UploadService;

    @Autowired
    private HeartRepository heartRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 상품 검색 메소드
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDto>> searchProducts(
            @IfLogin LoginUserDto loginUserDto,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sortBy,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<ProductResponseDto> results;

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

                List<Product> resultList = new ArrayList<>(searchResults);

                results = getHeartAndConverPage(resultList, sortBy, loginUserDto, pageable);

            } else {
                // 카테고리만 입력된 경우
                List<Product> resultList = new ArrayList<>(productRepository.findByCategory(category));

                results = getHeartAndConverPage(resultList, sortBy, loginUserDto, pageable);
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

                List<Product> resultList = new ArrayList<>(searchResults);

                results = getHeartAndConverPage(resultList, sortBy, loginUserDto, pageable);
            } else {
                // 아무 입력도 없는 경우
                List<Product> resultList = productService.getAllProducts();

                results = getHeartAndConverPage(resultList, sortBy, loginUserDto, pageable);
            }
        }

        return ResponseEntity.ok(results);
    }

    // 상품 생성
    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult bindingResult)
            throws IOException {

        if (bindingResult.hasErrors()) {

            // 유저가 뭘 잘못 입력했는지 출력
            ValidationResponse errors = new ValidationResponse();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errors.addFieldError(fieldError.getField(), fieldError.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
            // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body('d');
        }

        MultipartFile image = productDto.getImage();
        String uploadedUrl = null;

        // image 없어도 됨
        if (image != null) {
            uploadedUrl = s3UploadService.upload(image);
        }

        try {
            productService.createProduct(productDto, uploadedUrl); // 상품 중복 확인 후 추가
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        return ResponseEntity.ok("Success");

    }

    // sorting method
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

    //isHeart And Convert Page method
    private Page<ProductResponseDto> getHeartAndConverPage(List<Product> resultList, String sortBy,
            LoginUserDto loginUserDto, Pageable pageable) {

        // sorting method
        List<Product> sortedList = getSort(resultList, sortBy);

        // hasHeart method
        List<ProductResponseDto> heartList = new ArrayList<>();

        for (Product products : sortedList) {
            boolean isHeart = productService.hasUserLikedProduct(loginUserDto.getMemberId(), products);
            ProductResponseDto productResponseDto = productService.convertToDto(products);
            productResponseDto.setIsHeart(isHeart);
            heartList.add(productResponseDto);
        }

        // 결과 리스트를 페이지로 변환
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), heartList.size());
        Page<ProductResponseDto> result = new PageImpl<>(heartList.subList(start, end), pageable, heartList.size());

        return result;
    }

    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 상품 수정 메소드
    @PutMapping("/edit")
    public ResponseEntity<?> updateProduct(@RequestBody @Valid ProductEditDto updatedProduct) {

        int productNumber = updatedProduct.getProductNumber();
        Optional<Product> product = productRepository.findById(productNumber);

        if (product.isPresent()) {
            productService.updateProduct(productNumber, updatedProduct.getProductTitle(),
                    updatedProduct.getProductPrice(), updatedProduct.getProductStock(),
                    updatedProduct.getProductImageUrl(), updatedProduct.getCategory(),
                    updatedProduct.getIsDiscountedProduct(), updatedProduct.getProductDiscountPrice());
            return ResponseEntity.ok(updatedProduct);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 신상품 업데이트 메소드
    @PutMapping("/updateNewProduct")
    public ResponseEntity<?> updateNewProduct() {

        productService.updateProductStatus();

        return ResponseEntity.ok("신상품이 업데이트 되었습니다.");

    }

    // 상품 삭제 메소드
    @Transactional
    @DeleteMapping("/{productNumber}")
    public ResponseEntity<String> deleteProduct(@PathVariable int productNumber) {
        System.out.println("before getProductById");
        Product product = productService.getProductById(productNumber); // exception 처리 함

        try {
            if (cartItemRepository.existsByProduct(product)) {
                cartItemRepository.deleteByProduct(product);
            }
            if (heartRepository.existsByProduct(product)) {
                heartRepository.deleteByProduct(product);
            }

            productRepository.delete(product);

            String successResponse = "상품id= " + productNumber + "가 삭제되었습니다";
            return new ResponseEntity<String>(successResponse, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 신상품 조회 메소드
    @GetMapping("/newProduct")
    public ResponseEntity<Page<ProductResponseDto>> newProducts(
        @IfLogin LoginUserDto loginUserDto,
        @RequestParam(required = false) String sortBy,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<ProductResponseDto> results;
        List<Product> resultList = productRepository.findByIsNewProduct(true);
        results = getHeartAndConverPage(resultList, sortBy, loginUserDto, pageable);
        return ResponseEntity.ok(results);
    }

    // 할인상품 조회 메소드
    @GetMapping("/discountProduct")
    public ResponseEntity<Page<ProductResponseDto>> discountProducts(
        @IfLogin LoginUserDto loginUserDto,
        @RequestParam(required = false) String sortBy,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<ProductResponseDto> results;
        List<Product> resultList = productRepository.findByIsDiscountedProduct(true);
        results = getHeartAndConverPage(resultList, sortBy, loginUserDto, pageable);
        return ResponseEntity.ok(results);
    }

    // 인기상품 조회 메소드
    @GetMapping("/popularProduct")
    public ResponseEntity<Page<ProductResponseDto>> popularProducts(
        @IfLogin LoginUserDto loginUserDto,
        @RequestParam(required = false) String sortBy,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<ProductResponseDto> results;
        List<Product> resultList = productRepository.findByIsPopularProduct(true);
        results = getHeartAndConverPage(resultList, sortBy, loginUserDto, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/mainPage")
    public ResponseEntity<Map<String, List<ProductResponseDto>>> mainPage(
        @IfLogin LoginUserDto loginUserDto,
        @RequestParam(required = false) String sortBy,
        @PageableDefault(size = 4) Pageable pageable
) {
    Map<String, List<ProductResponseDto>> mainPageData = new HashMap<>();

    // Get new products
    List<Product> newProducts = productRepository.findByIsNewProduct(true);
    List<ProductResponseDto> newProductsDto = getHeartAndConverPage(newProducts, sortBy, loginUserDto, pageable).getContent();
    mainPageData.put("newProducts", newProductsDto);

    // Get discount products
    List<Product> discountProducts = productRepository.findByIsDiscountedProduct(true);
    List<ProductResponseDto> discountProductsDto = getHeartAndConverPage(discountProducts, sortBy, loginUserDto, pageable).getContent();
    mainPageData.put("discountProducts", discountProductsDto);

    // Get popular products
    List<Product> popularProducts = productRepository.findByIsPopularProduct(true);
    List<ProductResponseDto> popularProductsDto = getHeartAndConverPage(popularProducts, sortBy, loginUserDto, pageable).getContent();
    mainPageData.put("popularProducts", popularProductsDto);

    return ResponseEntity.ok(mainPageData);
}


    @GetMapping
    public ResponseEntity<List<Product>> getProducts(Pageable pageable) {
        Page<Product> productPage = productService.getAllProducts(pageable);
        List<Product> products = productPage.getContent();
        return ResponseEntity.ok(products);
    }

}
