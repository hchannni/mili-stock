package com.milistock.develop.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.milistock.develop.code.ErrorCode;
import com.milistock.develop.domain.Cart;
import com.milistock.develop.domain.CartItem;
import com.milistock.develop.domain.Heart;
import com.milistock.develop.domain.Member;
import com.milistock.develop.domain.Product;
import com.milistock.develop.exception.BusinessExceptionHandler;
import com.milistock.develop.repository.CartItemRepository;
import com.milistock.develop.repository.CartRepository;
import com.milistock.develop.repository.HeartRepository;
import com.milistock.develop.repository.MemberRepository;

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

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private HeartRepository heartRepository;

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

        if (cart.isPresent()) {
            return cart.get();
        } else {
            throw new BusinessExceptionHandler("카트가 존재 안 합니다", ErrorCode.NOT_FOUND_ERROR);
        }

    }

    public boolean doesCartContainProduct(Cart cart, int productId) {
        return cartItemRepository.existsByCartAndProduct_ProductNumber(cart, productId);
    }

    // not done (카트 제한 & product)
    @Transactional
    public int addProductToCart(Long memberId, int productNumber) {

        Product product = productService.getProductById(productNumber);

        Optional<Cart> ocart = cartRepository.findByMemberMemberId(memberId);
        Cart cart = ocart.orElse(null); // 현재 로그인한 회원의 장바구니 엔티티 조회

        // 회원이 장바구니 없으면, 만들어줌
        if (cart == null) {
            System.out.println("No Cart!");
            Member member = memberRepository.findByMemberId(memberId).orElse(null);
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        // 상품이 장바구니에 있는지 확인
        if (doesCartContainProduct(cart, product.getProductNumber())) {
            throw new BusinessExceptionHandler("상품이 이미 카트에 추가 돼 있습니다", ErrorCode.CONFLICT);
        }

        // 하트 있는지 확인
        Heart heart = null;
        if(heartRepository.existsByProduct(product)){
            heart = heartRepository.findByProduct(product);
        }

        // 카트에 상품 저장
        cart.addCartItem(product, 1, heart); // cart.cartItems를 update
        cartRepository.save(cart); // repo에 저장!
        return cart.getCartId();
    }

    // done
    @Transactional
    public int removeProductFromCart(Long memberId, int productNumber) {

        Product product = productService.getProductById(productNumber);

        Cart cart = findByMemberId(memberId); // 현재 로그인한 회원의 장바구니 엔티티 조회

        // 상품이 장바구니에 있는지 확인
        // 카트에 상품 추가
        if (doesCartContainProduct(cart, product.getProductNumber())) {
            cart.removeCartItem(product);
            cartRepository.save(cart);
            return cart.getCartId();
        }

        // 상품이 장바구니에 없을 경우
        else {
            throw new BusinessExceptionHandler("상품이 카트에 없습니다", ErrorCode.NOT_FOUND_ERROR);
        }
    }

    // done
    public Long deleteCart(Long memberId) {

        Cart cart = findByMemberId(memberId);

        cartRepository.delete(cart);
        return memberId;

    }

    // cartItem의 count 1 증가
    public int increaseCount(int productNumber, int quantity, Long memberId) {
        Optional<Cart> cart = getCartByUser(memberId);
        Product product = productService.getProductById(productNumber);

        if (cart.isPresent()) {
            List<CartItem> cartItems = cart.get().getCartItems();

            if (cartItems == null) {
                cartItems = new ArrayList<>();
            }

            for (CartItem cartItem : cartItems) {
                if (cartItem.getProduct().equals(product)) {
                    int newQuantity = cartItem.getQuantity() + quantity;

                    cartItem.setQuantity(newQuantity);

                    cartRepository.save(cart.get());

                    return newQuantity;
                }
            }

            // If the product is not in the cart, create a new cart item
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart.get());
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);

            cartItems.add(cartItem);

            cartRepository.save(cart.get());

            return quantity;

        } else {
            throw new BusinessExceptionHandler("카트가 존재 안 합니다", ErrorCode.NOT_FOUND_ERROR);
        }

    }

    // cartItem의 count 1 줄이기
    public int decreaseCount(int productNumber, int quantity, Long memberId) {
        Optional<Cart> cart = getCartByUser(memberId);
        Product product = productService.getProductById(productNumber);

        if (cart.isPresent()) {
            List<CartItem> cartItems = cart.get().getCartItems();

            if (cartItems != null) {
                // Iterate through the cart items to find the one associated with the given
                // product
                Iterator<CartItem> iterator = cartItems.iterator();
                while (iterator.hasNext()) {
                    CartItem cartItem = iterator.next();
                    if (cartItem.getProduct().equals(product)) {
                        int newQuantity = cartItem.getQuantity() - quantity;
                        if (newQuantity > 0) {
                            // If the quantity is more than 1, just decrement the quantity
                            cartItem.setQuantity(newQuantity);
                        } else {
                            // If the quantity is 1 or less, remove the cart item from the list
                            iterator.remove();
                        }
                        cartRepository.save(cart.get());
                        return newQuantity;
                    }
                }
                throw new BusinessExceptionHandler("상품이 카트에 없습니다", ErrorCode.NOT_FOUND_ERROR);
            } else {
                throw new BusinessExceptionHandler("상품이 카트에 없습니다", ErrorCode.NOT_FOUND_ERROR);
            }

        } else {
            throw new BusinessExceptionHandler("카트가 존재 안 합니다", ErrorCode.NOT_FOUND_ERROR);
        }

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Helper Functions
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Cart findByMemberId(Long memberId) {
        Optional<Cart> cart = cartRepository.findByMemberMemberId(memberId); // 현재 로그인한 회원의 장바구니 엔티티 조회

        // 회원이 장바구니 없으면, 에러
        if (cart.isPresent()) {
            return cart.get();
        } else {
            throw new BusinessExceptionHandler("카트가 존재 안 합니다", ErrorCode.NOT_FOUND_ERROR);
        }
    }

    // 하트를 삭제할때 참조하는 cartItem 또한 삭제하기 위해 존재하는 함수
    @Transactional
    public void removeHeart_heartId(int heartId){
        Optional<CartItem> cartItem = cartItemRepository.findByHeart_HeartId(heartId);
        if (cartItem.isPresent()){
            cartItem.get().setHeart(null);
        }
    }

    @Transactional
    public void removeHeart_productNumber(int productNumber){
        Optional<CartItem> cartItem = cartItemRepository.findByProduct_ProductNumber(productNumber);
        if (cartItem.isPresent()){
            cartItem.get().setHeart(null);
        }
    }
    

    // 하트를 생성할때 상품이 cartItem으로 돼 있으면, cartItem.setHeart()를 통해 heart를 추가 
    @Transactional
    public void addHeart(int productNumber, Heart heart){
        Optional<CartItem> cartItem = cartItemRepository.findByProduct_ProductNumber(productNumber);
        if (cartItem.isPresent()){
            cartItem.get().setHeart(heart);
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
