package com.fardiat.activities_fragments.activity_home.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fardiat.R;
import com.fardiat.activities_fragments.activity_home.HomeActivity;
import com.fardiat.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.fardiat.adapters.CategoryAdapter;

import com.fardiat.adapters.ProductAdapter;
import com.fardiat.databinding.FragmentMainBinding;
import com.fardiat.models.MainCategoryDataModel;
import com.fardiat.models.MainCategoryModel;
import com.fardiat.models.ProductModel;
import com.fardiat.models.UserModel;
import com.fardiat.preferences.Preferences;
import com.fardiat.remote.Api;
import com.fardiat.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Main extends Fragment {

    private HomeActivity activity;
    private FragmentMainBinding binding;
    private Preferences preferences;
    private String lang;
    private UserModel userModel;
    private List<MainCategoryModel> categoryModelList;
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private LinearLayoutManager manager;
    private List<ProductModel> productModelList;


    public static Fragment_Main newInstance() {
        return new Fragment_Main();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initView() {

        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        categoryModelList = new ArrayList<>();
        productModelList = new ArrayList<>();
        manager = new LinearLayoutManager(activity);
        categoryAdapter = new CategoryAdapter(categoryModelList, this, activity);
        binding.recViewCategory.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));

        binding.recViewCategory.setAdapter(categoryAdapter);

        productAdapter = new ProductAdapter(productModelList,activity,this);
        binding.recView.setLayoutManager(manager);
        binding.recView.setAdapter(productAdapter);


        getMainCategory();
    }

    @Override
    public void onResume() {
        getMainCategory();
        super.onResume();
    }

    public void getMainCategory() {
        Api.getService(Tags.base_url)
                .getMainCategory_Products()
                .enqueue(new Callback<MainCategoryDataModel>() {
                    @Override
                    public void onResponse(Call<MainCategoryDataModel> call, Response<MainCategoryDataModel> response) {
                        binding.progBarCategory.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            categoryModelList.clear();
                            categoryModelList.addAll(response.body().getData());

                            if (categoryModelList.size() > 0) {
                                binding.tvNoData.setVisibility(View.GONE);
                                MainCategoryModel model = categoryModelList.get(0);
                                model.setSelected(true);
                                categoryModelList.set(0,model);
                                categoryAdapter.notifyDataSetChanged();

                                setItemData(categoryModelList.get(0));
                            } else {
                                binding.tvNoData.setVisibility(View.VISIBLE);

                            }


                        } else {
                            binding.progBarCategory.setVisibility(View.GONE);

                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MainCategoryDataModel> call, Throwable t) {
                        try {
                            binding.progBarCategory.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }



    public void setItemData(MainCategoryModel mainCategoryModel) {
        productModelList.clear();
        productAdapter.notifyDataSetChanged();
        productModelList.addAll(mainCategoryModel.getProducts());
        productAdapter.notifyDataSetChanged();
        if (productModelList.size()>0){
            binding.llNoData.setVisibility(View.GONE);
        }else {
            binding.llNoData.setVisibility(View.VISIBLE);

        }

    }


    public void setProductItemData(ProductModel productModel) {

        Intent intent = new Intent(activity,ProductDetailsActivity.class);
        intent.putExtra("product_id",productModel.getId());

        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



    }



}
