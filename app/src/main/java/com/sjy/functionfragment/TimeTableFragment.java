package com.sjy.functionfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sjy.adapter.OnAdapterItemClickListener;
import com.sjy.adapter.RailTimeRecyclerAdapter;
import com.sjy.baseactivity.ShowRailTimeTable;
import com.sjy.beans.RailItemBean;
import com.sjy.beans.RailWayTimeTable;
import com.sjy.beans.RouteLineItemBean;
import com.sjy.bushelper.MyApp;
import com.sjy.bushelper.R;
import com.sjy.divider.HorizontalDividerItemDecoration;
import com.sjy.listener.IFragemDataListener;

import java.util.ArrayList;
import java.util.List;

public class TimeTableFragment extends Fragment implements IFragemDataListener {

    private RecyclerView mRecyclerView;
    private RailTimeRecyclerAdapter mRailTimeRecyclerAdapter;
    private List<RailItemBean> mRailData = new ArrayList<>();
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

    public void InitData(){
        mRailData.clear();
        List<RailWayTimeTable> timeTables = MyApp.theIns().getRailWayTimeTables();
        List<RouteLineItemBean> routeLines = MyApp.theIns().getAllRouteLine();
        for (RouteLineItemBean routeItembean : routeLines){
            RailItemBean descItem = new RailItemBean();
            descItem.setRailID(routeItembean.getStrRouteName());
            descItem.setType(RailItemBean.DESCRIBITEMBEAN);
            mRailData.add(descItem);
            for (RailWayTimeTable it : timeTables){
                if (it.getBelongRouteID().contains(routeItembean.getRouteID())){
                    RailItemBean bean = new RailItemBean();
                    bean.setRailID(it.getmRailWayTrainID());
                    bean.setRailDesc(it.getTimeDesc());
                    mRailData.add(bean);
                }
            }

        }

        mRailTimeRecyclerAdapter = new RailTimeRecyclerAdapter(getContext(),mRailData);
        mRecyclerView.setAdapter(mRailTimeRecyclerAdapter);
        mRailTimeRecyclerAdapter.setOnAdapterItemClickListener(mItemClickListener);
        mRailTimeRecyclerAdapter.notifyDataSetChanged();
    }

    private OnAdapterItemClickListener mItemClickListener = new OnAdapterItemClickListener() {
        @Override
        public void onAdapterItemClickListener(View v, int postion) {
            RailItemBean item = mRailData.get(postion);
            if (item == null)
                return;


            Intent intent = new Intent();
            //intent.setAction("com.sjy.baseactivity.ShowStationActivity");
            intent.setClass(getActivity().getApplicationContext(), ShowRailTimeTable.class);
            Bundle bd = new Bundle();
            bd.putString("strID",item.getRailID());
            bd.putString("strDesc",item.getRailDesc());
            intent.putExtra("information",bd);
            startActivity(intent);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.action_routeselect:
//                ShowPopWindow();
//                break;
//            default:
//                break;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnDataChanged(Bundle bd) {

    }

    @Override
    public boolean doBackPress() {
        return false;
    }

}
