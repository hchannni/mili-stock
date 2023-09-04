package com.milistock.develop.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder    
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="cart")
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cartId;
    private String userId;
    private List<Integer> productNumbers;

    public void addProductNumber(int productNumber){
        productNumbers.add(productNumber);
    }
    
    public Integer deleteProductNumber(int productNumber) {
        if (productNumbers == null) {
            return null; // Return null if the list is null
        }
        
        // Attempt to remove the productNumber by its value (remove(Integer.valueOf(33)) deletes item with value 33)
        boolean removed = productNumbers.remove(Integer.valueOf(productNumber));
        
        // Check if the productNumber was found and removed
        if (removed) {
            return productNumber;
        } else {
            return null; // Return null if productNumber was not found
        }
    }
}