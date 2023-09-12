package com.milistock.develop.controller.products;

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

import com.milistock.develop.domain.Cart;
import com.milistock.develop.domain.Member;
import com.milistock.develop.domain.Product;
import com.milistock.develop.service.CartService;
import com.milistock.develop.service.MemberService;

@RestController
@RequestMapping("/carts")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private MemberService memberService;

    @PostMapping
    public ResponseEntity<?> createCart(@RequestBody Member user) {
        Member existingMember = memberService.findByMemberId(user.getMemberId());

        if (existingMember == null) {
            // Member does not exist, return an error response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Member does not exist.");
        }

        Cart createdCart = cartService.createCart(existingMember);
        return ResponseEntity.ok(createdCart);
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

    // Update -> add/delete productID from Cart
    
    @PostMapping("/{cartId}/addProduct/{productNumber}")
    public ResponseEntity<Cart> addProductToCart(@PathVariable int cartId, @PathVariable int productNumber) {
        Cart updatedCart = cartService.addProductToCart(cartId, productNumber);
        if (updatedCart != null) {
            return ResponseEntity.ok(updatedCart);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{cartId}/deleteProduct/{productNumber}")
    public ResponseEntity<Cart> deleteProductFromCart(@PathVariable int cartId, @PathVariable int productNumber) {
        Cart updatedCart = cartService.removeProductFromCart(cartId, productNumber);
        if (updatedCart != null) {
            return ResponseEntity.ok(updatedCart);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> deleteCart(@PathVariable int cartId) {
        boolean deleted = cartService.deleteCart(cartId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
