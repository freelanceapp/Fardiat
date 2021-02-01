package com.fardiat.models;

import android.content.Context;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.fardiat.BR;
import com.fardiat.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddAdsModel extends BaseObservable implements Serializable {
    private int department_id;
    private String name;
    private String price;
    private String details;
    private String address;
    private double lat;
    private double lng;
    private boolean hasExtraItems;
    private String video_url;
    private List<String> imagesList;
    private List<ItemAddAds> itemAddAdsList;
    private boolean swear = false;


    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_price = new ObservableField<>();
    public ObservableField<String> error_details = new ObservableField<>();
    public ObservableField<String> error_address = new ObservableField<>();


    public boolean isDataValid(Context context)
    {

        if (department_id!=0&&
                !name.isEmpty() &&
                !price.isEmpty() &&
                !details.isEmpty() &&
                !address.isEmpty() &&
                imagesList.size() == 1&&
                swear
        ) {

            error_name.set(null);
            error_price.set(null);
            error_details.set(null);
            error_address.set(null);
            if (hasExtraItems){
                if (itemAddAdsList.size()>0){

                    if (isDataItemValid(context)){
                        return true;

                    }else {
                        return false;
                    }
                }else {
                    return false;
                }
            }else {

                return true;
            }

        } else {

            if (name.isEmpty()){
                error_name.set(context.getString(R.string.field_required));

            }else {
                error_name.set(null);

            }

            if (price.isEmpty()){
                error_price.set(context.getString(R.string.field_required));

            }else {
                error_price.set(null);

            }

            if (details.isEmpty()){
                error_details.set(context.getString(R.string.field_required));

            }else {
                error_details.set(null);

            }

            if (address.isEmpty()){
                error_address.set(context.getString(R.string.field_required));

            }else {
                error_address.set(null);

            }

            if (imagesList.size()!=1){
                Toast.makeText(context, R.string.choose_maximum2_image, Toast.LENGTH_SHORT).show();
            }

            if (department_id==0){
                Toast.makeText(context, R.string.choose_category, Toast.LENGTH_SHORT).show();

            }

            if (!swear){
                Toast.makeText(context, R.string.accept_swear, Toast.LENGTH_SHORT).show();
            }
            if (hasExtraItems&&itemAddAdsList.size()>0){
                if (isDataItemValid(context)){

                }
            }

            return false;
        }
    }


    public AddAdsModel() {
        department_id = 0;
        name = "";
        price = "";
        details = "";
        address = "";
        lat = 0.0;
        lng = 0.0;
        video_url = "";
        imagesList = new ArrayList<>();
        hasExtraItems = false;
        itemAddAdsList = new ArrayList<>();
    }

    public int getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(int department_id) {
        this.department_id = department_id;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
        notifyPropertyChanged(BR.price);

    }

    @Bindable
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
        notifyPropertyChanged(BR.details);

    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);

    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public boolean isHasExtraItems() {
        return hasExtraItems;
    }

    public void setHasExtraItems(boolean hasExtraItems) {
        this.hasExtraItems = hasExtraItems;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public List<String> getImagesList() {
        return imagesList;
    }

    public void setImagesList(List<String> imagesList) {
        this.imagesList = imagesList;
    }

    public List<ItemAddAds> getItemAddAdsList() {
        return itemAddAdsList;
    }

    public void setItemAddAdsList(List<ItemAddAds> itemAddAdsList) {
        this.itemAddAdsList = itemAddAdsList;
    }

    public boolean isSwear() {
        return swear;
    }

    public void setSwear(boolean swear) {
        this.swear = swear;
    }

    public boolean isDataItemValid(Context context){
        boolean valid = true;


        for (ItemAddAds itemAddAds:itemAddAdsList){
            if (itemAddAds.getContent().isEmpty()){
                valid = false;
            }
        }


        return valid;
    }
}
