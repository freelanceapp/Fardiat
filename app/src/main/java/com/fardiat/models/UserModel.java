package com.fardiat.models;

import java.io.Serializable;

public class UserModel implements Serializable {
    private User user;

    public User getUser() {
        return user;
    }

    public static class User implements Serializable {
        private int id;
        private String name;
        private String email;
        private String user_type;
        private String phone_code;
        private String phone;
        private String logo;
        private String token;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getUser_type() {
            return user_type;
        }

        public String getPhone_code() {
            return phone_code;
        }

        public String getPhone() {
            return phone;
        }

        public String getLogo() {
            return logo;
        }

        public String getToken() {
            return token;
        }
    }
}
