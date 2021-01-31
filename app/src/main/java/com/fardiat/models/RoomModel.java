package com.fardiat.models;

import java.io.Serializable;

public class RoomModel implements Serializable {
    private int id;
    private int first_user_id;
    private int second_user_id;
    private int is_approved;
    private String created_at;
    private String updated_at;
    private String other_user_name;
    private String other_user_email;
    private String other_user_phone_code;
    private String other_user_phone;
    private String other_user_logo;
    private String note;

    public int getId() {
        return id;
    }

    public int getFirst_user_id() {
        return first_user_id;
    }

    public int getSecond_user_id() {
        return second_user_id;
    }

    public int getIs_approved() {
        return is_approved;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getOther_user_name() {
        return other_user_name;
    }

    public String getOther_user_email() {
        return other_user_email;
    }

    public String getOther_user_phone_code() {
        return other_user_phone_code;
    }

    public String getOther_user_phone() {
        return other_user_phone;
    }

    public String getOther_user_logo() {
        return other_user_logo;
    }

    public String getNote() {
        return note;
    }
}
