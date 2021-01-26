package com.fardiat.activities_fragments.activity_client_profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.fardiat.R;
import com.fardiat.activities_fragments.activity_edit_profile.EditProfileActivity;
import com.fardiat.databinding.ActivityClientProfileBinding;
import com.fardiat.interfaces.Listeners;
import com.fardiat.language.Language;
import com.fardiat.models.UserModel;
import com.fardiat.preferences.Preferences;

import java.util.Locale;
import java.util.Random;

import io.paperdb.Paper;

public class ClientProfileActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityClientProfileBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_client_profile);
        initView();
    }


    private void initView() {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Random random = new Random();

        binding.setRandom(random);
        binding.setBackListener(this);

        binding.setModel(userModel);
        binding.imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClientProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public void back() {
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (preferences != null) {
            userModel = preferences.getUserData(this);
            binding.setModel(userModel);
        }
    }
}
