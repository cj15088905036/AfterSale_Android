package com.mapsoft.aftersale.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/11/6.
 * 自定义fragAdapter
 */

public class MyFragAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    public MyFragAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        fragmentList=fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
