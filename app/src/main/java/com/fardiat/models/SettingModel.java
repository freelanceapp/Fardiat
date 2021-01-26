package com.fardiat.models;

import java.io.Serializable;

public class SettingModel implements Serializable {

    private Data settings;
    private double coupon_value;
    private int coupon_id;


    public Data getSettings() {
        return settings;
    }

    public double getCoupon_value() {
        return coupon_value;
    }

    public int getCoupon_id() {
        return coupon_id;
    }


    public static class Data implements Serializable {
        private String termis_condition;
        private String about_app;
        private String whatsapp;
        private String instagram;
        private String facebook;
        private String twitter;
        private String offer_muted;
        private double delivery_value;
        private double tax;
        private double pay_when_recieving;

        public String getTermis_condition() {
            return termis_condition;
        }

        public String getAbout_app() {
            return about_app;
        }


        public String getWhatsapp() {
            return whatsapp;
        }

        public String getInstagram() {
            return instagram;
        }

        public String getFacebook() {
            return facebook;
        }

        public String getTwitter() {
            return twitter;
        }

        public String getOffer_muted() {
            return offer_muted;
        }

        public double getDelivery_value() {
            return delivery_value;
        }

        public double getTax() {
            return tax;
        }

        public double getPay_when_recieving() {
            return pay_when_recieving;
        }
    }
}
