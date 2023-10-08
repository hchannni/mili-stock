package com.milistock.develop.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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

    @ManyToMany
    @JoinTable(
        name = "cart_products",
        joinColumns = @JoinColumn(name = "cart_Id"),
        inverseJoinColumns = @JoinColumn(name = "product_number")
    )
    private List<Product> products;

    // CartService에서 유저가 카트에 상품 담을때, 카트가 아직 부재 시 호출
    public static Cart createCart(Member member){
        Cart cart = new Cart();
        cart.setMember(member);
        cart.setProducts(new ArrayList<>());
        return cart;
    }

    // Check if a product is already in the cart
    public boolean containsProduct(Product product) {
        if (products == null) {
            return false; // Handle the case where the products list is null
        }

        return products.contains(product);
    }

}