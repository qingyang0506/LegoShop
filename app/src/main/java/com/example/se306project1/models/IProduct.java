package com.example.se306project1.models;

import java.util.List;

/**
 * @Description: This is IProduct interface which is abstraction of product class
 * @author: Frank Ji
 * @date: 9/08/2022
 */
public interface IProduct {

    String getCategoryTitle();

    void setCategoryTitle(String title);

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    double getPrice();

    void setPrice(double price);

    List<Integer> getImages();

    void setImages(List<Integer> images);

    int getStock();

    void setStock(int stock);

    void setLikesNumber(int likesNumber);

    int getLikesNumber();

    CartProduct toCartProduct();

}
