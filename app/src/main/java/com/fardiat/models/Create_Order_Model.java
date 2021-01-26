package com.fardiat.models;

import java.io.Serializable;
import java.util.List;

public class Create_Order_Model implements Serializable {

    private String user_id;
    private double total_price;
    private String pay_type = "cash";
    private List<OrderDetails> products;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public List<OrderDetails> getProducts() {
        return products;
    }

    public void setProducts(List<OrderDetails> products) {
        this.products = products;
    }

    public static class OrderDetails implements Serializable {
        private int product_id;
        private double price;

        private String book_date_from;
        private String book_date_to;


        public int getProduct_id() {
            return product_id;
        }

        public void setProduct_id(int product_id) {
            this.product_id = product_id;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getBook_date_from() {
            return book_date_from;
        }

        public void setBook_date_from(String book_date_from) {
            this.book_date_from = book_date_from;
        }

        public String getBook_date_to() {
            return book_date_to;
        }

        public void setBook_date_to(String book_date_to) {
            this.book_date_to = book_date_to;
        }
    }


}
