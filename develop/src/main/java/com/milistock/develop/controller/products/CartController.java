package com.milistock.develop.controller.products;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.milistock.develop.code.ErrorCode;
import com.milistock.develop.domain.Cart;
import com.milistock.develop.domain.Member;
import com.milistock.develop.exception.BusinessExceptionHandler;
import com.milistock.develop.service.CartService;
import com.milistock.develop.service.MemberService;

@RestController
@RequestMapping("/carts")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private MemberService memberService;

    // // 수정1: input user 말고 userId로 하기
    // @PostMapping
    // public ResponseEntity<?> createCart(@RequestBody Member user) {
    // Member existingMember = memberService.findByMemberId(user.getMemberId());

    // if (existingMember == null) {
    // // Member does not exist, return an error response
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Member does not
    // exist.");
    // }

    // Cart createdCart = cartService.createCart(existingMember);
    // return ResponseEntity.ok(createdCart);
    // }

    // Cart에다가 product 추가할때 어떻게 하는지. 내 코드는 지금 -> (1) memberId로 Cart 찾고 (2) Cart의 CartId로 prduct 집어넣기
    // Member에다가 바로 CartId 넣으면 되지 않을까?
    // public void createCart(Long memberId, Principal principal) {
    //     Member existingMember = memberService.findByMemberId(memberId);
    //     if (existingMember == null) {
    //         // Member does not exist, return an error response
    //         throw new BusinessExceptionHandler("이름이 일치하지 않습니다.", ErrorCode.NOT_FOUND_ERROR);            
    //     }

    //     String email = principal.getName();
    //     System.out.println("Email: " + email);

    //     Cart createdCart = cartService.createCart(existingMember);
        
    // }

    @GetMapping
    public ResponseEntity<Cart> getCart(Principal principal) {
        String userInfo = principal.getName(); // "LoginInfoDto(memberId=6, serviceNumber=22-70014661, name=김동현)"        

        Optional<Cart> cart = cartService.getCart(userInfo);

        if (cart.isPresent()) {
            return ResponseEntity.ok(cart.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<Cart> getCartById(@PathVariable int cartId) {
        Optional<Cart> cart = cartService.getCartById(cartId);
        if (cart.isPresent()) {
            return ResponseEntity.ok(cart.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{memberId}")
    public ResponseEntity<Cart> getCartByUserId(@PathVariable Long memberId) {
        Optional<Cart> cart = cartService.getCartByUser(memberId);
        if (cart.isPresent()) {
            return ResponseEntity.ok(cart.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 카트에 상품 추가
    // +유저가 카트 없을 시, 카트 최초 생성!
    @PostMapping("/productNumber/{productNumber}")
    public ResponseEntity<?> addProductToCart(@PathVariable int productNumber, Principal principal) {
        
        String userInfo = principal.getName(); // "LoginInfoDto(memberId=6, serviceNumber=22-70014661, name=김동현)"        
        int cartItemId;

        // System.out.println(userInfo);

        try {
            cartItemId = cartService.addProductToCart(userInfo, productNumber); //dto -> entity
        } catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST); // 장바구니에 잘 안담겼으면 404
        }

        return new ResponseEntity<Integer>(cartItemId, HttpStatus.OK);
    }

    // 카트에 상품 제거
    @DeleteMapping("/productNumber/{productNumber}")
    public ResponseEntity<?> deleteProductFromCart(@PathVariable int productNumber, Principal principal) {

        String userInfo = principal.getName(); // "LoginInfoDto(memberId=6, serviceNumber=22-70014661, name=김동현)"        
        int cartItemId;

        try {
            cartItemId = cartService.removeProductFromCart(userInfo, productNumber); //dto -> entity
        } catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST); // 장바구니에 잘 안담겼으면 404
        }

        return new ResponseEntity<Integer>(cartItemId, HttpStatus.OK);

    }

    // 카트 제거
    @DeleteMapping
    public ResponseEntity<String> deleteCart(Principal principal) {
        String userInfo = principal.getName(); // "LoginInfoDto(memberId=6, serviceNumber=22-70014661, name=김동현)"        
        Long memberId;

        try {
            memberId = cartService.deleteCart(userInfo);
        } catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST); // 장바구니에 잘 안담겼으면 404
        }

        String successResponse = "유저 " + memberId + " 카트가 삭제되었습니다";
        return new ResponseEntity<String>(successResponse, HttpStatus.OK);
    }
}
