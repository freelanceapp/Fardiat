package com.fardiat.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerOrderAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private List<String> title;

    public ViewPagerOrderAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        fragmentList = new ArrayList<>();
        title = new ArrayList<>();
    }


    public void addFragments(List<Fragment> fragmentList) {

        this.fragmentList.addAll(fragmentList);
    }

    public void addTitles(List<String> title) {
        this.title.addAll(title);

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title.get(position);
    }
}
