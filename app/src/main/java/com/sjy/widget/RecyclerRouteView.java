package com.sjy.widget;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.sjy.adapter.RoutePlanRecyclerAdapter;
import com.sjy.beans.BigMapstationInfo;
import com.sjy.beans.RoutePlanDetailItem;
import com.sjy.bigimagemap.BigMapFlagDrawOverlay;
import com.sjy.bigimagemap.BigTileMap;
import com.sjy.bushelper.MyApp;
import com.sjy.bushelper.R;
import com.sjy.divider.HorizontalDividerItemDecoration;
import com.sjy.net.StaPassTime;
import com.sjy.net.StationPassTime;
import com.sjy.utils.TimeUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2016/9/9 0009.
 */
public class RecyclerRouteView extends Fragment{

    private StationPassTime stationPassTime;
    private RecyclerView mRecyclerView;
    private RoutePlanRecyclerAdapter mRoutePlanRecyclerAdapter;
    private TextView mRoutePlanDetailShow;
    private BigTileMap mTileMap;

    //private ListView mListView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //stationPassTime = (StationPassTime) getArguments().getSerializable("data");
    }

    public void setStationPassTime(StationPassTime passTime){
        this.stationPassTime = passTime;
    }

    public void setTileMap(BigTileMap tileMap){
        mTileMap = tileMap;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recycler_routeplant, null, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Set<String> filter = new LinkedHashSet<>();
        List<StaPassTime> ls = stationPassTime.StaList;
        List<RoutePlanDetailItem> lsDetailItem = new ArrayList<>();
        for (StaPassTime passTime : ls){

            String strStationName = "";
            BigMapstationInfo info = MyApp.theIns().findBigmapStationByBelongID(passTime.StaCode);
            if (info != null){
                strStationName = info.getStationName();
            }
            boolean bcontain = filter.contains(strStationName);
            if(bcontain)
                continue;
            filter.add(strStationName);
            RoutePlanDetailItem ib = new RoutePlanDetailItem();
            ib.setStrStationID(passTime.StaCode);
            ib.setStrStationName(strStationName);

            String strArrtime = TimeUtils.getTime(passTime.StaTime,TimeUtils.DATE_FORMAT_DATE_HM);
            ib.setStrArrayTime(strArrtime);
            lsDetailItem.add(ib);
        }
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_routeplant_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).color(ContextCompat.getColor(getContext(), R.color.deep_dark)).size(2).build());
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerViewHeader header = (RecyclerViewHeader)view.findViewById(R.id.recycler_routeplant_viewheader);
        header.attachTo(mRecyclerView);
        mRoutePlanDetailShow = (TextView) view.findViewById(R.id.recycler_routeplantdetailshow);
        mRoutePlanRecyclerAdapter = new RoutePlanRecyclerAdapter(lsDetailItem,getContext());
        mRecyclerView.setAdapter(mRoutePlanRecyclerAdapter);

        String costTime = TimeUtils.formatTime(stationPassTime.costtime);
        String strBoardingtime = TimeUtils.getTime(stationPassTime.lastboardingtime,TimeUtils.DATE_FORMAT_DATE_HM);
        //String strCosttime = TimeUtils.getTime(stationPassTime.costtime,TimeUtils.DATE_FORMAT_DATE_HM);
        String str = "预计耗时:" + costTime;
        str += "\r\n最晚上车时间:" + strBoardingtime;

        mRoutePlanDetailShow.setText(str);


//        mListView = (ListView) view.findViewById(R.id.list_routeview);
//        RoutePlanListAdapter adapter = new RoutePlanListAdapter(getContext(),lsDetailItem);
//        mListView.setAdapter(adapter);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void setTileMapRoute(){
        if (mTileMap != null){
            mTileMap.RemoveAllNaviOptions();
            List<StaPassTime> ls = stationPassTime.StaList;
            int ncounter = 0;
            for (StaPassTime staPassTime : ls){
                BigMapstationInfo binfo = MyApp.theIns().findBigmapStationByBelongID(staPassTime.StaCode);
                if (binfo != null){
                    BigMapFlagDrawOverlay overlay = new BigMapFlagDrawOverlay();
                    Bitmap bmap = MyApp.theIns().getBitmapFromAssets("bigmaps/header_floor_text_56.png");
                    if (ncounter == 0){
                        bmap = MyApp.theIns().getBitmapFromAssets("bigmaps/transit_start_ic.png");
                    }else if (ncounter == ls.size() - 1){
                        bmap = MyApp.theIns().getBitmapFromAssets("bigmaps/transit_end_ic.png");
                    }
                    ++ncounter;
                    overlay.setBackGroundDrawable(bmap);
                    overlay.setPoint(new Point((int) binfo.getDotX(), (int) binfo.getDotY()));
                    mTileMap.AddNaviOption(overlay);
                    mTileMap.invalidate();
                }

            }
        }
        if (mRecyclerView != null){
            mRecyclerView.scrollToPosition(0);
            mRecyclerView.requestFocus();
        }
    }
}
