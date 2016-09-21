package com.sjy.baseactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.sjy.adapter.OnAdapterItemClickListener;
import com.sjy.adapter.RailTimeRecyclerAdapter;
import com.sjy.beans.RailItemBean;
import com.sjy.beans.RailWayLineItem;
import com.sjy.beans.RailWayTimeTable;
import com.sjy.bushelper.MyApp;
import com.sjy.bushelper.R;
import com.sjy.divider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/20 0020.
 */

public class ShowDetailTimeTableActivity extends BasicActivity {
    private RecyclerView mRailRecyclerView;
    private List<RailItemBean> mlsData = new ArrayList<>();
    private RailTimeRecyclerAdapter mRailTimeAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showdetail_activity);
        Bundle bd = getIntent().getBundleExtra("information");
        InitView(bd);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bd = intent.getBundleExtra("information");
        InitView(bd);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void InitView(Bundle bd) {
        Toolbar tb = (Toolbar) findViewById(R.id.tb_functiontoolbar);
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


        mRailRecyclerView = (RecyclerView) findViewById(R.id.activity_recyclerDetail);
        mRailRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRailRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(R.color.deep_dark).size(2).build());
        mRailRecyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerViewHeader header = (RecyclerViewHeader) findViewById(R.id.recycler_routeplant_viewheader);
        header.attachTo(mRailRecyclerView);
        //mStationEdit.setText(bd.getString("desc"));
        String strRouteID = bd.getString("strRouteID");
        String strRouteName = "";
        RailWayLineItem itemBean = MyApp.theIns().getLineItem(strRouteID);
        if (itemBean != null) {
            strRouteName = itemBean.getRailWayLineName();

            LinearLayout linearLayout = (LinearLayout) findViewById(R.id._detailtext_container);
            linearLayout.removeAllViews();
            TextView tvName = new TextView(getApplicationContext());
            tvName.setTextColor(getResources().getColor(R.color.white));
            tvName.setTextSize(30);
            tvName.setText(strRouteName);
            linearLayout.addView(tvName);

            String strDestination = bd.getString("strDestination");
            String strTime = bd.getString("strTime");
            if (strDestination != null && !strDestination.isEmpty()) {
                TextView tvDesc = new TextView(getApplicationContext());
                tvDesc.setTextColor(getResources().getColor(R.color.white));
                tvDesc.setTextSize(15);
                tvDesc.setText(strDestination);
                linearLayout.addView(tvDesc);
            }

            if (strTime != null && !strTime.isEmpty()) {
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(getResources().getColor(R.color.white));
                tv.setTextSize(15);
                tv.setText(strTime);
                linearLayout.addView(tv);
            }

            List<RailWayTimeTable> timeTables = MyApp.theIns().getRailWayTimeTables();
            for (RailWayTimeTable it : timeTables) {
                if (it.getBelongRouteID().contains(strRouteID)) {
                    RailItemBean bean = new RailItemBean();
                    bean.setRailID(it.getmRailWayTrainID());
                    bean.setRailDesc(it.getTimeDesc());
                    mlsData.add(bean);
                }
            }


            // Collections.sort(mlsData, new ShowStationActivity.MiliTimeComparator());

            mRailTimeAdapter = new RailTimeRecyclerAdapter(this, mlsData);
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
                    bd.putString("strID", item.getRailID());
                    bd.putString("strDesc", item.getRailDesc());
                    intent.putExtra("information", bd);
                    startActivity(intent);
                }
            });
        }
    }
}
