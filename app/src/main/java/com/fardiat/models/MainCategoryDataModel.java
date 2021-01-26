package com.fardiat.models;

import java.io.Serializable;
import java.util.List;

public class MainCategoryDataModel implements Serializable {
    private List<MainCategoryModel> data;

    public List<MainCategoryModel> getData() {
        return data;
    }
}
