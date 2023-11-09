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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.milistock.develop.domain.Cart;
import com.milistock.develop.service.CartService;
import com.milistock.develop.utils.RegexFunctions;

@RestController
@RequestMapping("/carts")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(Principal principal) {
        Long memberId = RegexFunctions.extractMemberId(principal); // "LoginInfoDto(memberId=6, serviceNumber=22-70014661, name=김동현)"        

        Optional<Cart> cart = cartService.getCart(memberId);

        if (cart.isPresent()) {
            return ResponseEntity.ok(cart.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<Cart> getCartById(@PathVariable int cartId) {
        Cart cart = cartService.getCartById(cartId);
        System.out.println("Before return cart");
        return ResponseEntity.ok(cart);
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
        Long memberId = RegexFunctions.extractMemberId(principal); // "LoginInfoDto(memberId=6, serviceNumber=22-70014661, name=김동현)"        
        int cartItemId;

        try {
            System.out.println(productNumber);
            cartItemId = cartService.addProductToCart(memberId, productNumber); //dto -> entity
        } catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST); // 장바구니에 잘 안담겼으면 404
        }

        return new ResponseEntity<Integer>(cartItemId, HttpStatus.OK);
    }

    // 카트에 상품 제거
    @DeleteMapping("/productNumber/{productNumber}")
    public ResponseEntity<?> deleteProductFromCart(@PathVariable int productNumber, Principal principal) {
        Long memberId = RegexFunctions.extractMemberId(principal); // "LoginInfoDto(memberId=6, serviceNumber=22-70014661, name=김동현)"        
        int cartItemId;

        cartItemId = cartService.removeProductFromCart(memberId, productNumber);
        // try {
        //     cartItemId = cartService.removeProductFromCart(memberId, productNumber); //dto -> entity
        // } catch(Exception e){
        //     return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST); // 장바구니에 잘 안담겼으면 404
        // }

        return new ResponseEntity<Integer>(cartItemId, HttpStatus.OK);

    }

    // 카트 제거
    @DeleteMapping
    public ResponseEntity<String> deleteCart(Principal principal) {
        Long memberId = RegexFunctions.extractMemberId(principal); // "LoginInfoDto(memberId=6, serviceNumber=22-70014661, name=김동현)"        

        try {
            memberId = cartService.deleteCart(memberId);
        } catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST); // 장바구니에 잘 안담겼으면 404
        }

        String successResponse = "유저 " + memberId + " 카트가 삭제되었습니다";
        return new ResponseEntity<String>(successResponse, HttpStatus.OK);
    }
}
