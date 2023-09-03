package com.milistock.develop.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.milistock.develop.domain.Cart;
import com.milistock.develop.repository.CartRepository;

@Service
public class CartService {
    private final CartRepository cartRepository;

    @Autowired
    public CartService(CartRepository cartRepository){
        this.cartRepository = cartRepository;
    }

    public Cart createCart(Cart cart){
        return cartRepository.save(cart);
    }

    public Optional<Cart> getCartById(int cartId){
        return cartRepository.findById(cartId);
    }

    public Optional<Cart> getCartByUserId(String userId){
        return cartRepository.findByUserId(userId);
    }

    public Cart addProductToCart(int cartId, int productNumber){
        Optional<Cart> cartOptional = cartRepository.findById(cartId);
        if (cartOptional.isPresent()) {
            Cart cart = cartOptional.get();
            cart.addProductNumber(productNumber);
            return cartRepository.save(cart);
        } else {
            return null; // Cart not found
        }
    }    

    public boolean deleteCart(int cartId) {
        Optional<Cart> cartOptional = cartRepository.findById(cartId);
        if (cartOptional.isPresent()) {
            cartRepository.delete(cartOptional.get());
            return true;
        } else {
            return false; // Cart not found
        }
    }
}
