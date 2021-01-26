package com.fardiat.models;

import java.io.Serializable;
import java.util.List;

public class ProductDataModel implements Serializable {
    private int current_page;
    private List<ProductModel> data;

    public int getCurrent_page() {
        return current_page;
    }

    public List<ProductModel> getData() {
        return data;
    }
}
