package com.sjy.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;

import com.sjy.bushelper.R;
import com.sjy.widget.RecyclerRouteView;

import java.util.List;

/**
 * Created by Administrator on 2016/9/9 0009.
 */
public class TabPagerAdpter extends FragmentStatePagerAdapter{


    private List<RecyclerRouteView> mlsFragment;

    public TabPagerAdpter(FragmentManager fm, List<RecyclerRouteView> lsFragment){
        super(fm);
        mlsFragment = lsFragment;
    }
    @Override
    public int getCount() {
        return mlsFragment.size();
    }

    @Override
    public Fragment getItem(int position) {

        return mlsFragment.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "方案" + (position + 1);
    }

    public void setBaseData(List<RecyclerRouteView> lsFragment){
        mlsFragment = lsFragment;
        notifyDataSetChanged();
    }
}
