package com.fardiat.models;

import java.io.Serializable;
import java.util.List;

public class ItemCartUploadModel implements Serializable {
    private String user_id;
    private String address;
    private String address_lat;
    private String address_long;
    private String delivery_date;
    private String delivery_time;
    private String payment_type;
    private String tax;
    private String total_cost;
    private List<ItemCartModel> orders_details;

    public ItemCartUploadModel(String user_id, String address, String address_lat, String address_long, String delivery_date, String delivery_time, String payment_type, List<ItemCartModel> order_details, String tax, String total_cost) {
        this.user_id = user_id;
        this.address = address;
        this.address_lat = address_lat;
        this.address_long = address_long;
        this.delivery_date = delivery_date;
        this.delivery_time = delivery_time;
        this.payment_type = payment_type;
        this.orders_details = order_details;
        this.tax = tax;
        this.total_cost = total_cost;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress_lat() {
        return address_lat;
    }

    public void setAddress_lat(String address_lat) {
        this.address_lat = address_lat;
    }

    public String getAddress_long() {
        return address_long;
    }

    public void setAddress_long(String address_long) {
        this.address_long = address_long;
    }

    public String getDelivery_date() {
        return delivery_date;
    }

    public void setDelivery_date(String delivery_date) {
        this.delivery_date = delivery_date;
    }

    public String getDelivery_time() {
        return delivery_time;
    }

    public void setDelivery_time(String delivery_time) {
        this.delivery_time = delivery_time;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(String total_cost) {
        this.total_cost = total_cost;
    }

    public List<ItemCartModel> getOrders_details() {
        return orders_details;
    }

    public void setOrders_details(List<ItemCartModel> orders_details) {
        this.orders_details = orders_details;
    }

    public static class Orders_details implements Serializable {
        private int item_id;
        private String product_code;
        private int amount;
        private double cost;
        private String title;
        private String sub_image;

        public Orders_details(int item_id, String product_code, String title, double cost, int amount, String sub_image) {
            this.item_id = item_id;
            this.product_code = product_code;
            this.title = title;
            this.cost = cost;
            this.amount = amount;
            this.sub_image = sub_image;

        }

        public int getItem_id() {
            return item_id;
        }

        public void setItem_id(int item_id) {
            this.item_id = item_id;
        }

        public String getProduct_code() {
            return product_code;
        }

        public void setProduct_code(String product_code) {
            this.product_code = product_code;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public double getCost() {
            return cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSub_image() {
            return sub_image;
        }

        public void setSub_image(String sub_image) {
            this.sub_image = sub_image;
        }
    }
}
