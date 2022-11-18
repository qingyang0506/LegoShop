package com.example.se306project1.models;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * @Description: This is Product class which is implemented IProduct interface
 * @author: Frank Ji
 * @date: 9/08/2022
 */
public class Product implements IProduct {

    public static int LOW_STOCK_BOUNDARY = 50;

    private String categoryTitle;
    private String name;
    private String description;
    private double price;
    private List<Integer> images;
    private int stock;
    private int likesNum;

    @NonNull
    @Override
    public String toString() {
        return "Product{" + name + "}\n";
    }

    public Product() {

    }

    public Product(String categoryTitle, String name, String description, double price, List<Integer> images, int stock, int likesNum) {
        this.categoryTitle = categoryTitle;
        this.name = name;
        this.description = description;
        this.price = price;
        this.images = images;
        this.stock = stock;
        this.likesNum = likesNum;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Integer> getImages() {
        return images;
    }

    public void setImages(List<Integer> images) {
        this.images = images;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public void setLikesNumber(int likesNumber) {
        this.likesNum = likesNumber;
    }

    @Override
    public int getLikesNumber() {
        return likesNum;
    }

    //convert the product object to cart product which the initial amount equal to 0
    public CartProduct toCartProduct() {
        CartProduct cartProduct = new CartProduct();
        cartProduct.setCategoryTitle(this.categoryTitle);
        cartProduct.setName(this.name);
        cartProduct.setDescription(this.description);
        cartProduct.setPrice(this.price);
        cartProduct.setImages(this.images);
        cartProduct.setStock(this.stock);
        cartProduct.setLikesNumber(this.likesNum);
        cartProduct.setAmount(1);
        return cartProduct;
    }

}
