package com.fardiat.singleton;


import com.fardiat.models.ItemCartModel;

import java.util.ArrayList;
import java.util.List;

public class CartSingleton {

    private static CartSingleton instance = null;
    private List<ItemCartModel> itemCartModelList = new ArrayList<>();

    private CartSingleton() {
    }

    public static synchronized CartSingleton newInstance() {
        if (instance == null) {
            instance = new CartSingleton();
        }
        return instance;
    }

    public void addItem(ItemCartModel itemCartModel) {
        int pos = getItemPos(itemCartModel);

        if (pos == -1) {
            itemCartModelList.add(itemCartModel);
        } else {
            itemCartModelList.set(pos, itemCartModel);
        }


    }

    private int getItemPos(ItemCartModel itemCartModel) {


        int pos = -1;
        for (int i = 0; i < itemCartModelList.size(); i++) {
            if (itemCartModel.getProduct_id() == itemCartModelList.get(i).getProduct_id()) {

                pos = i;
                return pos;
            }
        }

        return pos;
    }

    public int getItemCount() {
        return itemCartModelList.size();
    }

    public void deleteItem(int pos) {
        this.itemCartModelList.remove(pos);
    }

    public List<ItemCartModel> getItemCartModelList() {
        return itemCartModelList;
    }

    public void clear() {
        //instance = null;
        itemCartModelList.clear();
    }


}
