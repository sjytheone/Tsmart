package com.sjy.baseactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sjy.adapter.OnAdapterItemClickListener;
import com.sjy.adapter.RouteRecyclerAdapter;
import com.sjy.beans.RailWayLineItem;
import com.sjy.beans.RailWayTimeTable;
import com.sjy.beans.RouteItemBean;
import com.sjy.bushelper.MyApp;
import com.sjy.bushelper.R;
import com.sjy.divider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/24.
 */
public class ShowDetailRouteActivity extends BasicActivity{

    private RecyclerView mDetailView;
    private RouteRecyclerAdapter routeAdapter;

    private List<RouteItemBean> mRouteData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showdetail_activity);
        InitView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void InitView(){
        Toolbar tb = (Toolbar)findViewById(R.id.tb_functiontoolbar);
        tb.setTitleTextColor(getResources().getColor(R.color.theme_white));
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("");


        Bundle bd = getIntent().getBundleExtra("information");
        String strRouteID = bd.getString("strRouteID");

        mDetailView = (RecyclerView) findViewById(R.id.activity_recyclerDetail);
        mDetailView.setLayoutManager(new LinearLayoutManager(this));
        mDetailView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(R.color.deep_dark).size(2).build());
        mDetailView.setItemAnimator(new DefaultItemAnimator());

        String strRouteName = "";
        RailWayLineItem itemBean = MyApp.theIns().getLineItem(strRouteID);
        if (itemBean != null){
            strRouteName = itemBean.getRailWayLineName();
            List<String> stations = itemBean.getRailWayStations();
            for (String ibs : stations) {
                RouteItemBean routeib = MyApp.theIns().findStation(ibs);
                if (routeib != null) {
                    mRouteData.add(routeib);
                }
            }
        }

        String strDestination = bd.getString("strDestination");
        String strTime = bd.getString("strTime");
        LinearLayout container = (LinearLayout) findViewById(R.id._detailtext_container);
        container.removeAllViews();
        TextView tvName = new TextView(getApplicationContext());
        tvName.setTextColor(getResources().getColor(R.color.white));
        tvName.setTextSize(30);
        tvName.setText(strRouteName);
        container.addView(tvName);

        TextView tv = null;
        if (strDestination != null && !strDestination.isEmpty()){
            tv = new TextView(getApplicationContext());
            tv.setTextColor(getResources().getColor(R.color.white));
            tv.setTextSize(20);
            tv.setText(strDestination);
            container.addView(tv);
        }

        if (strTime != null && !strTime.isEmpty()){
            tv = new TextView(getApplicationContext());
            tv.setTextColor(getResources().getColor(R.color.white));
            tv.setTextSize(16);
            tv.setText(strTime);
            container.addView(tv);
        }


        routeAdapter = new RouteRecyclerAdapter(mRouteData,getApplicationContext(),RouteRecyclerAdapter.ROUTE_FRAGMENT);
        mDetailView.setAdapter(routeAdapter);
        routeAdapter.notifyDataSetChanged();


        routeAdapter.setOnAdapterItemClickListener(new OnAdapterItemClickListener() {
            @Override
            public void onAdapterItemClickListener(View v, int postion) {
                RouteItemBean stationItem = mRouteData.get(postion);
                if (stationItem != null){
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), ShowStationActivity.class);
                    Bundle bd = new Bundle();
                    bd.putString("name",stationItem.getStrStationName());
                    bd.putString("id",stationItem.getStrStationID());
                    bd.putString("desc",stationItem.getStrStationDesc());
                    intent.putExtra("information",bd);
                    startActivity(intent);
                }
            }
        });
    }
}
