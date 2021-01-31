package com.fardiat.activities_fragments.activity_chat;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.fardiat.R;
import com.fardiat.models.MessageModel;
import com.fardiat.remote.Api;
import com.fardiat.share.Common;
import com.fardiat.tags.Tags;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceUploadAttachment extends Service {
    private String file_uri;
    private int user_id;
    private int to_user_id;
    private String user_token;
    private int room_id;
    private String attachment_type;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        file_uri = intent.getStringExtra("file_uri");
        user_token = intent.getStringExtra("user_token");
        user_id = intent.getIntExtra("user_id", 0);
        to_user_id = intent.getIntExtra("to_user_id", 0);
        room_id = intent.getIntExtra("room_id", 0);
        attachment_type = intent.getStringExtra("attachment_type");

        uploadAttachment(attachment_type);

        return START_STICKY;
    }

    private void uploadAttachment(String attachment_type) {
        Calendar calendar = Calendar.getInstance();
        long date = calendar.getTimeInMillis();

        RequestBody to_user_id_part = Common.getRequestBodyText(String.valueOf(to_user_id));
        RequestBody user_id_part = Common.getRequestBodyText(String.valueOf(user_id));
        RequestBody room_id_part = Common.getRequestBodyText(String.valueOf(room_id));
        RequestBody type_part = Common.getRequestBodyText(attachment_type);
        RequestBody message_part = Common.getRequestBodyText("");
        RequestBody date_part = Common.getRequestBodyText(String.valueOf(date));

        MultipartBody.Part file_part;
        if (attachment_type.equals("file")) {
            file_part = Common.getMultiPartImage(this, Uri.parse(file_uri), "file_link");

        } else {
            file_part = Common.getMultiPartAudio(this, file_uri, "voice");

        }

        Api.getService(Tags.base_url).sendChatAttachment(user_token, room_id_part, user_id_part, to_user_id_part, type_part, date_part, file_part)
                .enqueue(new Callback<MessageModel>() {
                    @Override
                    public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {
                        if (response.isSuccessful()) {

                            if (response.body() != null) {
                                MessageModel model = response.body();
                                EventBus.getDefault().post(model);
                                stopSelf();
                            } else {
                                Toast.makeText(ServiceUploadAttachment.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }


                        } else {

                            if (response.code() == 500) {

                                Toast.makeText(ServiceUploadAttachment.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(ServiceUploadAttachment.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageModel> call, Throwable t) {

                        try {

                            stopSelf();

                            if (t.getMessage() != null) {
                                Log.e("msg_chat_error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {

                                    Toast.makeText(ServiceUploadAttachment.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ServiceUploadAttachment.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                });
        stopSelf();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
