package com.cn.mv.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/7/17.
 */
public class MyAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;
    public MyAdapter(List<Fragment> fragments, FragmentManager fm) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return (Fragment) fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size() > 0 ? fragments.size() : 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String str= (String) fragments.get(position).getArguments().get("title");
        return str;
    }
}
