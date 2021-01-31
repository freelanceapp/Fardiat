package com.fardiat.models;

import java.io.Serializable;
import java.util.List;

public class MessageDataModel implements Serializable {
    private RoomModel room;
    private Data messages;

    public RoomModel getRoom() {
        return room;
    }

    public Data getMessages() {
        return messages;
    }

    public static class Data implements Serializable {
        private int current_page;
        private List<MessageModel> data;

        public int getCurrent_page() {
            return current_page;
        }

        public List<MessageModel> getData() {
            return data;
        }
    }
}
