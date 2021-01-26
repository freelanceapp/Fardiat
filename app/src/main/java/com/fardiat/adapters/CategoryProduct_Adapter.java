package com.fardiat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fardiat.R;
import com.fardiat.activities_fragments.activity_home.fragments.Fragment_Main;
import com.fardiat.databinding.OfferRowBinding;
import com.fardiat.models.ProductModel;
import com.fardiat.singleton.CartSingleton;


import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class CategoryProduct_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ProductModel> list;
    private Context context;
    private LayoutInflater inflater;
    private String lang;
    private int i = 0;
    private Fragment fragment;
    private CartSingleton cartSingleton;

    public CategoryProduct_Adapter(List<ProductModel> list, Context context, Fragment fragment) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        Paper.init(context);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        this.fragment = fragment;
        cartSingleton = CartSingleton.newInstance();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        OfferRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.offer_row, parent, false);
        return new EventHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder eventHohlder, int position) {

        EventHolder myHolder = (EventHolder) eventHohlder;
        myHolder.binding.setModel(list.get(position));
        myHolder.binding.setLang(lang);
        myHolder.itemView.setOnClickListener(view -> {
            if (fragment instanceof Fragment_Main) {

                Fragment_Main fragment_main = (Fragment_Main) fragment;
               // fragment_main.setItemDataOffers(list.get(myHolder.getAdapterPosition()));
            }


        });




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class EventHolder extends RecyclerView.ViewHolder {
        public OfferRowBinding binding;

        public EventHolder(@NonNull OfferRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }
    }


}
