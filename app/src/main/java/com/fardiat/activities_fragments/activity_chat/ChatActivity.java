package com.fardiat.activities_fragments.activity_chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fardiat.R;
import com.fardiat.adapters.ChatAdapter;
import com.fardiat.databinding.ActivityChatBinding;
import com.fardiat.language.Language;
import com.fardiat.models.ChatUserModel;
import com.fardiat.models.MessageDataModel;
import com.fardiat.models.MessageModel;
import com.fardiat.models.UserModel;
import com.fardiat.preferences.Preferences;
import com.fardiat.remote.Api;
import com.fardiat.tags.Tags;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private String lang;
    private final int IMG_REQ = 1;
    private final int CAMERA_REQ = 2;
    private final int MIC_REQ = 3;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String WRITE_PERM = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String CAMERA_PERM = Manifest.permission.CAMERA;
    private final String MIC_PERM = Manifest.permission.RECORD_AUDIO;
    private long audio_total_seconds = 0;
    private MediaRecorder recorder;
    private String audio_path = "";
    private Handler handler;
    private Runnable runnable;
    private ChatUserModel chatUserModel;
    private UserModel userModel;
    private Preferences preferences;
    private ChatAdapter adapter;
    private List<MessageModel> messageModelList;
    private boolean isDataChanged = false;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        getDataFromIntent();
        initView();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(Tags.not_tag, Tags.not_id);

        }
        chatUserModel = (ChatUserModel) intent.getSerializableExtra("data");

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        messageModelList = new ArrayList<>();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.setModel(chatUserModel);
        adapter = new ChatAdapter(messageModelList, this, userModel.getUser().getId());
        binding.recView.setLayoutManager(manager);
        binding.recView.setAdapter(adapter);

        binding.llBack.setOnClickListener(v -> {
            back();
        });


        binding.imageChooser.setOnClickListener(v -> {
            checkGalleryPermission();

        });

        binding.imageCamera.setOnClickListener(v -> {
            checkCameraPermission();
        });

        binding.imageRecord.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (isMicReady()) {
                    createMediaRecorder();

                } else {
                    checkMicPermission();
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {

                if (isMicReady()) {
                    try {
                        recorder.stop();
                        stopTimer();
                        sendAttachment(audio_path, "voice");
                    } catch (Exception e) {
                        stopTimer();
                    }

                }

            }

            return true;
        });


        binding.imageSend.setOnClickListener(v -> {
            String message = binding.edtMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                binding.edtMessage.setText("");
                sendChatText(message);
            }
        });

        preferences.create_room_id(this, String.valueOf(chatUserModel.getRoom_id()));
        EventBus.getDefault().register(this);
        getAllMessages();

    }


    public void getAllMessages()
    {

        Api.getService(Tags.base_url)
                .getChatMessages(userModel.getUser().getToken(), chatUserModel.getRoom_id(), 1)
                .enqueue(new Callback<MessageDataModel>() {
                    @Override
                    public void onResponse(Call<MessageDataModel> call, Response<MessageDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {

                            if (response.body() != null) {

                                if (response.body().getMessages().getData().size() > 0) {
                                    messageModelList.clear();
                                    messageModelList.addAll(response.body().getMessages().getData());
                                    adapter.notifyDataSetChanged();
                                    binding.recView.postDelayed(() -> binding.recView.smoothScrollToPosition(messageModelList.size() - 1), 200);

                                }
                            } else {
                                Toast.makeText(ChatActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }


                        } else {
                            if (response.code() == 500) {
                                Toast.makeText(ChatActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ChatActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }

                            try {
                                Log.e("error code", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<MessageDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);
                            if (t.getMessage() != null) {
                                Log.e("Error", t.getMessage());

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(ChatActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else if (t.getMessage().contains("socket")) {

                                } else {
                                    Toast.makeText(ChatActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }
    private void sendAttachment(String file_uri, String attachment_type)
    {

        Intent intent = new Intent(this, ServiceUploadAttachment.class);
        intent.putExtra("file_uri", file_uri);
        intent.putExtra("user_token", userModel.getUser().getToken());
        intent.putExtra("user_id", userModel.getUser().getId());
        intent.putExtra("to_user_id", chatUserModel.getId());
        intent.putExtra("room_id", chatUserModel.getRoom_id());
        intent.putExtra("attachment_type", attachment_type);
        startService(intent);


    }
    private void sendChatText(String message)
    {
        long date = Calendar.getInstance().getTimeInMillis();

        Api.getService(Tags.base_url)
                .sendChatMessage(userModel.getUser().getToken(), chatUserModel.getRoom_id(), userModel.getUser().getId(), chatUserModel.getId(), "text", date, message)
                .enqueue(new Callback<MessageModel>() {
                    @Override
                    public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {
                        if (response.isSuccessful()) {

                            if (response.body() != null) {
                                isDataChanged = true;
                                MessageModel model = response.body();
                                messageModelList.add(model);
                                adapter.notifyItemInserted(messageModelList.size());
                                binding.recView.postDelayed(() -> binding.recView.smoothScrollToPosition(messageModelList.size() - 1), 200);
                            } else {
                                Toast.makeText(ChatActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }


                        } else {

                            try {
                                Log.e("error", response.code() + "___" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(ChatActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<MessageModel> call, Throwable t) {
                        try {
                            if (t.getMessage() != null) {
                                Log.e("Error", t.getMessage());

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(ChatActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else if (t.getMessage().contains("socket")) {

                                } else {
                                    Toast.makeText(ChatActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }
    private void checkCameraPermission()
    {
        if (ActivityCompat.checkSelfPermission(this, CAMERA_PERM) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, WRITE_PERM) == PackageManager.PERMISSION_GRANTED) {
            selectImage(CAMERA_REQ);

        } else {

            ActivityCompat.requestPermissions(this, new String[]{CAMERA_PERM, WRITE_PERM}, CAMERA_REQ);

        }

    }
    private void checkGalleryPermission()
    {
        if (ActivityCompat.checkSelfPermission(this, READ_PERM) == PackageManager.PERMISSION_GRANTED) {
            selectImage(IMG_REQ);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{READ_PERM}, IMG_REQ);

        }

    }
    private void selectImage(int req)
    {

        Intent intent = new Intent();
        if (req == IMG_REQ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

            } else {
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


            }
            intent.setType("image/*");


        } else if (req == CAMERA_REQ) {

            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        }

        startActivityForResult(intent, req);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMG_REQ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage(requestCode);
            } else {
                Toast.makeText(this, "Access image denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA_REQ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                selectImage(requestCode);
            } else {
                Toast.makeText(this, "Access camera denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == MIC_REQ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Access mic denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQ && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            sendAttachment(uri.toString(), "file");

        } else if (requestCode == CAMERA_REQ && resultCode == RESULT_OK && data != null) {

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            Uri uri = getUriFromBitmap(bitmap);
            sendAttachment(uri.toString(), "file");

        }

    }


    private Uri getUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
        return Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", ""));

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAttachmentSuccess(MessageModel messageModel) {
        isDataChanged = true;
        messageModelList.add(messageModel);
        adapter.notifyItemChanged(messageModelList.size());
        binding.recView.postDelayed(() -> binding.recView.smoothScrollToPosition(messageModelList.size() - 1), 200);


    }

    private void createMediaRecorder()
    {

        String audio_name = "AUD" + System.currentTimeMillis() + ".mp3";

        File file = new File(Tags.audio_path);
        boolean isFolderCreate;

        if (!file.exists()) {
            isFolderCreate = file.mkdir();
        } else {
            isFolderCreate = true;
        }


        if (isFolderCreate) {
            startTimer();
            binding.recordTime.setVisibility(View.VISIBLE);
            // createVibration();
            audio_path = file.getAbsolutePath() + "/" + audio_name;
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioChannels(1);
            recorder.setOutputFile(audio_path);
            try {
                recorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            recorder.start();
        } else {
            Toast.makeText(this, "Unable to create sound file on your device", Toast.LENGTH_SHORT).show();
        }


    }
    private void checkMicPermission()
    {
        if (ActivityCompat.checkSelfPermission(this, MIC_PERM) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, WRITE_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{MIC_PERM, WRITE_PERM}, MIC_REQ);

        }

    }
    private boolean isMicReady()
    {

        if (ActivityCompat.checkSelfPermission(this, MIC_PERM) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, WRITE_PERM) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        return false;

    }
    private void startTimer()
    {
        binding.recordTime.setVisibility(View.VISIBLE);
        binding.edtMessage.setVisibility(View.INVISIBLE);
        handler = new Handler();
        runnable = () -> {
            audio_total_seconds += 1;
            binding.recordTime.setText(getRecordTimeFormat(audio_total_seconds));
            startTimer();
        };

        handler.postDelayed(runnable, 1000);
    }
    private void stopTimer()
    {
        binding.recordTime.setVisibility(View.GONE);
        binding.edtMessage.setVisibility(View.VISIBLE);
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
        audio_total_seconds = 0;
        if (runnable != null) {
            handler.removeCallbacks(runnable);

        }
        handler = null;
        binding.recordTime.setText("00:00:00");
        binding.recordTime.setVisibility(View.GONE);
    }
    private String getRecordTimeFormat(long seconds)
    {
        int hours = (int) (seconds / 3600);
        int minutes = (int) ((seconds % 3600) / 60);
        int second = (int) (seconds % 60);

        return String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, second);

    }
    @Override
    public void onBackPressed() {
        back();
    }

    public void back() {
        preferences.create_room_id(this, "");
        if (isDataChanged) {
            setResult(RESULT_OK);
        }
        finish();
    }

}