package com.fardiat.models;

import android.content.Context;
import android.text.TextUtils;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.fardiat.BR;
import com.fardiat.R;

public class EditProfileModel extends BaseObservable {

    private String image_url;
    private String name;

    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_email = new ObservableField<>();


    public boolean isDataValid(Context context) {


        if (!TextUtils.isEmpty(name)
        ) {


            error_name.set(null);

            return true;
        } else {


            error_name.set(context.getString(R.string.field_required));

            return false;
        }
    }

    public EditProfileModel() {

        this.name = "";
        this.image_url = "";


    }


    @Bindable
    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);

    }




}
