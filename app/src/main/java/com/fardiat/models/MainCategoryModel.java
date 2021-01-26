package com.fardiat.models;

import java.io.Serializable;
import java.util.List;

public class MainCategoryModel implements Serializable {
    private int id;
    private String title;
    private String background;
    private String image;
    private String icon;
    private String parent;
    private int level;
    private String created_at;
    private boolean isSelected = false;

    private List<ProductModel> products;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBackground() {
        return background;
    }

    public String getImage() {
        return image;
    }

    public String getIcon() {
        return icon;
    }

    public String getParent() {
        return parent;
    }

    public int getLevel() {
        return level;
    }

    public List<ProductModel> getProducts() {
        return products;
    }

    public String getCreated_at() {
        return created_at;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
