package com.fardiat.activities_fragments.activity_add_ads;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.fardiat.R;
import com.fardiat.activities_fragments.activity_signup.FragmentMapTouchListener;
import com.fardiat.adapters.ImageAdsAdapter;
import com.fardiat.adapters.SpinnerCategoryAdapter;
import com.fardiat.databinding.ActivityAboutAppBinding;
import com.fardiat.databinding.ActivityAddAdsBinding;
import com.fardiat.databinding.ItemAddAdsBinding;
import com.fardiat.interfaces.Listeners;
import com.fardiat.language.Language;
import com.fardiat.models.AddAdsModel;
import com.fardiat.models.ItemAddAds;
import com.fardiat.models.ItemAddAdsDataModel;
import com.fardiat.models.MainCategoryDataModel;
import com.fardiat.models.MainCategoryModel;
import com.fardiat.models.PlaceGeocodeData;
import com.fardiat.models.PlaceMapDetailsData;
import com.fardiat.models.UserModel;
import com.fardiat.preferences.Preferences;
import com.fardiat.remote.Api;
import com.fardiat.share.Common;
import com.fardiat.tags.Tags;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAdsActivity extends AppCompatActivity implements Listeners.BackListener , Listeners.SignUpListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private ActivityAddAdsBinding binding;
    private String lang;
    private SimpleExoPlayer player;
    private int currentWindow = 0;
    private long currentPosition = 0;
    private boolean playWhenReady = true;
    private Uri videoUri = null;
    private List<String> imagesUriList;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private final int READ_REQ = 1, CAMERA_REQ = 2,VIDEO_REQ=3;
    private double lat = 0.0, lng = 0.0;
    private String address = "";
    private GoogleMap mMap;
    private Marker marker;
    private float zoom = 15.0f;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private final String fineLocPerm = Manifest.permission.ACCESS_FINE_LOCATION;
    private final int loc_req = 1225;
    private FragmentMapTouchListener fragment;
    private List<MainCategoryModel> categoryModelList;
    private SpinnerCategoryAdapter spinnerCategoryAdapter;
    private ImageAdsAdapter imageAdsAdapter;
    private boolean isVideoAvailable = false;
    private List<ItemAddAds> itemAddAdsList;
    private List<View> viewList;
    private AddAdsModel model;
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_ads);
        initView();
    }




    private void initView() {
        model = new AddAdsModel();
        viewList = new ArrayList<>();
        itemAddAdsList = new ArrayList<>();
        categoryModelList = new ArrayList<>();
        imagesUriList = new ArrayList<>();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        MainCategoryModel mainCategoryModel = new MainCategoryModel();
        mainCategoryModel.setId(0);
        mainCategoryModel.setTitle(getString(R.string.choose_category));
        categoryModelList.add(mainCategoryModel);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setModel(model);
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setListener(this);
        updateUI();

        binding.recView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        imageAdsAdapter = new ImageAdsAdapter(imagesUriList,this);
        binding.recView.setAdapter(imageAdsAdapter);

        binding.edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = binding.edtSearch.getText().toString();
                if (!TextUtils.isEmpty(query)) {
                    Common.CloseKeyBoard(this,binding.edtSearch);
                    Search(query);
                    return false;
                }
            }
            return false;
        });
        spinnerCategoryAdapter = new SpinnerCategoryAdapter(categoryModelList,this);
        binding.spinner.setAdapter(spinnerCategoryAdapter);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0){
                    model.setDepartment_id(0);
                    if (itemAddAdsList.size()>0){
                        removeItems();
                    }
                }else {
                    model.setDepartment_id(categoryModelList.get(i).getId());
                    getItems(categoryModelList.get(i).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        binding.flUploadVideo.setOnClickListener(view -> {
            checkVideoPermission();
        });

        getMainCategory();

    }

    private void addItems()
    {

        removeItems();

        List<ItemAddAds> itemAddAdsList = new ArrayList<>();
        for (ItemAddAds itemAddAds:this.itemAddAdsList){
            itemAddAds.setContent("");
            ItemAddAdsBinding itemAddAdsBinding = DataBindingUtil.inflate(LayoutInflater.from(this),R.layout.item_add_ads,null,false);
            itemAddAdsBinding.tvTitle.setText(itemAddAds.getTitle());
            itemAddAdsBinding.edt.setHint(itemAddAds.getTitle());
            Picasso.get().load(Uri.parse(Tags.IMAGE_URL+itemAddAds.getIcon())).into(itemAddAdsBinding.icon, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {

                }
            });
            itemAddAdsBinding.edt.setTag(itemAddAds.getId());
            itemAddAdsBinding.setModel(itemAddAds);
            itemAddAdsList.add(itemAddAds);
            binding.llAdditionViews.addView(itemAddAdsBinding.getRoot());
            viewList.add(itemAddAdsBinding.getRoot());
        }
        model.setItemAddAdsList(itemAddAdsList);

    }

    private void removeItems()
    {
        binding.llAdditionViews.removeAllViews();
        viewList.clear();
    }

    private void getMainCategory()
    {
        Api.getService(Tags.base_url)
                .getMainCategory_Products()
                .enqueue(new Callback<MainCategoryDataModel>() {
                    @Override
                    public void onResponse(Call<MainCategoryDataModel> call, Response<MainCategoryDataModel> response) {
                        if (response.isSuccessful()) {
                            categoryModelList.addAll(response.body().getData());
                            runOnUiThread(() -> spinnerCategoryAdapter.notifyDataSetChanged());


                        } else {

                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(AddAdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MainCategoryDataModel> call, Throwable t) {
                        try {

                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(AddAdsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }
    private void initPlayer(Uri uri)
    {

        if (isVideoAvailable){
            binding.flPlayerView.setVisibility(View.GONE);
            DataSource.Factory factory = new DefaultDataSourceFactory(this, "Ta3leem_live");


            if (player == null) {
                player = new SimpleExoPlayer.Builder(this).build();
                binding.player.setPlayer(player);
                MediaSource mediaSource = new ProgressiveMediaSource.Factory(factory).createMediaSource(uri);
                player.prepare(mediaSource);

                player.seekTo(currentWindow, currentPosition);
                player.setPlayWhenReady(playWhenReady);
                player.prepare(mediaSource);
            } else {

                MediaSource mediaSource = new ProgressiveMediaSource.Factory(factory).createMediaSource(uri);

                player.seekTo(currentWindow, currentPosition);
                player.setPlayWhenReady(playWhenReady);
                player.prepare(mediaSource);
            }
        }






    }
    @Override
    protected void onStop()
    {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            release();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            release();
        }
    }


    private void release(){
        if (player!=null){
            currentWindow = player.getCurrentWindowIndex();
            currentPosition = player.getCurrentPosition();
            player.release();
            player=null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPlayer(videoUri);
    }

    @Override
    public void back() {
        finish();
    }

    @Override
    public void openSheet() {
        binding.expandLayout.setExpanded(true, true);
    }

    @Override
    public void closeSheet() {
        binding.expandLayout.collapse(true);

    }


    @Override
    public void checkDataValid() {
        model.setImagesList(imagesUriList);
        for (int index =0;index<model.getItemAddAdsList().size();index++){
            ItemAddAds itemAddAds = model.getItemAddAdsList().get(index);
            View view = viewList.get(index);
            LinearLayout linearLayout = (LinearLayout) view;
            EditText editText = linearLayout.findViewWithTag(itemAddAds.getId());
            if (itemAddAds.getContent().isEmpty()){
                editText.setError(getString(R.string.field_required));
            }else {
                editText.setError(null);

            }
        }
        if (model.isDataValid(this)){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            if (videoUri!=null&&model.getItemAddAdsList().size()>0){
                addAdsWithVideoWithList();
            }else if (videoUri==null&&model.getItemAddAdsList().size()==0){
                addAdsWithoutVideoWithoutList();

            }else if (videoUri!=null&&model.getItemAddAdsList().size()==0){
                addAdsWithVideoWithoutList();

            }else if (videoUri==null&&model.getItemAddAdsList().size()>0){
                addAdsWithoutVideoWithList();

            }
        }
    }

    private void addAdsWithoutVideoWithList() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        RequestBody id_part = Common.getRequestBodyText(String.valueOf(userModel.getUser().getId()));
        RequestBody title_part = Common.getRequestBodyText(model.getName());
        RequestBody department_id_part = Common.getRequestBodyText(String.valueOf(model.getDepartment_id()));
        RequestBody price_part = Common.getRequestBodyText(model.getPrice());
        RequestBody details_part = Common.getRequestBodyText(model.getDetails());
        RequestBody address_part = Common.getRequestBodyText(model.getAddress());
        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(model.getLat()));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(model.getLng()));

        Map<String,RequestBody> map = new HashMap<>();
        for (int index=0;index<model.getItemAddAdsList().size();index++){
            map.put("product_details["+index+"][title]",Common.getRequestBodyText(model.getItemAddAdsList().get(index).getTitle()));
            map.put("product_details["+index+"][icon]",Common.getRequestBodyText(model.getItemAddAdsList().get(index).getIcon()));
            map.put("product_details["+index+"][content]",Common.getRequestBodyText(model.getItemAddAdsList().get(index).getContent()));

        }


        Api.getService(Tags.base_url)
                .addAdsWithoutVideoWithList(title_part,department_id_part,price_part,id_part,details_part,address_part,lat_part,lng_part,getMultipartImage(),map)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            finish();
                        } else {
                            try {
                                Log.e("error",response.code()+"__"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(AddAdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            }{
                                Log.e("mmmmmmmmmm",response.code()+"__"+response.errorBody());

                                Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("mmmmmmmmmm",t.getMessage()+"__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(AddAdsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("ccccc",t.getMessage());

                                    Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    private void addAdsWithVideoWithoutList() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        RequestBody id_part = Common.getRequestBodyText(String.valueOf(userModel.getUser().getId()));
        RequestBody title_part = Common.getRequestBodyText(model.getName());
        RequestBody department_id_part = Common.getRequestBodyText(String.valueOf(model.getDepartment_id()));
        RequestBody price_part = Common.getRequestBodyText(model.getPrice());
        RequestBody details_part = Common.getRequestBodyText(model.getDetails());
        RequestBody address_part = Common.getRequestBodyText(model.getAddress());
        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(model.getLat()));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(model.getLng()));
        MultipartBody.Part video = Common.getMultiPartVideo(this, videoUri, "vedio");


        Api.getService(Tags.base_url)
                .addAdsWithVideoWithoutList(title_part,department_id_part,price_part,id_part,details_part,address_part,lat_part,lng_part,getMultipartImage(),video)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            finish();
                        } else {
                            try {
                                Log.e("error",response.code()+"__"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(AddAdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            }{
                                Log.e("error",response.code()+"cccccccc"+response.errorBody());

                                Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(AddAdsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    private void addAdsWithoutVideoWithoutList() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        RequestBody id_part = Common.getRequestBodyText(String.valueOf(userModel.getUser().getId()));
        RequestBody title_part = Common.getRequestBodyText(model.getName());
        RequestBody department_id_part = Common.getRequestBodyText(String.valueOf(model.getDepartment_id()));
        RequestBody price_part = Common.getRequestBodyText(model.getPrice());
        RequestBody details_part = Common.getRequestBodyText(model.getDetails());
        RequestBody address_part = Common.getRequestBodyText(model.getAddress());
        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(model.getLat()));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(model.getLng()));


        Api.getService(Tags.base_url)
                .addAdsWithoutVideoWithoutList(title_part,department_id_part,price_part,id_part,details_part,address_part,lat_part,lng_part,getMultipartImage())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            finish();
                        } else {
                            try {
                                Log.e("error",response.code()+"__"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(AddAdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            }{
                                Log.e("bbbbbbbb",response.code()+"__"+response.errorBody());

                                Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(AddAdsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    private void addAdsWithVideoWithList() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        RequestBody id_part = Common.getRequestBodyText(String.valueOf(userModel.getUser().getId()));
        RequestBody title_part = Common.getRequestBodyText(model.getName());
        RequestBody department_id_part = Common.getRequestBodyText(String.valueOf(model.getDepartment_id()));
        RequestBody price_part = Common.getRequestBodyText(model.getPrice());
        RequestBody details_part = Common.getRequestBodyText(model.getDetails());
        RequestBody address_part = Common.getRequestBodyText(model.getAddress());
        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(model.getLat()));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(model.getLng()));
        MultipartBody.Part video = Common.getMultiPartVideo(this, videoUri, "vedio");
        Map<String,RequestBody> map = new HashMap<>();
        for (int index=0;index<model.getItemAddAdsList().size();index++){
            map.put("product_details["+index+"][title]",Common.getRequestBodyText(model.getItemAddAdsList().get(index).getTitle()));
            map.put("product_details["+index+"][icon]",Common.getRequestBodyText(model.getItemAddAdsList().get(index).getIcon()));
            map.put("product_details["+index+"][content]",Common.getRequestBodyText(model.getItemAddAdsList().get(index).getContent()));

        }

        Api.getService(Tags.base_url)
                .addAdsWithVideoWithList(title_part,department_id_part,price_part,id_part,details_part,address_part,lat_part,lng_part,video,getMultipartImage(),map)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            finish();
                        } else {
                            try {
                                Log.e("error",response.code()+"__"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(AddAdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            }{
                                Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(AddAdsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    private List<MultipartBody.Part> getMultipartImage(){
        List<MultipartBody.Part> parts = new ArrayList<>();
        for (String path :imagesUriList){
            Uri uri = Uri.parse(path);
            MultipartBody.Part part = Common.getMultiPart(this,uri,"multi_image[]");
            parts.add(part);
        }
        return parts;
    }

    @Override
    public void checkReadPermission() {
        closeSheet();
        if (ActivityCompat.checkSelfPermission(this, READ_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_PERM}, READ_REQ);
        } else {
            SelectImage(READ_REQ);
        }
    }

    public void checkVideoPermission() {
        closeSheet();
        if (ActivityCompat.checkSelfPermission(this, READ_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_PERM}, VIDEO_REQ);
        } else {
            displayVideoIntent(VIDEO_REQ);
        }
    }

    private void displayVideoIntent(int video_req)
    {
        Intent intent = new Intent();

        if (video_req == VIDEO_REQ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            } else {
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("video/*");
            startActivityForResult(intent, video_req);

        }
    }

    @Override
    public void checkCameraPermission() {

        closeSheet();

        if (ContextCompat.checkSelfPermission(this, write_permission) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, camera_permission) == PackageManager.PERMISSION_GRANTED
        ) {
            SelectImage(CAMERA_REQ);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{camera_permission, write_permission}, CAMERA_REQ);
        }
    }

    private void SelectImage(int req) {

        Intent intent = new Intent();

        if (req == READ_REQ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            } else {
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");
            startActivityForResult(intent, req);

        } else if (req == CAMERA_REQ) {
            try {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, req);
            } catch (SecurityException e) {
                Toast.makeText(this, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();

            }


        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == loc_req) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                initGoogleApi();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == READ_REQ) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SelectImage(requestCode);
            } else {
                Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == CAMERA_REQ) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                SelectImage(requestCode);
            } else {
                Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == VIDEO_REQ) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayVideoIntent(requestCode);
            } else {
                Toast.makeText(this, R.string.vid_pem_denied, Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            startLocationUpdate();
        }else if (requestCode == READ_REQ && resultCode == Activity.RESULT_OK && data != null) {

            Uri uri = data.getData();
            if (imagesUriList.size()<2){
                imagesUriList.add(uri.toString());
                imageAdsAdapter.notifyItemInserted(imagesUriList.size()-1);
            }else {
                Toast.makeText(this, R.string.max_ad_photo, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA_REQ && resultCode == Activity.RESULT_OK && data != null) {

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            Uri uri = getUriFromBitmap(bitmap);
            if (uri != null) {
                if (imagesUriList.size()<2){
                    imagesUriList.add(uri.toString());
                    imageAdsAdapter.notifyItemInserted(imagesUriList.size()-1);

                }else {
                    Toast.makeText(this, R.string.max_ad_photo, Toast.LENGTH_SHORT).show();
                }
            }


        }else if (requestCode == VIDEO_REQ && resultCode == Activity.RESULT_OK && data != null) {

            Uri uri = data.getData();
            new VideoTask().execute(uri);
        }

    }


    private Uri getUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "", ""));
    }



    private void Search(String query)
    {

        String fields = "id,place_id,name,geometry,formatted_address";

        Api.getService("https://maps.googleapis.com/maps/api/")
                .searchOnMap("textquery", query, fields, "ar", getString(R.string.map_key))
                .enqueue(new Callback<PlaceMapDetailsData>() {
                    @Override
                    public void onResponse(Call<PlaceMapDetailsData> call, Response<PlaceMapDetailsData> response) {

                        if (response.isSuccessful() && response.body() != null) {


                            if (response.body().getCandidates().size() > 0) {

                                address = response.body().getCandidates().get(0).getFormatted_address().replace("Unnamed Road,", "");
                                binding.edtSearch.setText(address);
                                LatLng latLng = new LatLng(response.body().getCandidates().get(0).getGeometry().getLocation().getLat(), response.body().getCandidates().get(0).getGeometry().getLocation().getLng());
                                addMarker(latLng);
                            }
                        } else {

                            try {
                                Log.e("error_code", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<PlaceMapDetailsData> call, Throwable t) {
                        try {

                            Toast.makeText(AddAdsActivity.this, getString(R.string.something), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {

                        }
                    }
                });
    }
    private void getGeoData(final double lat, double lng)
    {
        String location = lat + "," + lng;
        Api.getService("https://maps.googleapis.com/maps/api/")
                .getGeoData(location, "ar", getString(R.string.map_key))
                .enqueue(new Callback<PlaceGeocodeData>() {
                    @Override
                    public void onResponse(Call<PlaceGeocodeData> call, Response<PlaceGeocodeData> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            if (response.body().getResults().size() > 0) {
                                address = response.body().getResults().get(0).getFormatted_address().replace("Unnamed Road,", "");
                                binding.edtSearch.setText(address);

                                /*signUpModel.setAddress(address);
                                signUpModel.setLat(lat);
                                signUpModel.setLng(lng);
                                binding.edtAddress.setText(address + "");*/
                            }
                        } else {

                            try {
                                Log.e("error_code", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<PlaceGeocodeData> call, Throwable t) {
                        try {
                            Toast.makeText(AddAdsActivity.this, getString(R.string.something), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {

                        }
                    }
                });
    }
    private void checkPermission()
    {
        if (ActivityCompat.checkSelfPermission(AddAdsActivity.this, fineLocPerm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddAdsActivity.this, new String[]{fineLocPerm}, loc_req);
        } else {
            mMap.setMyLocationEnabled(true);
            initGoogleApi();
        }
    }
    private void initGoogleApi()
    {
        googleApiClient = new GoogleApiClient.Builder(AddAdsActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }
    private void updateUI()
    {

        fragment = (FragmentMapTouchListener) getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);


    }
    @Override
    public void onMapReady(GoogleMap googleMap)
    {

        if (googleMap != null) {
            mMap = googleMap;
            mMap.setTrafficEnabled(false);
            mMap.setBuildingsEnabled(false);
            mMap.setIndoorEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            fragment.setListener(() -> binding.scrollView.requestDisallowInterceptTouchEvent(true));
            checkPermission();

            mMap.setOnMapClickListener(latLng -> {
                lat = latLng.latitude;
                lng = latLng.longitude;
                LatLng latLng2 = new LatLng(lat,lng);
                addMarker(latLng2);
                getGeoData(lat,lng);

            });


        }
    }
    private void addMarker(LatLng latLng)
    {
        model.setLat(latLng.latitude);
        model.setLng(latLng.longitude);
        if (marker==null){
            marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }else {
            marker.setPosition(latLng);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        initLocationRequest();
    }

    private void initLocationRequest()
    {
        locationRequest = LocationRequest.create();
        locationRequest.setFastestInterval(1000);
        locationRequest.setInterval(60000);
        LocationSettingsRequest.Builder request = new LocationSettingsRequest.Builder();
        request.addLocationRequest(locationRequest);
        request.setAlwaysShow(false);


        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, request.build());
        result.setResultCallback(locationSettingsResult -> {
            Status status = locationSettingsResult.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    startLocationUpdate();
                    break;

                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(AddAdsActivity.this, 100);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        });


    }
    @Override
    public void onConnectionSuspended(int i)
    {
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @SuppressLint("MissingPermission")
    private void startLocationUpdate()
    {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        LocationServices.getFusedLocationProviderClient(AddAdsActivity.this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }
    @Override
    public void onLocationChanged(Location location)
    {
        lat = location.getLatitude();
        lng = location.getLongitude();
        getGeoData(lat,lng);
        addMarker(new LatLng(lat,lng));
        if (googleApiClient != null) {
            LocationServices.getFusedLocationProviderClient(AddAdsActivity.this).removeLocationUpdates(locationCallback);
            googleApiClient.disconnect();
            googleApiClient = null;
        }
    }

    public void deleteImage(int adapterPosition) {
        if (imagesUriList.size()>0){
            imagesUriList.remove(adapterPosition);
            imageAdsAdapter.notifyItemRemoved(adapterPosition);

        }
    }

    public  class VideoTask extends AsyncTask<Uri,Void,Long>
    {
        MediaMetadataRetriever retriever;
        private Uri uri ;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            retriever = new MediaMetadataRetriever();
        }

        @Override
        protected Long doInBackground(Uri... uris) {
            uri = uris[0];
            retriever.setDataSource(AddAdsActivity.this,uris[0]);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long duration  = Long.parseLong(time)/1000;
            retriever.release();
            return duration;
        }

        @Override
        protected void onPostExecute(Long duration) {
            super.onPostExecute(duration);
            if (duration<=59){
                isVideoAvailable = true;
                videoUri = uri;
                model.setVideo_url(videoUri.toString());
                initPlayer(videoUri);

            }else {
                Toast.makeText(AddAdsActivity.this, R.string.length_video_shouldnot_exceed, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void getItems(int department_id){
        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();;
        Api.getService(Tags.base_url)
                .getItemsAds(department_id,"off",20)
                .enqueue(new Callback<ItemAddAdsDataModel>() {
                    @Override
                    public void onResponse(Call<ItemAddAdsDataModel> call, Response<ItemAddAdsDataModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            if (response.body().getData().size()>0){
                                itemAddAdsList.clear();
                                itemAddAdsList.addAll(response.body().getData());
                                Log.e("size",itemAddAdsList.size()+"__");
                                model.setHasExtraItems(true);
                                addItems();
                            }else {
                                model.setHasExtraItems(false);
                                model.setItemAddAdsList(new ArrayList<>());
                                removeItems();

                            }
                        } else {
                            dialog.dismiss();

                            if (response.code() == 500) {
                                Toast.makeText(AddAdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                try {

                                    Log.e("error", response.code() + "_" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ItemAddAdsDataModel> call, Throwable t) {
                        try {
                            dialog.dismiss();

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(AddAdsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddAdsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });
    }


}