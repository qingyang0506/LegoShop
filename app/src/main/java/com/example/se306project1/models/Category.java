package com.example.se306project1.models;

/**
 * @Description: This is Category abstract class which used for three categories and implement the ICategory interface
 * @author: XiaoXiao Zhuang
 * @date: 9/08/2022
 */
public abstract class Category implements ICategory {

    private String title;
    private int image;
    private String layout;

    public Category(String title, int image, String layout) {
        this.title = title;
        this.image = image;
        this.layout = layout;
    }

    public void setTitle(String tile) {
        this.title = tile;

    }

    public String getTitle() {
        return title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getLayout() {
        return layout;
    }


}
