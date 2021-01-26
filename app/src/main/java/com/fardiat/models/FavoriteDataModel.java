package com.fardiat.models;

import java.io.Serializable;
import java.util.List;

public class FavoriteDataModel implements Serializable {
    private List<FavoriteModel> data;

    public List<FavoriteModel> getData() {
        return data;
    }
}
