package com.fardiat.models;

import java.io.Serializable;

public class CommentsModel implements Serializable {
    private int id;
    private int product_id;
    private int user_id;
    private String comment;
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

    public String getComment() {
        return comment;
    }

    public UserModel.User getUser() {
        return user;
    }
}
