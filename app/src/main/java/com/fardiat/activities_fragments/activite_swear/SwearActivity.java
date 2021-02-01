package com.fardiat.activities_fragments.activite_swear;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.fardiat.R;
import com.fardiat.databinding.ActivitySwearBinding;
import com.fardiat.language.Language;
import com.fardiat.models.UserModel;
import com.fardiat.preferences.Preferences;

import io.paperdb.Paper;

public class SwearActivity extends AppCompatActivity {

    private ActivitySwearBinding binding;
    private Preferences preference;
    private UserModel userModel;
    private String lang;
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_swear);
        initView();
    }

    private void initView() {
        Paper.init(this);
        lang = Paper.book().read("lang","ar");
        binding.setLang(lang);
        preference = Preferences.getInstance();
        userModel = preference.getUserData(this);

        binding.btnDone.setOnClickListener(view -> {
            finish();
        });
        binding.llBack.setOnClickListener(view -> finish());
    }
}
