package com.fardiat.models;

import java.io.Serializable;
import java.util.List;

public class OrderModel implements Serializable {

    private int id;
    private String order_code;
    private int user_id;
    private String order_status;
    private double total_price;
    private long order_date;
    private String order_time;
    private String pay_type;
    private int coupon_id;
    private String address;
    private double latitude;
    private double longitude;
    private String details;
    private String created_at;
    private String title;
    private List<OrdersDetails> order_product;

    public int getId() {
        return id;
    }

    public String getOrder_code() {
        return order_code;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public double getTotal_price() {
        return total_price;
    }

    public long getOrder_date() {
        return order_date;
    }

    public String getOrder_time() {
        return order_time;
    }

    public String getPay_type() {
        return pay_type;
    }

    public int getCoupon_id() {
        return coupon_id;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDetails() {
        return details;
    }

    public String getCreated_at() {
        return created_at;
    }

    public List<OrdersDetails> getOrder_product() {
        return order_product;
    }

    public String getTitle() {
        return title;
    }

    private Data tler;

    public Data getTler() {
        return tler;
    }

    public class OrdersDetails implements Serializable {
        private int id;
        private int product_id;
        private int order_id;
        private double price;
        private int amount;
        private String book_date_from;
        private String book_date_to;

        private ProductInfo product_info;

        public int getId() {
            return id;
        }

        public int getProduct_id() {
            return product_id;
        }

        public int getOrder_id() {
            return order_id;
        }

        public double getPrice() {
            return price;
        }

        public int getAmount() {
            return amount;
        }

        public ProductInfo getProduct_info() {
            return product_info;
        }

        public String getBook_date_from() {
            return book_date_from;
        }

        public String getBook_date_to() {
            return book_date_to;
        }

        public class ProductInfo implements Serializable {
            private int id;
            private String title;
            private String image;
            private String price;

            public int getId() {
                return id;
            }

            public String getTitle() {
                return title;
            }

            public String getImage() {
                return image;
            }

            public String getPrice() {
                return price;
            }
        }

        public class Item implements Serializable {
            private String id;
            private String sub_image;
            private String price;
            private String dimensions;
            private String product_title;
            private String content;
            private String is_favourite;
            private String available;

            public String getId() {
                return id;
            }

            public String getSub_image() {
                return sub_image;
            }

            public String getPrice() {
                return price;
            }

            public String getDimensions() {
                return dimensions;
            }

            public String getProduct_title() {
                return product_title;
            }

            public String getContent() {
                return content;
            }

            public String getIs_favourite() {
                return is_favourite;
            }

            public String getAvailable() {
                return available;
            }
        }
    }

    public class Data implements Serializable {
        private String url;
        private String succes_url;
        private String canceled_url;
        private String declined_url;

        public String getUrl() {
            return url;
        }

        public String getSucces_url() {
            return succes_url;
        }

        public String getCanceled_url() {
            return canceled_url;
        }

        public String getDeclined_url() {
            return declined_url;
        }
    }


}
