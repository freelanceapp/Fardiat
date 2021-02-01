package com.fardiat.activities_fragments.activity_home;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.fardiat.R;
import com.fardiat.activities_fragments.activity_add_ads.AddAdsActivity;
import com.fardiat.activities_fragments.activity_home.fragments.Fragment_Favorite;
import com.fardiat.activities_fragments.activity_home.fragments.Fragment_Main;
import com.fardiat.activities_fragments.activity_home.fragments.Fragment_Settings;
import com.fardiat.activities_fragments.activity_home.fragments.Fragment_Profile;
import com.fardiat.activities_fragments.activity_login.LoginActivity;
import com.fardiat.activities_fragments.activity_notification.NotificationActivity;
import com.fardiat.activities_fragments.activity_search.SearchActivity;
import com.fardiat.databinding.ActivityHomeBinding;
import com.fardiat.language.Language;
import com.fardiat.models.NotFireModel;
import com.fardiat.models.NotificationCount;
import com.fardiat.models.UserModel;
import com.fardiat.preferences.Preferences;
import com.fardiat.remote.Api;
import com.fardiat.share.Common;
import com.fardiat.tags.Tags;
import com.google.firebase.iid.FirebaseInstanceId;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private Preferences preferences;
    private FragmentManager fragmentManager;
    private Fragment_Main fragment_main;
    private Fragment_Profile fragment_profile;
    private Fragment_Settings fragment_settings;
    private Fragment_Favorite fragment_favorite;
    private UserModel userModel;
    private String lang;
    private String token;

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        initView();


    }

    private void initView() {
        fragmentManager = getSupportFragmentManager();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);


        binding.flNotification.setOnClickListener(view -> {


            if (userModel != null) {
                readNotificationCount();
                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                startActivity(intent);

            } else {
                Common.CreateDialogAlert(this, getString(R.string.please_sign_in_or_sign_up));
            }

        });

        binding.search.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(intent);

        });



        binding.flHome.setOnClickListener(v -> {
            displayFragmentMain();
        });

        binding.flFavorite.setOnClickListener(v -> {
            if (userModel!=null){
                displayFragmentFavorite();

            }else {
                Common.CreateDialogAlert(this,getString(R.string.please_sign_in_or_sign_up));
            }
        });


        binding.flProfile.setOnClickListener(v -> {
            if (userModel!=null){
                displayFragmentProfile();

            }else {
                Common.CreateDialogAlert(this,getString(R.string.please_sign_in_or_sign_up));
            }
        });

        binding.flSettings.setOnClickListener(v -> {
            displayFragmentSettings();
        });

        displayFragmentMain();

        if (userModel != null) {
            EventBus.getDefault().register(this);
            getNotificationCount();
            updateTokenFireBase();

        }
        binding.fab.setOnClickListener(view -> {
            if (userModel!=null){

            Intent intent = new Intent(this, AddAdsActivity.class);
            startActivity(intent);
            }else {
                Common.CreateDialogAlert(this,getString(R.string.please_sign_in_or_sign_up));
            }

        });

    }

    private void getNotificationCount() {
        Api.getService(Tags.base_url)
                .getUnreadNotificationCount(userModel.getUser().getToken())
                .enqueue(new Callback<NotificationCount>() {
                    @Override
                    public void onResponse(Call<NotificationCount> call, Response<NotificationCount> response) {
                        if (response.isSuccessful()) {
                            binding.setNotCount(response.body().getCount());
                        } else {
                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationCount> call, Throwable t) {
                        try {
                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(HomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    private void readNotificationCount() {
        binding.setNotCount(0);
    }


    public void displayFragmentMain() {
        try {
            updateHomUi();
            if (fragment_main == null) {
                fragment_main = Fragment_Main.newInstance();
                fragment_main.getMainCategory();
            }



            if (fragment_favorite != null && fragment_favorite.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_favorite).commit();
            }
            if (fragment_profile != null && fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_profile).commit();
            }

            if (fragment_settings != null && fragment_settings.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_settings).commit();
            }
            if (fragment_main.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_main).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_main, "fragment_main").commit();

            }
            binding.setTitle(getString(R.string.home));
        } catch (Exception e) {
        }

    }

    public void displayFragmentFavorite() {

        try {

            updateFavoriteUi();
            if (fragment_favorite == null) {
                fragment_favorite = Fragment_Favorite.newInstance();
                fragment_favorite.getFavorite();
            }


            if (fragment_main != null && fragment_main.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_main).commit();
            }
            if (fragment_settings != null && fragment_settings.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_settings).commit();
            }

            if (fragment_profile != null && fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_profile).commit();
            }


            if (fragment_favorite.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_favorite).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_favorite, "fragment_favorite").commit();

            }
            binding.setTitle(getString(R.string.department));
        } catch (Exception e) {
        }
    }


    public void displayFragmentProfile() {

        try {
            updateProfileUi();
            if (fragment_profile == null) {
                fragment_profile = Fragment_Profile.newInstance();
            }


            if (fragment_main != null && fragment_main.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_main).commit();
            }
            if (fragment_favorite != null && fragment_favorite.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_favorite).commit();
            }

            if (fragment_settings != null && fragment_settings.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_settings).commit();
            }



            if (fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_profile).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_profile, "fragment_profile").commit();

            }
            binding.setTitle(getString(R.string.profile));
        } catch (Exception e) {
        }
    }

    public void displayFragmentSettings() {

        try {
            updateSettingUi();
            if (fragment_settings == null) {
                fragment_settings = Fragment_Settings.newInstance();
            }


            if (fragment_main != null && fragment_main.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_main).commit();
            }
            if (fragment_favorite != null && fragment_favorite.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_favorite).commit();
            }

            if (fragment_profile != null && fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_profile).commit();
            }



            if (fragment_settings.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_settings).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_settings, "fragment_profile").addToBackStack("fragment_profile").commit();

            }
            binding.setTitle(getString(R.string.more));
        } catch (Exception e) {
        }
    }


    private void updateHomUi() {

        binding.flFavorite.setBackgroundResource(R.drawable.small_rounded_btn_primary);
        binding.iconSearch.setColorFilter(ContextCompat.getColor(this, R.color.white));
        binding.tvSearch.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvSearch.setVisibility(View.GONE);

        binding.flHome.setBackgroundResource(0);
        binding.iconHome.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
        binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        binding.tvHome.setVisibility(View.VISIBLE);

        binding.flProfile.setBackgroundResource(R.drawable.small_rounded_btn_primary);
        binding.iconProfile.setColorFilter(ContextCompat.getColor(this, R.color.white));
        binding.tvProfile.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvProfile.setVisibility(View.GONE);

        binding.flSettings.setBackgroundResource(R.drawable.small_rounded_btn_primary);
        binding.iconSettings.setColorFilter(ContextCompat.getColor(this, R.color.white));
        binding.tvSettings.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvSettings.setVisibility(View.GONE);


    }

    private void updateFavoriteUi() {

        binding.flFavorite.setBackgroundResource(0);
        binding.iconSearch.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
        binding.tvSearch.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        binding.tvSearch.setVisibility(View.VISIBLE);

        binding.flHome.setBackgroundResource(R.drawable.small_rounded_btn_primary);
        binding.iconHome.setColorFilter(ContextCompat.getColor(this, R.color.white));
        binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvHome.setVisibility(View.GONE);


        binding.flProfile.setBackgroundResource(R.drawable.small_rounded_btn_primary);
        binding.iconProfile.setColorFilter(ContextCompat.getColor(this, R.color.white));
        binding.tvProfile.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvProfile.setVisibility(View.GONE);

        binding.flSettings.setBackgroundResource(R.drawable.small_rounded_btn_primary);
        binding.iconSettings.setColorFilter(ContextCompat.getColor(this, R.color.white));
        binding.tvSettings.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvSettings.setVisibility(View.GONE);

    }



    private void updateProfileUi() {

        binding.flHome.setBackgroundResource(R.drawable.small_rounded_btn_primary);
        binding.iconHome.setColorFilter(ContextCompat.getColor(this, R.color.white));
        binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvHome.setVisibility(View.GONE);


        binding.flFavorite.setBackgroundResource(R.drawable.small_rounded_btn_primary);
        binding.iconSearch.setColorFilter(ContextCompat.getColor(this, R.color.white));
        binding.tvSearch.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvSearch.setVisibility(View.GONE);

        binding.flSettings.setBackgroundResource(R.drawable.small_rounded_btn_primary);
        binding.iconSettings.setColorFilter(ContextCompat.getColor(this, R.color.white));
        binding.tvSettings.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvSettings.setVisibility(View.GONE);

        binding.flProfile.setBackgroundResource(0);
        binding.iconProfile.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
        binding.tvProfile.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        binding.tvProfile.setVisibility(View.VISIBLE);

    }

    private void updateSettingUi() {

        binding.flHome.setBackgroundResource(R.drawable.small_rounded_btn_primary);
        binding.iconHome.setColorFilter(ContextCompat.getColor(this, R.color.white));
        binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvHome.setVisibility(View.GONE);


        binding.flFavorite.setBackgroundResource(R.drawable.small_rounded_btn_primary);
        binding.iconSearch.setColorFilter(ContextCompat.getColor(this, R.color.white));
        binding.tvSearch.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvSearch.setVisibility(View.GONE);


        binding.flSettings.setBackgroundResource(0);
        binding.iconSettings.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
        binding.tvSettings.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        binding.tvSettings.setVisibility(View.VISIBLE);

        binding.flProfile.setBackgroundResource(R.drawable.small_rounded_btn_primary);
        binding.iconProfile.setColorFilter(ContextCompat.getColor(this, R.color.white));
        binding.tvProfile.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvProfile.setVisibility(View.GONE);

    }

    private void updateTokenFireBase() {

        FirebaseInstanceId.getInstance()
                .getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                token = task.getResult().getToken();

                try {
                    Api.getService(Tags.base_url)
                            .updatePhoneToken(userModel.getUser().getToken(), token, "android")
                            .enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Log.e("token", "updated successfully");
                                    } else {
                                        try {

                                            Log.e("errorToken", response.code() + "_" + response.errorBody().string());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    try {

                                        if (t.getMessage() != null) {
                                            Log.e("errorToken2", t.getMessage());
                                            if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                                Toast.makeText(HomeActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    } catch (Exception e) {
                                    }
                                }
                            });
                } catch (Exception e) {


                }

            }
        });
    }

    public void logout() {
        if (userModel != null) {


            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.show();


            FirebaseInstanceId.getInstance()
                    .getInstanceId().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    token = task.getResult().getToken();

                    Api.getService(Tags.base_url)
                            .logout(userModel.getUser().getToken(), token, "android")
                            .enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    dialog.dismiss();
                                    if (response.isSuccessful()) {
                                        Log.e("dd", "ddd");
                                        preferences.clear(HomeActivity.this);
                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        if (manager != null) {
                                            manager.cancel(Tags.not_tag, Tags.not_id);
                                        }
                                        navigateToSignInActivity();


                                    } else {
                                        dialog.dismiss();
                                        try {
                                            Log.e("error", response.code() + "__" + response.errorBody().string());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        if (response.code() == 500) {
                                            Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    try {
                                        dialog.dismiss();
                                        if (t.getMessage() != null) {
                                            Log.e("error", t.getMessage() + "__");

                                            if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                                Toast.makeText(HomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.e("Error", e.getMessage() + "__");
                                    }
                                }
                            });

                }
            });


        } else {
            navigateToSignInActivity();
        }

    }

    public void refreshActivity(String lang) {
        Paper.book().write("lang", lang);
        Language.setNewLocale(this, lang);
        new Handler()
                .postDelayed(() -> {

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }, 1050);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void listenToNotifications(NotFireModel notFireModel) {
        if (userModel != null) {
            getNotificationCount();

        }
    }

    @Override
    public void onBackPressed() {

        if (fragment_main != null && fragment_main.isAdded() && fragment_main.isVisible()) {
            if (userModel != null) {
                finish();
            }else {
                navigateToSignInActivity();
            }
        } else {
            displayFragmentMain();
        }
    }


    private void navigateToSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }





}
