package com.milistock.develop.domain;

import java.util.ArrayList;
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

    public void addProduct(Product product, int quantity) {
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
    
        // Check if the product is already in the cart -> If yes, update quantity
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


}