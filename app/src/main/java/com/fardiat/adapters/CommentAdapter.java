package com.fardiat.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fardiat.R;
import com.fardiat.activities_fragments.activity_home.fragments.Fragment_Main;
import com.fardiat.databinding.CommentRowBinding;
import com.fardiat.databinding.ProductRowBinding;
import com.fardiat.models.CommentsModel;
import com.fardiat.models.ProductModel;
import com.fardiat.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CommentsModel> list;
    private Context context;
    private LayoutInflater inflater;

    public CommentAdapter(List<CommentsModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommentRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.comment_row, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            MyHolder myHolder = (MyHolder) holder;
            myHolder.binding.setModel(list.get(position));


        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public CommentRowBinding binding;

        public MyHolder(@NonNull CommentRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
