package com.fardiat.models;

import java.io.Serializable;

public class ProductDetailsModel implements Serializable {
    private int id;
    private String icon;
    private String title;
    private String content;

    public int getId() {
        return id;
    }

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
