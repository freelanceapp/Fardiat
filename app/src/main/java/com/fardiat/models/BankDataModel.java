package com.fardiat.models;

import java.io.Serializable;
import java.util.List;

public class BankDataModel implements Serializable {

    private List<BankModel> data;

    public List<BankModel> getData() {
        return data;
    }


    public static class BankModel implements Serializable {
        private int id;
        private String bank_name;
        private String account_name;
        private String account_number;
        private String account_iban;
        private String account_image;

        public int getId() {
            return id;
        }

        public String getBank_name() {
            return bank_name;
        }

        public String getAccount_name() {
            return account_name;
        }

        public String getAccount_number() {
            return account_number;
        }

        public String getAccount_iban() {
            return account_iban;
        }

        public String getAccount_image() {
            return account_image;
        }
    }
}
