package com.milistock.develop.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.milistock.develop.domain.Cart;
import com.milistock.develop.domain.Member;
import com.milistock.develop.domain.Product;
import com.milistock.develop.repository.CartRepository;
import com.milistock.develop.repository.MemberRepository;
import com.milistock.develop.repository.ProductRepository;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart createCart(Member user) {
        Cart cart = new Cart();
        cart.setMember(user);
        return cartRepository.save(cart);
    }

    public Optional<Cart> getCartByUser(Long memberId) {
        Member user = memberRepository.findByMemberId(memberId).orElse(null);
        if (user!=null){
            Cart cart = cartRepository.findByMember(user);
            return Optional.ofNullable(cart);
        }
        return null;
    }

    public Optional<Cart> getCartById(int cartId) {
        Cart cart = cartRepository.findByCartId(cartId);
        return Optional.ofNullable(cart);
    }

    public int addProductToCart(String userId, int productNumber) {
        
        Product product = productRepository.findById(productNumber).orElse(EntityNotFoundException::new);

        Member member = memberRepository.findByUserId(userId).orElse(null);
        Cart cart = cartRepository.findByMemberId(member.getMemberId()); // 현재 로그인한 회원의 장바구니 엔티티 조회
        
        if (cart!=null){
            
               if (product!=null){
                cart.getProducts().add(product);
                return cartRepository.save(cart);
            }
        }
        return null;      
    }

    public Cart removeProductFromCart(int cartId, int productNumber) {
        Cart cart = cartRepository.findByCartId(cartId);
        Product product = productRepository.findById(productNumber).orElse(null);

        if (cart!=null && product!=null){
            if (cart.getProducts().contains(product)){
                cart.getProducts().remove(product);
                return cartRepository.save(cart);
            }
        }
        return null;     
    }

    public boolean deleteCart(int cartId) {
        Cart cart = cartRepository.findByCartId(cartId);
        Optional<Cart> cartOptional = Optional.ofNullable(cart);
        if (cartOptional.isPresent()) {
            cartRepository.delete(cartOptional.get());
            return true;
        } else {
            return false; // Cart not found
        }
    }
}

// public class CartService {
// private final CartRepository cartRepository;

// @Autowired
// public CartService(CartRepository cartRepository){
// this.cartRepository = cartRepository;
// }

// public Cart createCart(Cart cart){
// return cartRepository.save(cart);
// }

// public Optional<Cart> getCartById(int cartId){
// return cartRepository.findById(cartId);
// }

// public Optional<Cart> getCartByUserId(String userId){
// return cartRepository.findByUserId(userId);
// }

// public Cart addProductToCart(int cartId, int productNumber){
// Optional<Cart> cartOptional = cartRepository.findById(cartId);
// if (cartOptional.isPresent()) {
// Cart cart = cartOptional.get();
// cart.addProductNumber(productNumber);
// return cartRepository.save(cart);
// } else {
// return null; // Cart not found
// }
// }

// public Cart deleteProductFromCart(int cartId, int productNumber){
// Optional<Cart> cartOptional = cartRepository.findById(cartId);
// if (cartOptional.isPresent()) {
// Cart cart = cartOptional.get();
// Integer productNumberToRemove = cart.deleteProductNumber(productNumber);
// if (productNumberToRemove != null){
// return cartRepository.save(cart);
// }
// }
// return null; // Cart not found
// }

// public boolean deleteCart(int cartId) {
// Optional<Cart> cartOptional = cartRepository.findById(cartId);
// if (cartOptional.isPresent()) {
// cartRepository.delete(cartOptional.get());
// return true;
// } else {
// return false; // Cart not found
// }
// }
// }
