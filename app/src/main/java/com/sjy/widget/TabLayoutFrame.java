package com.sjy.widget;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sjy.adapter.TabPagerAdpter;
import com.sjy.bushelper.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/10 0010.
 */
public class TabLayoutFrame extends FrameLayout {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TabPagerAdpter mTabPagerAdpter;
    private List<RecyclerRouteView> mlsFragment = new ArrayList<>();
    private FragmentManager mFragmentMgr;

    public TabLayoutFrame(Context context, FragmentManager fragmentManager, ViewGroup parent) {
        super(context);
        mFragmentMgr = fragmentManager;
        View v = LayoutInflater.from(getContext()).inflate(R.layout.routeplant_shower, parent, false);
        InitView(v);
        addView(v);
    }



    public void InitView(View v){
        mTabLayout = (TabLayout) v.findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) v.findViewById(R.id.viewPager);
        //mTabLayout.setTabGravity(GravityCompat.START);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("TabLayoutFrame","onPageSelected" + position);
                RecyclerRouteView v = mlsFragment.get(position);
                v.setTileMapRoute();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTabLayout.setupWithViewPager(mViewPager);

    }

    public void setFragmentList(List<RecyclerRouteView> lsFragment){
        mlsFragment = lsFragment;
        if (mTabPagerAdpter == null){
            mTabPagerAdpter = new TabPagerAdpter(mFragmentMgr,mlsFragment);
        }else {
            mTabPagerAdpter.setBaseData(mlsFragment);
        }
        mViewPager.setAdapter(mTabPagerAdpter);
        mViewPager.setCurrentItem(0);
        lsFragment.get(0).setTileMapRoute();

//        for (int i = 0 ; i < lsFragment.size() ; ++i){
//            mTabLayout.getTabAt(i).setIcon(R.drawable.tailwayicon);
//        }
    }

}
