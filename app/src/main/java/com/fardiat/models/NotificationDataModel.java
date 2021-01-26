package com.fardiat.models;

import java.io.Serializable;
import java.util.List;

public class NotificationDataModel implements Serializable {

    private List<NotificationModel> data;

    public List<NotificationModel> getData() {
        return data;
    }


}
