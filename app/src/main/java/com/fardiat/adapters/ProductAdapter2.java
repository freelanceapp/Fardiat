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
import com.fardiat.activities_fragments.activity_profile_products.ProfileProductsActivity;
import com.fardiat.databinding.ProductRow2Binding;
import com.fardiat.databinding.ProductRowBinding;
import com.fardiat.models.ProductModel;
import com.fardiat.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ProductModel> list;
    private Context context;
    private LayoutInflater inflater;
    private ProfileProductsActivity activity;

    public ProductAdapter2(List<ProductModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (ProfileProductsActivity) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProductRow2Binding binding = DataBindingUtil.inflate(inflater, R.layout.product_row2, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            MyHolder myHolder = (MyHolder) holder;
            myHolder.binding.setModel(list.get(position));

            myHolder.itemView.setOnClickListener(view -> {

                activity.setProductItemData(list.get(myHolder.getAdapterPosition()));

            });

            if (list.get(myHolder.getAdapterPosition()).getProducts_images().size() > 0) {
                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + list.get(myHolder.getAdapterPosition()).getProducts_images().get(0).getName())).fit().into(myHolder.binding.imageAds);

            }

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public ProductRow2Binding binding;

        public MyHolder(@NonNull ProductRow2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
