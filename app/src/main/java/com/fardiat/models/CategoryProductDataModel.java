package com.fardiat.models;

import java.io.Serializable;
import java.util.List;

public class CategoryProductDataModel implements Serializable {
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public class Data implements Serializable {

        private int id;
        private String title;
        private String background;
        private String image;
        private String icon;
        private String parent;
        private int level;
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
    }
}