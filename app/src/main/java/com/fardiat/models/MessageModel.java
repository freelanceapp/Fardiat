package com.fardiat.models;

import java.io.Serializable;

public class MessageModel implements Serializable {

    private int id;
    private int chat_room_id;
    private int from_user_id;
    private int to_user_id;
    private String message_kind;
    private String message;
    private String file_link;
    private long date;
    private String is_read;
    private String created_at;
    private String updated_at;
    private RoomModel room;
    private UserModel.User from_user;
    private UserModel.User to_user;
    private boolean isLoaded = false;
    private int progress = 0;
    private int max_duration =0;
    private boolean isImageLoaded = false;


    public MessageModel(int id, int chat_room_id, int from_user_id, int to_user_id, String message_kind, String message, String file_link, long date, RoomModel room, UserModel.User from_user, UserModel.User to_user) {
        this.id = id;
        this.chat_room_id = chat_room_id;
        this.from_user_id = from_user_id;
        this.to_user_id = to_user_id;
        this.message_kind = message_kind;
        this.message = message;
        this.file_link = file_link;
        this.date = date;
        this.room = room;
        this.from_user = from_user;
        this.to_user = to_user;
    }

    public int getId() {
        return id;
    }

    public int getChat_room_id() {
        return chat_room_id;
    }

    public int getFrom_user_id() {
        return from_user_id;
    }

    public int getTo_user_id() {
        return to_user_id;
    }

    public String getMessage_kind() {
        return message_kind;
    }

    public String getMessage() {
        return message;
    }

    public String getFile_link() {
        return file_link;
    }

    public long getDate() {
        return date;
    }

    public String getIs_read() {
        return is_read;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public RoomModel getRoom() {
        return room;
    }

    public UserModel.User getFrom_user() {
        return from_user;
    }

    public UserModel.User getTo_user() {
        return to_user;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getMax_duration() {
        return max_duration;
    }

    public void setMax_duration(int max_duration) {
        this.max_duration = max_duration;
    }

    public boolean isImageLoaded() {
        return isImageLoaded;
    }

    public void setImageLoaded(boolean imageLoaded) {
        isImageLoaded = imageLoaded;
    }
}
