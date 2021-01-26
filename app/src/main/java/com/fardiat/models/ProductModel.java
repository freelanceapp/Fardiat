package com.fardiat.models;

import java.io.Serializable;
import java.util.List;

public class ProductModel implements Serializable {
    private int id;
    private String title;
    private String image;
    private int departemnt_id;
    private double price;
    private int user_id;
    private String details;
    private double google_lat;
    private double google_long;
    private String address;
    private String vedio;
    private String created_at;
    private String block_check="";
    private UserModel.User user;
    private MainCategoryModel department_fk;
    private List<ProductImageModel> products_images;
    private List<ProductDetailsModel> product_details;
    private UserLike user_like;


    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public int getDepartemnt_id() {
        return departemnt_id;
    }

    public double getPrice() {
        return price;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getDetails() {
        return details;
    }

    public double getGoogle_lat() {
        return google_lat;
    }

    public double getGoogle_long() {
        return google_long;
    }

    public String getAddress() {
        return address;
    }

    public String getVedio() {
        return vedio;
    }

    public MainCategoryModel getDepartment_fk() {
        return department_fk;
    }

    public String getCreated_at() {
        return created_at;
    }

    public List<ProductImageModel> getProducts_images() {
        return products_images;
    }

    public UserModel.User getUser() {
        return user;
    }

    public List<ProductDetailsModel> getProduct_details() {
        return product_details;
    }

    public UserLike getUser_like() {
        return user_like;
    }

    public void setUser_like(UserLike user_like) {
        this.user_like = user_like;
    }

    public String getBlock_check() {
        return block_check;
    }

    public class UserLike implements Serializable {

    }
}
