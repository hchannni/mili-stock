package com.milistock.develop.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.milistock.develop.code.ErrorCode;
import com.milistock.develop.exception.BusinessExceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 수정2: 왜 DB cart table의 column에 "member"가 있는지 모르겠음

@Data
@Builder    
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="cart")
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private int cartId;
    
    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL) // cascade를 통해 cart를 예: 삭제할 때 모든 cartItem들도 삭제
    private List<CartItem> cartItems;

    // CartService에서 유저가 카트에 상품 담을때, 카트가 아직 부재 시 호출
    public static Cart createCart(Member member){
        Cart cart = new Cart();
        cart.setMember(member);
        cart.setCartItems(new ArrayList<>());
        return cart;
    }

    public void addCartItem(Product product, int quantity) {
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
    
        // Check if the product is already in the cart
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct().equals(product)) {
                throw new BusinessExceptionHandler("상품이 이미 카트에 추가 돼 있습니다", ErrorCode.CONFLICT); 
            }
        }
    
        // If the product is not in the cart, create a new cart item
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setCart(this);
    
        cartItems.add(cartItem);
    }

    // 카트 아이템 숫자 하나 더하기
    public void incrementCartItemCount(Product product, int quantity) {
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }

        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct().equals(product)) {
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                return;
            }
        }

        // If the product is not in the cart, create a new cart item
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setCart(this);

        cartItems.add(cartItem);
    }

    // 카트 아이템 제거
    // 더 효율: repo로 productId로 찾으면 더 빠름
    public void removeCartItem(Product product) {
        if (cartItems != null) {
            Iterator<CartItem> iterator = cartItems.iterator();
            while (iterator.hasNext()) {
                CartItem cartItem = iterator.next();
                if (cartItem.getProduct().equals(product)) {
                    iterator.remove();
                    return;
                }
            }
            throw new BusinessExceptionHandler("상품이 카트에 없습니다", ErrorCode.NOT_FOUND_ERROR); 
        }
        else {
            throw new BusinessExceptionHandler("상품이 카트에 없습니다", ErrorCode.NOT_FOUND_ERROR); 
        }
    }

    // 카트 아이템 숫자 하나 줄이기
    // 더 효율: repo로 productId로 찾으면 더 빠름
    public void decrementCartItemCount(Product product) {
        if (cartItems != null) {
            // Iterate through the cart items to find the one associated with the given product
            Iterator<CartItem> iterator = cartItems.iterator();
            while (iterator.hasNext()) {
                CartItem cartItem = iterator.next();
                if (cartItem.getProduct().equals(product)) {
                    int newQuantity = cartItem.getQuantity() - 1;
                    if (newQuantity > 0) {
                        // If the quantity is more than 1, just decrement the quantity
                        cartItem.setQuantity(newQuantity);
                    } else {
                        // If the quantity is 1 or less, remove the cart item from the list
                        iterator.remove();
                    }
                    return;
                }
            }
            throw new BusinessExceptionHandler("상품이 카트에 없습니다", ErrorCode.NOT_FOUND_ERROR); 
        }
    }


}