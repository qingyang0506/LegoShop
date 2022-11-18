package com.example.se306project1.models;

/**
 * @Description: This is CartProduct class which used for product in shopping cart
 * @author: Frank Ji
 * @date: 9/08/2022
 */
public class CartProduct extends Product {

    private int amount;

    public CartProduct() {
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
