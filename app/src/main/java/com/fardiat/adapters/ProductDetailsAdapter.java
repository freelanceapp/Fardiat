package com.fardiat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.fardiat.R;
import com.fardiat.databinding.AdditionalAdsRowBinding;
import com.fardiat.databinding.ProductDetailsRowBinding;
import com.fardiat.models.ProductDetailsModel;

import java.util.List;

public class ProductDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ProductDetailsModel> list;
    private Context context;
    private LayoutInflater inflater;

    public ProductDetailsAdapter(List<ProductDetailsModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        AdditionalAdsRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.additional_ads_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        ProductDetailsModel model = list.get(position);

        myHolder.binding.setModel(model);



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public AdditionalAdsRowBinding binding;

        public MyHolder(@NonNull AdditionalAdsRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
