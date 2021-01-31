package com.fardiat.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.fardiat.R;
import com.fardiat.databinding.LoadMoreRowBinding;
import com.fardiat.databinding.OfferRowBinding;
import com.fardiat.databinding.ProductRowBinding;
import com.fardiat.models.ProductModel;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int DATA_ROW =1;
    private final int LOAD_ROW =2;

    private final String lang;
    private List<ProductModel> list;
    private Context context;
    private LayoutInflater inflater;

    public SearchAdapter(List<ProductModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == DATA_ROW){
            ProductRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.product_row, parent, false);
            return new MyHolder(binding);
        }else {
            LoadMoreRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.load_more_row, parent, false);
            return new LoadMoreHolder(binding);
        }



    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyHolder){
            MyHolder myHolder = (MyHolder) holder;
            myHolder.binding.setModel(list.get(position));
            myHolder.itemView.setOnClickListener(view -> {

            });


        }else {
            LoadMoreHolder loadMoreHolder = (LoadMoreHolder) holder;
            loadMoreHolder.binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            loadMoreHolder.binding.progBar.setIndeterminate(true);
        }




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public ProductRowBinding binding;

        public MyHolder(@NonNull ProductRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


    public class LoadMoreHolder extends RecyclerView.ViewHolder {
        public LoadMoreRowBinding binding;

        public LoadMoreHolder(@NonNull LoadMoreRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;



        }
    }


    @Override
    public int getItemViewType(int position) {
        ProductModel model = list.get(position);
        if (model ==null){
            return LOAD_ROW;
        }else {
            return DATA_ROW;
        }
    }

}
