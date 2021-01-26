package com.fardiat.activities_fragments.activity_images;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fardiat.R;
import com.fardiat.databinding.ActivityImageBinding;
import com.fardiat.interfaces.Listeners;
import com.fardiat.language.Language;
import com.fardiat.models.ProductModel;
import com.fardiat.models.UserModel;
import com.fardiat.preferences.Preferences;
import com.fardiat.singleton.CartSingleton;

import java.util.Locale;

import io.paperdb.Paper;


public class ImagesActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityImageBinding binding;
    private String lang;
    private ProductModel productDataModel;
    private Preferences preferences;

    private UserModel userModel;
    private CartSingleton cartSingleton;
    private ProductModel productModel;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image);
        getDataFromIntent();
        initView();

    }


    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            productModel = (ProductModel) intent.getSerializableExtra("data");

        }

        //    product_id=1;
    }


    private void initView() {
       // productsImagesList = new ArrayList<>();
        Paper.init(this);
        cartSingleton = CartSingleton.newInstance();
        preferences = Preferences.getInstance();
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        //productImageAdapter = new ProductImageAdapter(productsImagesList, this);
        binding.recimage.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        binding.setModel(productDataModel);
        binding.progBarSlider.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        UPDATEUI(productModel);


    }


    private void UPDATEUI(ProductModel body) {

        binding.setModel(body);
        this.productModel = body;
        binding.progBarSlider.setVisibility(View.GONE);
    }


    @Override
    public void back() {
        finish();
    }


    @Override
    public void onBackPressed() {
        back();
    }


    public void showimage(int layoutPosition) {
        binding.pager.setCurrentItem(layoutPosition);
    }
}
