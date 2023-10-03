package com.milistock.develop.service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.milistock.develop.code.ErrorCode;
import com.milistock.develop.domain.Cart;
import com.milistock.develop.domain.Member;
import com.milistock.develop.domain.Product;
import com.milistock.develop.exception.BusinessExceptionHandler;
import com.milistock.develop.repository.CartRepository;
import com.milistock.develop.repository.MemberRepository;
import com.milistock.develop.repository.ProductRepository;
import com.milistock.develop.utils.RegexFunctions;

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
        if (user != null) {
            Cart cart = cartRepository.findByMember(user);
            return Optional.ofNullable(cart);
        }
        return null;
    }

    public Optional<Cart> getCartById(int cartId) {
        Cart cart = cartRepository.findByCartId(cartId);
        return Optional.ofNullable(cart);
    }

    public int addProductToCart(String userInfo, int productNumber) {
        // userInfo = "LoginInfoDto(memberId=6, serviceNumber=22-70014661, name=김동현)"
        // 에서 memberId=6만 추출하기
        Long memberId = RegexFunctions.extractMemberId(userInfo);

        Product product = productRepository.findById(productNumber).orElseThrow(EntityNotFoundException::new);
        
        Cart cart = cartRepository.findByMemberMemberId(memberId); // 현재 로그인한 회원의 장바구니 엔티티 조회

        // 회원이 장바구니 없으면, 만들어줌
        if (cart == null) {
            Member member = memberRepository.findByMemberId(memberId).orElse(null);
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        // 상품이 장바구니에 있는지 확인
        if (cart.containsProduct(product)){
            throw new BusinessExceptionHandler("상품이 이미 카트에 추가 돼 있습니다", ErrorCode.CONFLICT); 
        }

        // 카트에 상품 저장
        cart.getProducts().add(product);
        cartRepository.save(cart);
        return cart.getCartId();
    }

    public Cart removeProductFromCart(int cartId, int productNumber) {
        Cart cart = cartRepository.findByCartId(cartId);
        Product product = productRepository.findById(productNumber).orElse(null);

        if (cart != null && product != null) {
            if (cart.getProducts().contains(product)) {
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
