package com.sjy.baseactivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sjy.adapter.OnAdapterItemClickListener;
import com.sjy.adapter.RailTimeAdapter;
import com.sjy.adapter.RailTimeRecyclerAdapter;
import com.sjy.adapter.RouteRecyclerAdapter;
import com.sjy.beans.RailItemBean;
import com.sjy.beans.RailWayTimeTable;
import com.sjy.beans.RouteItemBean;
import com.sjy.beans.TimeItemBean;
import com.sjy.bushelper.MyApp;
import com.sjy.bushelper.R;
import com.sjy.divider.HorizontalDividerItemDecoration;
import com.sjy.widget.MarqueeTextView;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ShowStationActivity extends BasicActivity implements View.OnClickListener{

    private RecyclerView mRailRecyclerView;
    private List<RailItemBean> mlsData = new ArrayList<>();
    private RailTimeRecyclerAdapter mRailTimeAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showdetail_activity);
        InitView();
    }



    protected void InitView(){

        Bundle bd = getIntent().getBundleExtra("information");
        //bd.putString("name", strName);
        //bd.putString("id", strID);
        //getIntent().getExtras();
        Toolbar tb = (Toolbar)findViewById(R.id.tb_functiontoolbar);
        tb.setTitleTextColor(getResources().getColor(R.color.theme_white));
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getSupportActionBar().setDisplayShowCustomEnabled(true);


        mRailRecyclerView = (RecyclerView) findViewById(R.id.activity_recyclerDetail);
        mRailRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRailRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(R.color.deep_dark).size(2).build());
        mRailRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //mStationEdit.setText(bd.getString("desc"));
        String strName = bd.getString("name");
        String strDesc1 = bd.getString("desc");

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id._detailtext_container);
        linearLayout.removeAllViews();
        TextView tvName = new TextView(getApplicationContext());
        tvName.setTextColor(getResources().getColor(R.color.white));
        tvName.setTextSize(30);
        tvName.setText(strName);
        linearLayout.addView(tvName);

        if (strDesc1 != null && !strDesc1.isEmpty()){
            TextView tvDesc = new TextView(getApplicationContext());
            tvDesc.setTextColor(getResources().getColor(R.color.white));
            tvDesc.setTextSize(20);
            tvDesc.setText(strDesc1);
            linearLayout.addView(tvDesc);
        }

        String strID = bd.getString("id");
        List<RailWayTimeTable> railTimeTable = MyApp.theIns().getRailWayTimeTables();
        Map<String,TimeItemBean> mp = null;
        for (RailWayTimeTable item : railTimeTable){
            mp = item.getStationVtimeMap();
            for (Map.Entry<String,TimeItemBean> entry : mp.entrySet()){
                if (entry.getKey().compareTo(strID) == 0){
                    RailItemBean railItem = new RailItemBean();
                    railItem.setRailID(item.getmRailWayTrainID());
                    String strDestination = item.getDestinationID();
                    RouteItemBean statinon = MyApp.theIns().findStation(strDestination);
                    String strDesc = "到站时间:" + entry.getValue().getstrTime();
                    if (statinon != null)
                        strDesc += " 列车开往:" + statinon.getStrStationName();
                    railItem.setRailDesc(strDesc);
                    railItem.setMilisortTimes(entry.getValue().getMillisTime());
                    mlsData.add(railItem);
                }
            }
        }

        Collections.sort(mlsData, new MiliTimeComparator());

        mRailTimeAdapter = new RailTimeRecyclerAdapter(this,mlsData);
        mRailRecyclerView.setAdapter(mRailTimeAdapter);
        mRailTimeAdapter.notifyDataSetChanged();

        //mDetailView
        mRailTimeAdapter.setOnAdapterItemClickListener(new OnAdapterItemClickListener() {
            @Override
            public void onAdapterItemClickListener(View v, int postion) {
                RailItemBean item = mlsData.get(postion);
                if (item == null)
                    return;

                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), ShowRailTimeTable.class);
                Bundle bd = new Bundle();
                bd.putString("strID",item.getRailID());
                bd.putString("strDesc",item.getRailDesc());
                intent.putExtra("information",bd);
                startActivity(intent);
            }
        });

        //mRailRecyclerView.scrollToPosition();
        mHandler.post(mRunnable);
    }

    @Override
    public void onClick(View v) {
    }

    public class MiliTimeComparator implements Comparator<Object> {
        public int compare(Object o1, Object o2) {

            long time1 = ((RailItemBean) o1).getMilisortTimes();
            long time2 = ((RailItemBean) o2).getMilisortTimes();

            return Long.valueOf(time1).compareTo(Long.valueOf(time2));
        }
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            UpdateArrayTime();

            mHandler.postDelayed(mRunnable, 20*1000);
        }
    };

    private Handler mHandler = new Handler();


    public void UpdateArrayTime(){

        long curTime = System.currentTimeMillis();
        long interv = Long.MAX_VALUE;
        int index = 0,count = 0;
        for (RailItemBean routeItem : mlsData) {
            long itemTime = routeItem.getMilisortTimes();
            long tempInterv = itemTime - curTime;
            if(tempInterv >= 0 && tempInterv < interv){
                interv = tempInterv;
                index = count;
            }
            count++;
        }
        mRailRecyclerView.scrollToPosition(index);
    }
}
