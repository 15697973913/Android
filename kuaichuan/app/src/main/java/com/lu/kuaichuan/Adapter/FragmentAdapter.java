package com.lu.kuaichuan.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Lu on 2016/5/14.
 */
public class FragmentAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments;
    private List<String> mTitles;



    public FragmentAdapter (FragmentManager fm, List<Fragment> fragments, List<String> titles){
        super(fm);
        mFragments = fragments;
        mTitles = titles;
    }

        @Override
    public Fragment getItem(int position){
        return mFragments.get(position);
    }

    @Override
    public int getCount(){
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position){
        return  mTitles.get(position);
    }
}

