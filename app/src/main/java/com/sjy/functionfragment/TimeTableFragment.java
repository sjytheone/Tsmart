package com.sjy.functionfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sjy.adapter.OnAdapterItemClickListener;
import com.sjy.adapter.RailTimeRecyclerAdapter;
import com.sjy.adapter.RouteLineRecyclerAdapter;
import com.sjy.adapter.TabPagerAdpter;
import com.sjy.baseactivity.ShowDetailRouteActivity;
import com.sjy.baseactivity.ShowDetailTimeTableActivity;
import com.sjy.baseactivity.ShowRailTimeTable;
import com.sjy.beans.RailItemBean;
import com.sjy.beans.RailWayLineItem;
import com.sjy.beans.RailWayTimeTable;
import com.sjy.bushelper.MyApp;
import com.sjy.bushelper.R;
import com.sjy.divider.HorizontalDividerItemDecoration;
import com.sjy.listener.IFragemDataListener;
import com.sjy.widget.RecyclerRouteView;

import java.util.ArrayList;
import java.util.List;

public class TimeTableFragment extends Fragment implements IFragemDataListener {

    private RecyclerView mRecyclerView;
    private RouteLineRecyclerAdapter mRecyclerAdapter;
    private List<RailWayLineItem> items;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_time_table,container,false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.time_table_recyclerview);
        //mRecyclerView.setOnItemClickListener(mItemClickListener);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).color(ContextCompat.getColor(getContext(),R.color.deep_dark)).size(2).build());
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        InitData();
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void InitData(){
        items = MyApp.theIns().getRailWayLineItems();
        mRecyclerAdapter = new RouteLineRecyclerAdapter(getContext(),items,RouteLineRecyclerAdapter.FLAG_TIMETABLE);

        mRecyclerAdapter.setOnAdapterItemClickListener(new OnAdapterItemClickListener() {
            @Override
            public void onAdapterItemClickListener(View v, int postion) {
                RailWayLineItem itemBean = items.get(postion);
                Intent intent = new Intent();
                intent.setClass(getContext(),ShowDetailTimeTableActivity.class);
                Bundle bd = new Bundle();
                String strMain= String.format("起点:%s->终点:%s",itemBean.getFirstStation(),itemBean.getLastStation());
                String strSub= String.format("首班车:%s 末班车:%s",itemBean.getFirstRailWayTime(),itemBean.getLastRailWayTime());
                bd.putString("strRouteID",itemBean.getRailWayLineID());
                bd.putString("strDestination",strMain);
                bd.putString("strTime",strSub);
                intent.putExtra("information",bd);
                startActivity(intent);
            }
        });

        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerAdapter.notifyDataSetChanged();

    }

    @Override
    public void OnDataChanged(Bundle bd) {

    }

    @Override
    public boolean doBackPress() {
        return false;
    }

}
