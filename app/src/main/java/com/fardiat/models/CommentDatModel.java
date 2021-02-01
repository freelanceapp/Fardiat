package com.fardiat.models;

import java.io.Serializable;
import java.util.List;

public class CommentDatModel implements Serializable {
    private List<CommentsModel> data;

    public List<CommentsModel> getData() {
        return data;
    }
}
