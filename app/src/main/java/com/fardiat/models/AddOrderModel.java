package com.fardiat.models;

import android.content.Context;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.fardiat.BR;
import com.fardiat.R;

import java.io.Serializable;
import java.util.List;

public class AddOrderModel extends BaseObservable implements Serializable {
    private List<ItemCartModel> products;
    private String address;
    private String details;
    private double longitude;
    private double latitude;
    private double total_price;
    private String order_date;
    private String order_time;
    private String coupon_id;
    private String pay_type;
    private double tax;
    private String delivery;
    private double delivery_pay;
    private int user_id;
    private double cash_pay;
    public ObservableField<String> error_address = new ObservableField<>();


    public AddOrderModel() {
        setAddress("");
        setLatitude(0.0);
        setLongitude(0.0);
        setTotal_price(0.0);
        setOrder_date("");
        setOrder_time("");
        setDetails("");
        setPay_type("");
        setDelivery("");
        setDelivery_pay(0.0);
        setTax(0.0);
        setUser_id(0);
        setCash_pay(0.0);
    }





    public boolean isStep3Valid(Context context) {
        if (!pay_type.isEmpty()) {
            return true;
        } else {
            Toast.makeText(context, R.string.ch_payment_type, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public List<ItemCartModel> getProducts() {
        return products;
    }

    public void setProducts(List<ItemCartModel> products) {
        this.products = products;
    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public double getDelivery_pay() {
        return delivery_pay;
    }

    public void setDelivery_pay(double delivery_pay) {
        this.delivery_pay = delivery_pay;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public double getCash_pay() {
        return cash_pay;
    }

    public void setCash_pay(double cash_pay) {
        this.cash_pay = cash_pay;
    }
}
