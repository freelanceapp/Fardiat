package com.fardiat.models;

import java.io.Serializable;

public class FavoriteModel implements Serializable {
    private int id;
    private int product_id;
    private int user_id;
    private ProductModel product;
    private UserModel.User user;

    public int getId() {
        return id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public ProductModel getProduct() {
        return product;
    }

    public UserModel.User getUser() {
        return user;
    }
}
