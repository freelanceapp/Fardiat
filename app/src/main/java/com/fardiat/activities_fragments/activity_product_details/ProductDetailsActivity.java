package com.fardiat.activities_fragments.activity_product_details;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fardiat.activities_fragments.activity_chat.ChatActivity;
import com.fardiat.activities_fragments.activity_profile_products.ProfileProductsActivity;
import com.fardiat.models.ChatUserModel;
import com.fardiat.models.RoomIdModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.fardiat.R;
import com.fardiat.activities_fragments.activity_images.ImagesActivity;
import com.fardiat.activities_fragments.activity_search.SearchActivity;
import com.fardiat.adapters.ProductDetailsAdapter;
import com.fardiat.adapters.ProductDetialsSlidingImage_Adapter;
import com.fardiat.adapters.SliderAdapter;
import com.fardiat.databinding.ActivityProductDetailsBinding;
import com.fardiat.interfaces.Listeners;
import com.fardiat.language.Language;
import com.fardiat.models.ProductDataModel;
import com.fardiat.models.ProductDetailsModel;
import com.fardiat.models.ProductImageModel;
import com.fardiat.models.ProductModel;
import com.fardiat.models.UserModel;
import com.fardiat.preferences.Preferences;
import com.fardiat.remote.Api;
import com.fardiat.share.Common;
import com.fardiat.singleton.CartSingleton;
import com.fardiat.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityProductDetailsBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private int product_id;
    private ProductModel productModel;
    private ProductDetailsAdapter adapter;
    private List<ProductDetailsModel> productDetailsModelList;
    private SliderAdapter sliderAdapter;
    private List<ProductImageModel> productImageModelList;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);
        getDataFromIntent();

        initView();

    }


    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            product_id = intent.getIntExtra("product_id", 0);

        }

    }


    private void initView() {


        productImageModelList = new ArrayList<>();
        productDetailsModelList = new ArrayList<>();
        Paper.init(this);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setUserModel(userModel);
        binding.tab.setupWithViewPager(binding.pager);
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductDetailsAdapter(productDetailsModelList, this);
        binding.recView.setAdapter(adapter);

        sliderAdapter = new SliderAdapter(productImageModelList, this);
        binding.pager.setAdapter(sliderAdapter);

        binding.checkFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like_dislike();
            }
        });

        binding.iconCopy.setOnClickListener(view -> {

            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", productModel.getUser().getPhone_code() + productModel.getUser().getPhone());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.copied, Toast.LENGTH_SHORT).show();
        });

        binding.flCall.setOnClickListener(view -> {
            String phone = productModel.getUser().getPhone_code() + productModel.getUser().getPhone();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            startActivity(intent);
        });

        binding.cons.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileProductsActivity.class);
            intent.putExtra("data",productModel.getUser());
            startActivity(intent);
        });

        binding.iconWhatsApp.setOnClickListener(view -> {
            String phone = productModel.getUser().getPhone_code() + productModel.getUser().getPhone();
            String url = "https://api.whatsapp.com/send?phone=" + phone;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
        binding.imgWarning.setOnClickListener(view -> addReport());
        binding.flCall.setOnClickListener(view -> createChat());
        getProductById();
    }

    private void createChat() {
        try {
            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Api.getService(Tags.base_url)
                    .createRoom(userModel.getUser().getToken(), userModel.getUser().getId(), productModel.getUser().getId())
                    .enqueue(new Callback<RoomIdModel>() {
                        @Override
                        public void onResponse(Call<RoomIdModel> call, Response<RoomIdModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()) {

                                if (response.body() != null) {
                                    navigateToChatActivity(response.body());
                                } else {
                                    Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }

                            } else {
                                dialog.dismiss();
                                if (response.code() == 500) {
                                    Toast.makeText(ProductDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<RoomIdModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(ProductDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ProductDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    private void navigateToChatActivity(RoomIdModel roomIdModel) {
        ChatUserModel chatUserModel = new ChatUserModel(productModel.getUser().getId(), productModel.getUser().getName(), productModel.getUser().getLogo(), roomIdModel.getRoom_id());
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("data", chatUserModel);
        startActivity(intent);
    }


    private void getProductById() {

        try {
            binding.scrollView.setVisibility(View.GONE);
            binding.progBar.setVisibility(View.VISIBLE);
            String user_id = "";
            if (userModel != null) {
                user_id = String.valueOf(userModel.getUser().getId());
            }

            Api.getService(Tags.base_url)
                    .getProductById(user_id, product_id)
                    .enqueue(new Callback<ProductModel>() {
                        @Override
                        public void onResponse(Call<ProductModel> call, Response<ProductModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null) {
                                productModel = response.body();
                                if (productModel != null) {
                                    if (productModel.getBlock_check().equals("yes")) {
                                        binding.imgWarning.setColorFilter(ContextCompat.getColor(ProductDetailsActivity.this, R.color.colorPrimary));
                                    } else {
                                        binding.imgWarning.setColorFilter(ContextCompat.getColor(ProductDetailsActivity.this, R.color.gray4));

                                    }
                                }
                                binding.setModel(productModel);

                                if (productModel.getProduct_details() != null && productModel.getProduct_details().size() > 0) {

                                    productDetailsModelList.addAll(productModel.getProduct_details());

                                    adapter.notifyDataSetChanged();
                                }
                                if (productModel.getUser_like() != null) {
                                    binding.checkFavorite.setChecked(true);

                                }
/*
                                if (productModel.getVedio() != null) {
                                    productImageModelList.add(new ProductImageModel(0, productModel.getVedio(), "video"));
                                }*/
                                if (productModel.getProducts_images() != null && productModel.getProducts_images().size() > 0) {
                                    binding.flNoSlider.setVisibility(View.GONE);
                                    binding.flSlider.setVisibility(View.VISIBLE);
                                    productImageModelList.addAll(productModel.getProducts_images());

                                    sliderAdapter.notifyDataSetChanged();
                                } else {
                                    binding.flNoSlider.setVisibility(View.VISIBLE);
                                    binding.flSlider.setVisibility(View.GONE);
                                }
                                try {
                                    if (productModel.getUser_like() != null) {
                                        binding.checkFavorite.setChecked(true);
                                    }
                                } catch (Exception e) {

                                    if (productModel.getUser_like() != null) {
                                        binding.checkFavorite.setChecked(true);

                                    }

                                    if (productModel.getVedio() != null) {
                                        productImageModelList.add(new ProductImageModel(0, productModel.getVedio(), "video"));
                                    }
                                    if (productModel.getProducts_images() != null && productModel.getProducts_images().size() > 0) {
                                        binding.flNoSlider.setVisibility(View.GONE);
                                        binding.flSlider.setVisibility(View.VISIBLE);
                                        productImageModelList.addAll(productModel.getProducts_images());

                                        sliderAdapter.notifyDataSetChanged();
                                    } else {
                                        binding.flNoSlider.setVisibility(View.VISIBLE);
                                        binding.flSlider.setVisibility(View.GONE);
                                    }

                                }


                                binding.scrollView.setVisibility(View.VISIBLE);
                            } else {
                                binding.progBar.setVisibility(View.GONE);

                                if (response.code() == 500) {
                                    Toast.makeText(ProductDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ProductModel> call, Throwable t) {
                            try {
                                binding.progBar.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(ProductDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ProductDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    // عليا النعمة انا مشتغلت عليها (عماد مجدي)
    public int like_dislike() {

        if (userModel != null) {
            try {
                Log.e("llll", userModel.getUser().getToken());

                Api.getService(Tags.base_url)
                        .addFavoriteProduct(userModel.getUser().getToken(), product_id + "")
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    initView();

                                } else {


                                    if (response.code() == 500) {
                                        Toast.makeText(ProductDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                    } else {
                                        Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                        try {

                                            Log.e("error", response.code() + "_" + response.errorBody().string());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                try {

                                    if (t.getMessage() != null) {
                                        Log.e("error", t.getMessage());
                                        if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                            Toast.makeText(ProductDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ProductDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                } catch (Exception e) {
                                }
                            }
                        });
            } catch (Exception e) {
            }
            return 1;
        } else {
            Common.CreateDialogAlert(ProductDetailsActivity.this, getString(R.string.please_sign_in_or_sign_up));
            return 0;

        }
    }

    public int addReport() {
        if (userModel != null) {
            try {
                ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
                Api.getService(Tags.base_url)
                        .addReport(userModel.getUser().getId(), productModel.getId())
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                dialog.dismiss();
                                if (response.isSuccessful()) {
                                    getProductById();
                                    //Common.CreateDialogAlert(ProductDetailsActivity.this, getString(R.string.rep));
                                } else {

                                    dialog.dismiss();
                                    if (response.code() == 500) {
                                        Toast.makeText(ProductDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                    } else {
                                        Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                        try {

                                            Log.e("error", response.code() + "_" + response.errorBody().string());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                try {
                                    dialog.dismiss();
                                    if (t.getMessage() != null) {
                                        Log.e("error", t.getMessage());
                                        if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                            Toast.makeText(ProductDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ProductDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                } catch (Exception e) {
                                }
                            }
                        });
            } catch (Exception e) {
            }
            return 1;
        } else {
            Common.CreateDialogAlert(ProductDetailsActivity.this, getString(R.string.please_sign_in_or_sign_up));
            return 0;

        }
    }


    @Override
    public void back() {
        finish();
    }


    @Override
    public void onBackPressed() {
        back();
    }


}
