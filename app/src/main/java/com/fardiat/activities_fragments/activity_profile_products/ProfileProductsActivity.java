package com.fardiat.activities_fragments.activity_profile_products;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fardiat.R;
import com.fardiat.activities_fragments.activity_my_ads.MyAdsActivity;
import com.fardiat.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.fardiat.adapters.MyAdsAdapter;
import com.fardiat.adapters.ProductAdapter2;
import com.fardiat.databinding.ActivityMyAdsBinding;
import com.fardiat.databinding.ActivityProfileProductsBinding;
import com.fardiat.language.Language;
import com.fardiat.models.ProductDataModel;
import com.fardiat.models.ProductModel;
import com.fardiat.models.UserModel;
import com.fardiat.preferences.Preferences;
import com.fardiat.remote.Api;
import com.fardiat.share.Common;
import com.fardiat.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileProductsActivity extends AppCompatActivity {
    private ActivityProfileProductsBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private String lang;
    private ProductAdapter2 adapter;
    private List<ProductModel> productModelList;
    private UserModel.User user;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_products);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        user = (UserModel.User) intent.getSerializableExtra("data");
    }


    private void initView() {
        productModelList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        binding.setModel(user);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        adapter = new ProductAdapter2(productModelList, this);
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        binding.recView.setAdapter(adapter);

        String last4Number = user.getPhone().substring(user.getPhone().length()-4);
        String stars ="";
        for (int index=0;index<user.getPhone().length()-4;index++){
            stars +="*";
        }

        String phone2 = stars+last4Number;
        binding.tvPhone.setText(phone2);

        binding.llBack.setOnClickListener(view -> finish());
        binding.iconCopy.setOnClickListener(view -> {
            String phone = user.getPhone_code() + user.getPhone();

            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", phone);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.copied, Toast.LENGTH_SHORT).show();
        });

        binding.flCall.setOnClickListener(view -> {
            String phone = user.getPhone_code() + user.getPhone();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            startActivity(intent);
        });


        binding.iconWhatsApp.setOnClickListener(view -> {
            String phone = user.getPhone_code() + user.getPhone();
            String url = "https://api.whatsapp.com/send?phone=" + phone;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
        getUserData();
    }

    private void getUserData()
    {
        String user_id ="";
        if (userModel!=null){
            user_id = String.valueOf(userModel.getUser().getId());
        }
        Api.getService(Tags.base_url)
                .getProductByUserId(user_id, user.getId())
                .enqueue(new Callback<ProductDataModel>() {
                    @Override
                    public void onResponse(Call<ProductDataModel> call, Response<ProductDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            productModelList.clear();
                            productModelList.addAll(response.body().getData());


                            if (productModelList.size() > 0) {

                                 adapter.notifyDataSetChanged();

                                binding.tvNoData.setVisibility(View.GONE);
                            } else {
                                binding.tvNoData.setVisibility(View.VISIBLE);

                            }
                        } else {
                            if (response.code() == 500) {
                                Toast.makeText(ProfileProductsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(ProfileProductsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                try {

                                    Log.e("error", response.code() + "_" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(ProfileProductsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ProfileProductsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });

    }

    public void setProductItemData(ProductModel productModel) {
        Intent intent = new Intent(ProfileProductsActivity.this, ProductDetailsActivity.class);
        intent.putExtra("product_id", productModel.getId());
        startActivity(intent);
    }


}