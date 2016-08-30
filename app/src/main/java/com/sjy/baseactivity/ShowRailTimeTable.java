package com.sjy.baseactivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sjy.adapter.OnAdapterItemClickListener;
import com.sjy.adapter.RouteRecyclerAdapter;
import com.sjy.beans.RailWayTimeTable;
import com.sjy.beans.RouteItemBean;
import com.sjy.beans.TimeItemBean;
import com.sjy.bushelper.MyApp;
import com.sjy.bushelper.R;
import com.sjy.divider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author sjy
 *
 * Created by Administrator on 2016/4/8.
 * @category 显示
 *
 *
 */
public class ShowRailTimeTable extends BasicActivity {

    private RecyclerView mDetailView;
    private RouteRecyclerAdapter routeAdapter;

    private List<RouteItemBean> mRouteData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView();
        setContentView(R.layout.showdetail_activity);
        InitView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.post(mRunnable);
    }

    public void InitView(){
        mHandler.removeCallbacks(mRunnable);

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
        String strRailID = bd.getString("strID");
        String strDesc = bd.getString("strDesc");

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id._detailtext_container);
        linearLayout.removeAllViews();
        TextView tvName = new TextView(getApplicationContext());
        tvName.setTextColor(getResources().getColor(R.color.white));
        tvName.setTextSize(30);
        tvName.setText(strRailID);
        linearLayout.addView(tvName);

        if (strDesc != null && !strDesc.isEmpty()){
            TextView tvDesc = new TextView(getApplicationContext());
            tvDesc.setTextColor(getResources().getColor(R.color.white));
            tvDesc.setTextSize(20);
            tvDesc.setText(strDesc);
            linearLayout.addView(tvDesc);
        }

        mDetailView = (RecyclerView) findViewById(R.id.activity_recyclerDetail);
        mDetailView.setLayoutManager(new LinearLayoutManager(this));
        mDetailView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(R.color.deep_dark).size(2).build());
        mDetailView.setItemAnimator(new DefaultItemAnimator());

        List<RailWayTimeTable> timeTables = MyApp.theIns().getRailWayTimeTables();
        List<RouteItemBean> allStations = MyApp.theIns().getJsonStations();

        Map<String,TimeItemBean> mapStationVTime = null;
        for (RailWayTimeTable itm : timeTables){
            if (itm.getmRailWayTrainID().compareTo(strRailID) == 0){
                mapStationVTime = itm.getStationVtimeMap();

                break;
            }
        }
        if (mapStationVTime != null){
            Set<Map.Entry<String,TimeItemBean>>entrySet = mapStationVTime.entrySet();
            for (Map.Entry<String,TimeItemBean> entry : entrySet){
                String stationID = entry.getKey();
                String strTime = entry.getValue().getstrTime();

                for (RouteItemBean ib : allStations){
                    if (ib.getStrStationID().compareTo(stationID) == 0){
                        ib.setStrArrayTime(strTime);
                        //ib.getStrArrayTime()
                        mRouteData.add(ib);
                    }
                }
            }
        }

        routeAdapter = new RouteRecyclerAdapter(mRouteData,this,RouteRecyclerAdapter.RAILTIMETABLE_ACTIVITY);
        mDetailView.setAdapter(routeAdapter);
        routeAdapter.notifyDataSetChanged();

        //mDetailView
        routeAdapter.setOnAdapterItemClickListener(new OnAdapterItemClickListener() {
            @Override
            public void onAdapterItemClickListener(View v, int postion) {
                RouteItemBean ib = (RouteItemBean) mRouteData.get(postion);
                Intent intent = new Intent();
                //intent.setAction("com.sjy.baseactivity.ShowStationActivity");
                intent.setClass(getApplicationContext(), ShowStationActivity.class);
                Bundle bd = new Bundle();
                bd.putString("name", ib.getStrStationName());
                bd.putString("id", ib.getStrStationID());
                Bundle info = getIntent().getBundleExtra("information");
                bd.putString("desc", ib.getStationFragmentDes());
                intent.putExtra("information", bd);
                startActivity(intent);
            }
        });

        mHandler.post(mRunnable);
    }


    public void UpdateListTime(){
        Time t=new Time();
        t.setToNow(); // 取得系统时间。
        int hour = t.hour; // 0-23
        int minute = t.minute;

        for (RouteItemBean routeItem : mRouteData){
            String strArrayTime = routeItem.getStrArrayTime();
            String[] atime = strArrayTime.split(":");
            int ah = Integer.parseInt(atime[0]);
            int am = Integer.parseInt(atime[1]);
            if (ah == hour && (am - minute >= 0) && (am - minute) <=2){
                routeItem.setnBusCount(1);
                routeItem.setTailArrived(true);
            }else {
                routeItem.setnBusCount(0);
                routeItem.setTailArrived(false);
            }
        }

        routeAdapter.setAdapterBase(mRouteData);
        routeAdapter.notifyDataSetChanged();
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            UpdateListTime();

            mHandler.postDelayed(mRunnable, 50*1000);
        }
    };

    private Handler mHandler = new Handler();


}
