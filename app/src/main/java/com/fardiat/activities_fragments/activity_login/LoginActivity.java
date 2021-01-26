package com.fardiat.activities_fragments.activity_login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fardiat.R;
import com.fardiat.activities_fragments.activity_home.HomeActivity;
import com.fardiat.activities_fragments.activity_verification_code.VerificationCodeActivity;
import com.fardiat.adapters.CountriesAdapter;
import com.fardiat.databinding.ActivityLoginBinding;
import com.fardiat.databinding.DialogCountriesBinding;
import com.fardiat.interfaces.Listeners;
import com.fardiat.language.Language;
import com.fardiat.models.CountryModel;
import com.fardiat.models.LoginModel;
import com.fardiat.share.Common;
import com.fardiat.singleton.CartSingleton;
import com.fardiat.tags.Tags;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity implements Listeners.LoginListener {
    private ActivityLoginBinding binding;
    private LoginModel loginModel;
    private CountryModel[] countries = new CountryModel[]{  new CountryModel("SA", "Saudi Arabia", "+966", R.drawable.flag_sa, "SAR"), new CountryModel("EG", "Egypt", "+20", R.drawable.flag_eg, "EGP")};
    private List<CountryModel> countryModelList = new ArrayList<>();
    private CountriesAdapter countriesAdapter;
    private AlertDialog dialog;
    private String lang;
    private String phone_code = "+966";
    private CartSingleton singleton;
    private int back = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        initView();
    }

    @SuppressLint("ResourceAsColor")
    private void initView() {
        singleton = CartSingleton.newInstance();
        countryModelList = new ArrayList<>(Arrays.asList(countries));
        loginModel = new LoginModel();
        binding.setLoginModel(loginModel);
        binding.setListener(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);

        binding.edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().startsWith("0")) {
                    binding.edtPhone.setText("");
                }
            }
        });

        sortCountries();
        createCountriesDialog();
        binding.btnSkip.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }


    private void createCountriesDialog() {

        dialog = new AlertDialog.Builder(this)
                .create();
        countriesAdapter = new CountriesAdapter(countryModelList, this);

        DialogCountriesBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_countries, null, false);
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        binding.recView.setAdapter(countriesAdapter);

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
    }

    private void sortCountries() {
        Collections.sort(countryModelList, (country1, country2) -> {
            return country1.getName().trim().compareToIgnoreCase(country2.getName().trim());
        });
    }

    @Override
    public void validate() {
        if (loginModel.isDataValid(this)) {
            Common.CloseKeyBoard(this, binding.edtPhone);


            navigateToVerificationCodeActivity();
        }
    }

    @Override
    public void showCountryDialog() {
        dialog.show();
    }

    private void navigateToVerificationCodeActivity() {

        Intent intent = new Intent(this, VerificationCodeActivity.class);
        intent.putExtra("phone_code", phone_code);
        intent.putExtra("phone", loginModel.getPhone());
        startActivity(intent);
        finish();

    }

    public void setItemData(CountryModel countryModel) {
        dialog.dismiss();
        phone_code = countryModel.getDialCode();
        binding.txtCode.setText(countryModel.getDialCode());
        binding.image.setImageResource(countryModel.getFlag());
    }

    @Override
    public void onBackPressed() {
        if (singleton.getItemCartModelList() != null && singleton.getItemCartModelList().size() > 0) {
            if (back == 0) {
                back = 1;
                String sound_Path = "android.resource://" + getPackageName() + "/" + R.raw.not;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    String CHANNEL_ID = "my_channel_02";
                    CharSequence CHANNEL_NAME = "my_channel_name";
                    int IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;

                    final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                    final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE);
                    channel.setShowBadge(true);
                    channel.setSound(Uri.parse(sound_Path), new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                            .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                            .build()
                    );

                    builder.setChannelId(CHANNEL_ID);
                    builder.setSound(Uri.parse(sound_Path), AudioManager.STREAM_NOTIFICATION);
                    builder.setSmallIcon(R.drawable.logo);


                    builder.setContentTitle(getResources().getString(R.string.cart));


                    builder.setContentText(getResources().getString(R.string.cart_not_empty));


                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                    builder.setLargeIcon(bitmap);
                    manager.createNotificationChannel(channel);
                    manager.notify(new Random().nextInt(200), builder.build());
                } else {

                    final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

                    builder.setSound(Uri.parse(sound_Path), AudioManager.STREAM_NOTIFICATION);
                    builder.setSmallIcon(R.drawable.logo);

                    builder.setContentTitle(getResources().getString(R.string.cart));


                    builder.setContentText(getResources().getString(R.string.cart_not_empty));


                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                    builder.setLargeIcon(bitmap);
                    manager.notify(new Random().nextInt(200), builder.build());

                }
            } else {
                // back=1;
                CreatecloseDialog();

            }
        } else {
            finish();
        }
    }

    private void CreatecloseDialog() {

        final androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_delete, null);
        TextView tvDelete = view.findViewById(R.id.tvDelete);
        TextView tvCancel = view.findViewById(R.id.tvCancel);
        TextView tvtitle = view.findViewById(R.id.tvtitle);
        TextView tvcart = view.findViewById(R.id.tvcart);

        tvDelete.setText(getResources().getString(R.string.back));
        tvcart.setText(getResources().getString(R.string.cart));
        tvtitle.setText(getResources().getString(R.string.if_you));
        tvCancel.setOnClickListener(v -> {
            back = 0;
            dialog.dismiss();

        });

        tvDelete.setOnClickListener(v -> {
            try {
                dialog.dismiss();
                finish();
            } catch (Exception e) {
            }

            dialog.dismiss();
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setView(view);
        dialog.show();

    }

}
