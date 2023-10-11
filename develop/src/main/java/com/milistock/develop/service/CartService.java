package com.milistock.develop.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.milistock.develop.code.ErrorCode;
import com.milistock.develop.domain.Cart;
import com.milistock.develop.domain.Member;
import com.milistock.develop.domain.Product;
import com.milistock.develop.exception.BusinessExceptionHandler;
import com.milistock.develop.repository.CartRepository;
import com.milistock.develop.repository.MemberRepository;
import com.milistock.develop.utils.RegexFunctions;

@Service
public class CartService {
    @Autowired
    private ProductService productService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private MemberRepository memberRepository;

    public Cart createCart(Member user) {
        Cart cart = new Cart();
        cart.setMember(user);
        return cartRepository.save(cart);
    }

    // done
    public Optional<Cart> getCartByUser(Long memberId) {
        Member user = memberService.findByMemberId(memberId);
        if (user != null) {
            Cart cart = cartRepository.findByMember(user);
            return Optional.ofNullable(cart);
        }
        return null;
    }

    // done
    public Optional<Cart> getCart(Long memberId) {
        Cart cart = findByMemberId(memberId);
        
        return Optional.ofNullable(cart);
    }

    // done
    public Cart getCartById(int cartId) {

        Optional<Cart> cart = cartRepository.findByCartId(cartId);

        if(cart.isPresent()){
            return cart.get();
        } else{
            throw new BusinessExceptionHandler("카트가 존재 안 합니다", ErrorCode.NOT_FOUND_ERROR);
        }
        
    }
    
    // not done (카트 제한 & product)
    public int addProductToCart(Long memberId, int productNumber) {

        Product product = productService.getProductById(productNumber);
        
        Cart cart = cartRepository.findByMemberMemberId(memberId).get(); // 현재 로그인한 회원의 장바구니 엔티티 조회

        // 회원이 장바구니 없으면, 만들어줌
        if (cart == null) {
            System.out.println("No Cart!");
            Member member = memberRepository.findByMemberId(memberId).orElse(null);
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        // 상품이 장바구니에 있는지 확인
        if (cart.containsProduct(product)){
            throw new BusinessExceptionHandler("상품이 이미 카트에 추가 돼 있습니다", ErrorCode.CONFLICT); 
        }

        System.out.println("just before storing product!");
        // 카트에 상품 저장
        cart.getProducts().add(product);
        cartRepository.save(cart);
        return cart.getCartId();
    }

    // done
    public int removeProductFromCart(Long memberId, int productNumber) {

        Product product = productService.getProductById(productNumber);

        Cart cart = findByMemberId(memberId); // 현재 로그인한 회원의 장바구니 엔티티 조회

        // 상품이 장바구니에 있는지 확인
        // 카트에 상품 추가
        if (cart.containsProduct(product)){
            cart.getProducts().remove(product);
            cartRepository.save(cart);
            return cart.getCartId();            
        }

        // 상품이 장바구니에 없을 경우
        else {
            throw new BusinessExceptionHandler("상품이 카트에 없습니다", ErrorCode.CONFLICT); 
        }
    }

    // done
    public Long deleteCart(Long memberId) {

        Cart cart = findByMemberId(memberId);

        cartRepository.delete(cart);
        return memberId;

    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Helper Functions
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public Cart findByMemberId(Long memberId){
        Optional<Cart> cart = cartRepository.findByMemberMemberId(memberId); // 현재 로그인한 회원의 장바구니 엔티티 조회
        
        // 회원이 장바구니 없으면, 에러
        if (cart.isPresent()) {
            return cart.get();
        } else {
            throw new BusinessExceptionHandler("카트가 존재 안 합니다", ErrorCode.NOT_FOUND_ERROR); 
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
