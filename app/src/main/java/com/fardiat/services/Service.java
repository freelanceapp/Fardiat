package com.fardiat.services;


import com.fardiat.models.AddOrderModel;
import com.fardiat.models.BankDataModel;
import com.fardiat.models.FavoriteDataModel;
import com.fardiat.models.ItemAddAdsDataModel;
import com.fardiat.models.MainCategoryDataModel;
import com.fardiat.models.MessageDataModel;
import com.fardiat.models.MessageModel;
import com.fardiat.models.NotificationCount;
import com.fardiat.models.NotificationDataModel;
import com.fardiat.models.OrderDataModel;
import com.fardiat.models.OrderModel;
import com.fardiat.models.PlaceGeocodeData;
import com.fardiat.models.PlaceMapDetailsData;
import com.fardiat.models.ProductDataModel;
import com.fardiat.models.ProductModel;
import com.fardiat.models.RoomIdModel;
import com.fardiat.models.RoomModel;
import com.fardiat.models.SettingModel;
import com.fardiat.models.Slider_Model;
import com.fardiat.models.UserModel;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface Service {


    @GET("place/findplacefromtext/json")
    Call<PlaceMapDetailsData> searchOnMap(@Query(value = "inputtype") String inputtype,
                                          @Query(value = "input") String input,
                                          @Query(value = "fields") String fields,
                                          @Query(value = "language") String language,
                                          @Query(value = "key") String key
    );

    @GET("geocode/json")
    Call<PlaceGeocodeData> getGeoData(@Query(value = "latlng") String latlng,
                                      @Query(value = "language") String language,
                                      @Query(value = "key") String key);


    @FormUrlEncoded
    @POST("api/login")
    Call<UserModel> login(@Field("phone_code") String phone_code,
                          @Field("phone") String phone

    );

    @FormUrlEncoded
    @POST("api/logout")
    Call<ResponseBody> logout(@Header("Authorization") String user_token,
                              @Field("phone_token") String phone_token,
                              @Field("soft_type") String soft_type


    );

    @FormUrlEncoded
    @POST("api/update-firebase")
    Call<ResponseBody> updatePhoneToken(@Header("Authorization") String Authorization,
                                        @Field("phone_token") String phone_token,
                                        @Field("soft_type") String soft_type
    );

    @FormUrlEncoded
    @POST("api/register")
    Call<UserModel> signUpWithoutImage(@Field("name") String name,
                                       @Field("phone_code") String phone_code,
                                       @Field("phone") String phone,
                                       @Field("email") String email
    );

    @Multipart
    @POST("api/register")
    Call<UserModel> signUpWithImage(@Part("name") RequestBody name,
                                    @Part("phone_code") RequestBody phone_code,
                                    @Part("phone") RequestBody phone,
                                    @Part MultipartBody.Part logo


    );

    @GET("api/sttings")
    Call<SettingModel> getSetting(@Header("lang") String lang

    );


    @GET("api/slider")
    Call<Slider_Model> get_slider();


   /*

    @GET("api/get-most-sale")
    Call<ProductDataModel> getMostSeller(@Query("pagination") String pagination);


    @GET("api/get-box")
    Call<ProductDataModel> getFamilyBoxes(@Query("pagination") String pagination);

    @GET("api/category-product")
    Call<CategoryProductDataModel> getCategoryProducts(@Query("pagination") String pagination,
                                                       @Query("user_id") int user_id);

    @GET("api/genaral-search")
    Call<ProductDataModel> Search(@Query("pagination") String pagination,
                                  @Query("user_id") int user_id,
                                  @Query("search_name") String search_name,
                                  @Query("have_offer") String have_offer,
                                  @Query("product_type") String product_type,

                                  @Query("departemnt_id") String departemnt_id
    );

    @GET("api/products-by-dep")
    Call<ProductDataModel> getOffersProducts(@Query("pagination") String pagination,
                                             @Query("departemnt_id") String departemnt_id,
                                             @Query("user_id") String user_id,
                                             @Query("page") int page
    );*/

    @GET("api/product")
    Call<ProductModel> Product_detials(@Query("product_id") String product_id);

    @GET("api/one-order")
    Call<OrderModel> order_detials(@Query("order_id") int order_id);

   /* @GET("api/brands")
    Call<MainCategoryDataModel> getBrands(
            @Query("pagination") String pagination
    );

    @GET("api/category")
    Call<MainCategoryDataModel> getCategory(
            @Query("pagination") String pagination
    );*/

    @GET("api/banks")
    Call<BankDataModel> getBanks();

    @FormUrlEncoded
    @POST("api/deleteProduct")
    Call<ResponseBody> deleteAds(@Field("user_id") int user_id,
                                 @Field("product_id") int product_id);

    @FormUrlEncoded
    @POST("api/addReport")
    Call<ResponseBody> addReport(@Field("user_id") int user_id,
                                 @Field("product_id") int product_id
    );

    @FormUrlEncoded
    @POST("api/favorite-action")
    Call<ResponseBody> favoriteAction(@Header("Authorization") String Authorization,
                                      @Field("product_id") int product_id);

    @GET("api/my-favorites")
    Call<FavoriteDataModel> getMyFavoriteProducts(@Header("Authorization") String Authorization);

    @FormUrlEncoded
    @POST("api/myProducts")
    Call<ProductDataModel> getMyAds(@Field("user_id") int user_id,
                                    @Field("pagination") String pagination,
                                    @Field("limit_per_page") int limit_per_page);


    @GET("api/my-notification")
    Call<NotificationDataModel> getNotification(@Header("Authorization") String user_token


    );

    @FormUrlEncoded
    @POST("api/delete-notification")
    Call<ResponseBody> deleteNotification(@Header("Authorization") String user_token,
                                          @Field("notification_id") int notification_id
    );

    @GET("api/count-unread")
    Call<NotificationCount> getUnreadNotificationCount(@Header("Authorization") String user_token
    );

    @GET("api/my-orders")
    Call<OrderDataModel> getOrders(@Header("Authorization") String user_token,
                                   @Query("user_id") String user_id,
                                   @Query("pagination") String pagination,
                                   @Query("page") int page,
                                   @Query("limit_per_page") int limit_per_page

    );

    @POST("api/create-order")
    Call<OrderModel> createOrder(
            @Header("Authorization") String Authorization,
            @Body AddOrderModel addOrderModel)
            ;

    @FormUrlEncoded
    @POST("api/find-coupon")
    Call<SettingModel> getCouponValue(@Field("coupon_num") String coupon_num);

    @Multipart
    @POST("api/update-profile")
    Call<UserModel> editClientProfileWithImage(@Header("Authorization") String Authorization,
                                               @Part("name") RequestBody name,
                                               @Part MultipartBody.Part logo

    );

    @Multipart
    @POST("api/update-profile")
    Call<UserModel> editClientProfileWithoutImage(@Header("Authorization") String Authorization,
                                                  @Part("name") RequestBody name
    );

    @FormUrlEncoded
    @POST("api/add-rate")
    Call<ResponseBody> rate(
            @Header("Authorization") String Authorization,
            @Field("product_id") String product_id,
            @Field("user_id") String user_id,
            @Field("rate") String rate
    );

    @FormUrlEncoded
    @POST("api/send-partner")
    Call<ResponseBody> bepartner(
            @Field("name") String name,
            @Field("phone") String phone,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("address") String address
    );


    @GET("api/category-product")
    Call<MainCategoryDataModel> getMainCategory_Products();

    @GET("api/search-product")
    Call<ProductDataModel> search(@Query("user_id") String user_id,
                                  @Query("pagination") String pagination,
                                  @Query("page") int page,
                                  @Query("search_name") String search_name

    );

    @FormUrlEncoded
    @POST("api/getSubDepartmentsByBasicDepartmentId")
    Call<ItemAddAdsDataModel> getItemsAds(@Field("department_id") int department_id,
                                          @Field("pagination") String pagination,
                                          @Field("limit_per_page") int limit_per_page

    );

    @Multipart
    @POST("api/addProduct")
    Call<ResponseBody> addAdsWithVideoWithList(@Part("title") RequestBody title,
                                               @Part("departemnt_id") RequestBody departemnt_id,
                                               @Part("price") RequestBody price,
                                               @Part("user_id") RequestBody user_id,
                                               @Part("details") RequestBody details,
                                               @Part("address") RequestBody address,
                                               @Part("google_lat") RequestBody google_lat,
                                               @Part("google_long") RequestBody google_long,
                                               @Part MultipartBody.Part vedio,
                                               @Part List<MultipartBody.Part> images,
                                               @PartMap() Map<String, RequestBody> map
    );


    @Multipart
    @POST("api/addProduct")
    Call<ResponseBody> addAdsWithoutVideoWithoutList(@Part("title") RequestBody title,
                                                     @Part("departemnt_id") RequestBody departemnt_id,
                                                     @Part("price") RequestBody price,
                                                     @Part("user_id") RequestBody user_id,
                                                     @Part("details") RequestBody details,
                                                     @Part("address") RequestBody address,
                                                     @Part("google_lat") RequestBody google_lat,
                                                     @Part("google_long") RequestBody google_long,
                                                     @Part List<MultipartBody.Part> images
    );


    @Multipart
    @POST("api/addProduct")
    Call<ResponseBody> addAdsWithoutVideoWithList(@Part("title") RequestBody title,
                                                  @Part("departemnt_id") RequestBody departemnt_id,
                                                  @Part("price") RequestBody price,
                                                  @Part("user_id") RequestBody user_id,
                                                  @Part("details") RequestBody details,
                                                  @Part("address") RequestBody address,
                                                  @Part("google_lat") RequestBody google_lat,
                                                  @Part("google_long") RequestBody google_long,
                                                  @Part List<MultipartBody.Part> image,
                                                  @PartMap() Map<String, RequestBody> map

    );


    @Multipart
    @POST("api/addProduct")
    Call<ResponseBody> addAdsWithVideoWithoutList(@Part("title") RequestBody title,
                                                  @Part("departemnt_id") RequestBody departemnt_id,
                                                  @Part("price") RequestBody price,
                                                  @Part("user_id") RequestBody user_id,
                                                  @Part("details") RequestBody details,
                                                  @Part("address") RequestBody address,
                                                  @Part("google_lat") RequestBody google_lat,
                                                  @Part("google_long") RequestBody google_long,
                                                  @Part List<MultipartBody.Part> image,
                                                  @Part MultipartBody.Part vedio
    );

    @GET("api/product")
    Call<ProductModel> getProductById(@Query("user_id") String user_id,
                                      @Query("product_id") int product_id

    );


    @FormUrlEncoded
    @POST("api/favorite-action")
    Call<ResponseBody> addFavoriteProduct(@Header("Authorization") String Authorization,
                                          @Field("product_id") String product_id);

    @FormUrlEncoded
    @POST("api/single-chat-room")
    Call<MessageDataModel> getChatMessages(@Header("Authorization") String token,
                                           @Field("user_id") int user_id,
                                           @Field("page") int page


    );

    @FormUrlEncoded
    @POST("api/message/send")
    Call<MessageModel> sendChatMessage(@Header("Authorization") String bearer_token,
                                       @Field("room_id") int room_id,
                                       @Field("from_user_id") int from_user_id,
                                       @Field("to_user_id") int to_user_id,
                                       @Field("message_kind") String message_kind,
                                       @Field("date") long date,
                                       @Field("message") String message


    );

    @Multipart
    @POST("api/message/send")
    Call<MessageModel> sendChatAttachment(@Header("Authorization") String bearer_token,
                                          @Part("room_id") RequestBody room_id,
                                          @Part("from_user_id") RequestBody from_user_id,
                                          @Part("to_user_id") RequestBody to_user_id,
                                          @Part("message_kind") RequestBody message_kind,
                                          @Part("date") RequestBody date,
                                          @Part MultipartBody.Part attachment
    );

    @FormUrlEncoded
    @POST("api/chatRoom/get")
    Call<RoomIdModel> createRoom(@Header("Authorization") String user_token,
                                 @Field("from_user_id") int from_user_id,
                                 @Field("to_user_id") int to_user_id
    );

    @GET("api/product")
    Call<ProductDataModel> getProductByUserId(@Query("my_user_id") int my_user_id,
                                              @Query("other_user_id") int product_id

    );

}