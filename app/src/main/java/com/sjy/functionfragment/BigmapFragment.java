package com.sjy.functionfragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.sjy.baseactivity.SearchActivity;
import com.sjy.baseactivity.ShowRailTimeTable;
import com.sjy.baseactivity.ShowStationActivity;
import com.sjy.beans.BigMapstationInfo;
import com.sjy.bigimagemap.BigMapDrawOverlay;
import com.sjy.bigimagemap.BigTileMap;
import com.sjy.bigimagemap.ITileMapNotify;
import com.sjy.bigimagemap.RailOptionManager;
import com.sjy.bigimagemap.RoutePlanManager;
import com.sjy.bigimagemap.TileMapRailOption;
import com.sjy.bushelper.MyApp;
import com.sjy.bushelper.R;
import com.sjy.listener.IFragemDataListener;
import com.sjy.listener.IRailItemMoveListener;
import com.sjy.net.StationPassTime;
import com.sjy.widget.MarqueeTextView;
import com.sjy.widget.RecyclerRouteView;
import com.sjy.widget.TabLayoutFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/11.
 */
public class BigmapFragment extends Fragment implements ITileMapNotify,IRailItemMoveListener,IFragemDataListener
    ,RoutePlanManager.IRoutePlanListener {

    final static String TAG = "BigmapFragment";

    private BigTileMap mBigImageView;
    private ImageViewState mImageViesState;
    private PopupWindow mPopWindow = null;
    private TextView mTileMapStart;
    private TextView mTileMapEnd;
    private View mViewStationInfo = null;
    private View mViewRailInfo = null;
    private MyApp myApp;
    private RelativeLayout mTileStationInfo;
    private RailOptionManager mRailOptionMgr = null;
    private RoutePlanManager mRoutPlanMgr = null;
    private List<BigMapstationInfo> mStationPoslist;
    private int tileMapMode = BigTileMap.TileMapMode.NORMAL.ordinal();
    //private SlideBottomPanel mSlideBottomPlanel = null;
    private BottomSheetLayout mBottomSheetLayout = null;
    private ImageView mDetailImageView = null;

    private TabLayoutFrame mTabLayoutFrame;
    BottomSheetBehavior mBottomSheetBehavior;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = MyApp.theIns();
        mStationPoslist = MyApp.theIns().getBigMapStationPos_flat();
        //mRailOptionList =
        mPopWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //mPopWindow.setElevation(5.0f);
        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopWindow.setOutsideTouchable(true);

        tileMapMode = getArguments().getInt("tileMode", BigTileMap.TileMapMode.NORMAL.ordinal());

        mRailOptionMgr = new RailOptionManager();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View v = inflater.inflate(R.layout.bigmap_routeplanfragment, container, false);
        View v = inflater.inflate(R.layout.fragment_bigmap, container, false);

        mBigImageView = (BigTileMap)v.findViewById(R.id.bigmap_ImageView);
        InitMapView(mBigImageView);

        mBottomSheetLayout = (BottomSheetLayout) v.findViewById(R.id.bigmap_bottomSheetLayout);
        mBottomSheetLayout.setShouldDimContentView(true);

        //mBottomSheetLayout.seton
        //mPlanDisplayer = LayoutInflater.from(getContext()).inflate(R.layout.routeplant_shower, mBottomSheetLayout, false);
        //InitPlanDisplayer();
        //mTabLayoutFrame = TabLayoutFrame.createInstance(mBottomSheetLayout);

        //LinearLayout bottomSheetViewgroup = (LinearLayout) v.findViewById(R.id.bottom_sheet);

        //FrameLayout frameLayout = (FrameLayout) v.findViewById(R.id.tabLayout_container);


        mTabLayoutFrame = new TabLayoutFrame(getContext(),getFragmentManager(),mBottomSheetLayout);

        mDetailImageView = (ImageView) v.findViewById(R.id.detailImageview);
        mDetailImageView.setClickable(true);
        mDetailImageView.setOnClickListener(mClickListener);

        mRoutPlanMgr = new RoutePlanManager(mBigImageView);

        if (tileMapMode == BigTileMap.TileMapMode.GRID.ordinal()) {
            mRoutPlanMgr.setRoutePlanListener(this);
        }else {
            ImageView imgVie = (ImageView) v.findViewById(R.id.tile_maplayer);
            imgVie.setVisibility(View.VISIBLE);
            imgVie.setOnTouchListener(myTouchListener);
            imgVie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean bShow = mBigImageView.isShowRailOptions();
                    mBigImageView.setShowRailOptions(!bShow);
                }
            });
            mRailOptionMgr.setTileMap(mBigImageView);
            mRailOptionMgr.setRailItemMoveListener(BigmapFragment.this);
            mRailOptionMgr.startRailDataProcess();
        }

        return v;
    }

    protected void InitMapView(final BigTileMap mapView){
        mapView.setMinimumScaleType(2);
        mapView.setTileMapNotify(this);
        mapView.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {
            @Override
            public void onReady() {
                mapView.setMaxScale(2.3f);
                mapView.setMinScale(0.6f);
                mapView.setDoubleTapZoomScale(2.0f);
                mapView.setDoubleTapZoomDuration(1000);

                //mapView.setDoubleTapZoomDpi(500);
            }

            @Override
            public void onImageLoaded() {
                float max = mapView.getMaxScale();
                float min = mapView.getMinScale();
                //float x = 0;
            }

            @Override
            public void onPreviewLoadError(Exception e) {

            }

            @Override
            public void onImageLoadError(Exception e) {

            }

            @Override
            public void onTileLoadError(Exception e) {

            }
        });

        mapView.setZoomEnabled(true);

        //mapView.setImage(ImageSource.asset("bigmaps/metro_sy_plant.png"), mImageViesState);
        mapView.setImage(ImageSource.asset("bigmaps/sy_route.jpg"), mImageViesState);
        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }


    BigMapstationInfo checkStationPos(PointF pointF){
        for (BigMapstationInfo info : mStationPoslist){
            if(info.isPointWithin((int)pointF.x,(int)pointF.y)){
                return  info;
            }
        }
        return null;
    }

    public void showSattionInfo(BigMapstationInfo station){
        if (mViewStationInfo == null){
            mViewStationInfo = getActivity().getLayoutInflater().inflate(R.layout.layout_tile_map_popview, null);
            mTileMapStart = (TextView) mViewStationInfo.findViewById(R.id.tile_map_popview_start);
            mTileMapEnd = (TextView) mViewStationInfo.findViewById(R.id.tile_map_popview_end);
            mTileStationInfo = (RelativeLayout) mViewStationInfo.findViewById(R.id.main_map_popview_station_info);
            mTileMapStart.setOnClickListener(mClickListener);
            mTileMapStart.setClickable(true);

            mTileMapStart.setOnTouchListener(myTouchListener);
            mTileMapEnd.setOnClickListener(mClickListener);
            mTileMapEnd.setOnTouchListener(myTouchListener);
            mTileMapEnd.setClickable(true);
            mTileStationInfo.setOnClickListener(mClickListener);
            mTileStationInfo.setClickable(true);
            mTileStationInfo.setOnTouchListener(myTouchListener);
            if (tileMapMode == BigTileMap.TileMapMode.GRID.ordinal()){
               View vbtn = mViewStationInfo.findViewById(R.id.startandendBtnlayout);
                vbtn.setVisibility(View.VISIBLE);
            }
        }
        mPopWindow.setContentView(mViewStationInfo);

        if (tileMapMode != BigTileMap.TileMapMode.GRID.ordinal()){
            LinearLayout containerdesc = (LinearLayout) mViewStationInfo.findViewById(R.id.map_popview_tvdesccontainer);
            containerdesc.removeAllViews();
            boolean bshowview = false;
            List<String> beLongstations = station.getBelongStationIDs();
            for (String strID: beLongstations){
                Map<String,TileMapRailOption> mp = mRailOptionMgr.getStationNearTimeTable(strID);
                for (Map.Entry<String,TileMapRailOption> entry : mp.entrySet()){
                    TileMapRailOption ioption = entry.getValue();
                    String out = "下次到站时间:" + ioption.getRailArrTime(strID) + "列车方向:" + ioption.getDestination();

                    // <com.sjy.widget.MarqueeTextView android:layout_marginTop="10dp" android:layout_height="30dp" android:textSize="18.0sp" android:textColor="@color/theme_blue" android:id="@+id/popview_stationdesc" android:layout_width="match_parent"/>
                    MarqueeTextView mtv = new MarqueeTextView(getContext());
                    TypedValue typedValue = new  TypedValue();
                    getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
                    int textcolor = typedValue.data;
                    mtv.setTextColor(textcolor);
                    mtv.setTextSize(18);
                    mtv.setSingleLine(true);
                    mtv.setText(out);
                    //LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mtv.getLayoutParams();
                    containerdesc.addView(mtv);
                    bshowview = true;
                    mtv.startFor0();
                }
            }
            //
            if (MyApp.theIns().BusinessInfo.containsKey(station.getStationID())){
                String strout = MyApp.theIns().BusinessInfo.get(station.getStationID());
                MarqueeTextView mtv = new MarqueeTextView(getContext());
                TypedValue typedValue = new  TypedValue();
                getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
                int textcolor = typedValue.data;
                String out = "商业地标:" + strout;
                mtv.setTextColor(textcolor);
                mtv.setTextSize(18);
                mtv.setSingleLine(true);
                mtv.setText(out);
                //LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mtv.getLayoutParams();
                containerdesc.addView(mtv);
                bshowview = true;
                mtv.startFor0();
            }
            //
            LinearLayout linearLayout = (LinearLayout) mViewStationInfo.findViewById(R.id.map_popvie_stattiondesc);
            linearLayout.setVisibility(bshowview ? View.VISIBLE : View.GONE);

        }
        TextView tv = (TextView) mViewStationInfo.findViewById(R.id.tile_map_popview_station_name);
        tv.setText(station.getStationName());
        mTileStationInfo.setTag(station);
        mTileMapStart.setTag(station);
        mTileMapEnd.setTag(station);
        mPopWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);
    }



    public View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tile_map_popview_end:{
                    mPopWindow.dismiss();
                    mPopWindow.setContentView(null);
                    BigMapstationInfo station = (BigMapstationInfo)v.getTag();
                    //String endID = station.getBelongStationIDs().get(0);
                    mRoutPlanMgr.setTerminusStation(station.getStationID());
                }
                    break;
                case R.id.tile_map_popview_start: {
                    mPopWindow.dismiss();
                    mPopWindow.setContentView(null);
                    BigMapstationInfo station = (BigMapstationInfo) v.getTag();
                    //String startID = station.getBelongStationIDs().get(0);
                    mRoutPlanMgr.setOriginationStation(station.getStationID());
                }
                    break;
                case R.id.main_map_popview_station_info: {
                    mPopWindow.dismiss();
                    mPopWindow.setContentView(null);
                    BigMapstationInfo info  = (BigMapstationInfo)v.getTag();
                    //RouteItemBean it = myApp.findStation(info.getBelongStationIDs().get(0));
                    if (info != null){
                        Intent intent = new Intent();
                        //intent.setAction("com.sjy.baseactivity.ShowStationActivity");
                        intent.setClass(getActivity().getApplicationContext(), ShowStationActivity.class);
                        Bundle bd = new Bundle();
                        bd.putString("name", info.getStationName());
                        bd.putString("id", info.getStationID());
                        //bd.putString("desc", info.ge.getStationFragmentDes());
                        intent.putExtra("information", bd);
                        startActivity(intent);
                    }
                }
                break;
                case R.id.layout_tilemap_railinfo: {
                    mPopWindow.dismiss();
                    TileMapRailOption option = (TileMapRailOption) v.getTag();
                    Intent itent = new Intent();
                    //intent.setAction("com.sjy.baseactivity.ShowStationActivity");
                    itent.setClass(getActivity().getApplicationContext(), ShowRailTimeTable.class);
                    Bundle bd = new Bundle();
                    bd.putString("strID", option.getStrRailID());
                    bd.putString("strDesc", "始发:" + option.getStrStartTime() + "-终到:" + option.getStrEndTime());
                    itent.putExtra("information", bd);
                    startActivity(itent);
                }
                break;
                case R.id.detailImageview: {
                    if (mBottomSheetLayout.isSheetShowing()){
                        mBottomSheetLayout.dismissSheet();
                    }else {
                        mBottomSheetLayout.showWithSheetView(mTabLayoutFrame);
                    }
                }
                break;
                default:
                    break;
            }
        }
    };


    private  View.OnTouchListener myTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setBackgroundColor(getResources().getColor(R.color.light_grey));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.setBackgroundColor(Color.TRANSPARENT);
            } else if (event.getAction() == MotionEvent.ACTION_SCROLL){
                System.out.print("output action scroll");
            }
            return false;
        }
    };

    @Override
    public void onMapClicked(MotionEvent event) {
        PointF fpoint = mBigImageView.viewToSourceCoord(event.getX(),event.getY());
        BigMapstationInfo stationInfo = checkStationPos(fpoint);
        if (stationInfo != null){
            showSattionInfo(stationInfo);
            //Toast.makeText(getActivity().getApplicationContext(),stationInfo.getStationID() + stationInfo.getStationName(),Toast.LENGTH_SHORT).show();
        }
//        String tost = String.format("dotX:%f dotY:%f", fpoint.x, fpoint.y);
//        Toast.makeText(getContext(), tost, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOverlayClicked(MotionEvent event,BigMapDrawOverlay overlay) {
        if (overlay != null){
            TileMapRailOption option = (TileMapRailOption) overlay.GetExtarInfo("railinfo");
            if (option != null){
                showRailItemInfo(option);
            }
        }
    }


    @Override
    public void onRailItemMoved() {
        mBigImageView.invalidate();
    }

    public void showRailItemInfo( TileMapRailOption option ){
        if (mViewRailInfo == null){
            mViewRailInfo = getActivity().getLayoutInflater().inflate(R.layout.layout_tile_map_railinfo, null);
            RelativeLayout layout = (RelativeLayout) mViewRailInfo.findViewById(R.id.layout_tilemap_railinfo);
            layout.setOnTouchListener(myTouchListener);
            layout.setOnClickListener(mClickListener);
//            layout.set
            //mViewRailInfo.setAlpha(0.5f);
           // mViewRailInfo.setBackgroundColor(Color.parseColor("#e0000000"));
        }
        mPopWindow.setContentView(mViewRailInfo);
        TextView tvRailID = (TextView) mViewRailInfo.findViewById(R.id.layout_tilemap_railid);
        TextView tvStart = (TextView) mViewRailInfo.findViewById(R.id.layout_tilemap_railinfo_starttime);
        TextView tvEnd = (TextView) mViewRailInfo.findViewById(R.id.layout_tilemap_railinfo_endtime);

        TextView tvCurStation = (TextView) mViewRailInfo.findViewById(R.id.railinfo_currentstaion);
        TextView tvLeavingtime = (TextView) mViewRailInfo.findViewById(R.id.railinfo_leavingtime);
        TextView tvDestination = (TextView) mViewRailInfo.findViewById(R.id.railinfo_destination);

        RelativeLayout layout = (RelativeLayout) mViewRailInfo.findViewById(R.id.layout_tilemap_railinfo);
        layout.setTag(option);
        tvStart.setText(option.getStrStartTime());
        tvEnd.setText(option.getStrEndTime());
        tvRailID.setText(option.getStrRailID());

        tvCurStation.setText("当前站:" + option.getCurStationName() + "-拥挤度:" + option.getRailStatus());
        if (option.getNextStationID().compareTo(option.getCurStationID())==0){
            tvLeavingtime.setText("已到达终点站" + option.getCurStationID());
        }else {
            tvLeavingtime.setText("距离下站时间:" + option.getNextStationLeavingTime());
        }
        tvDestination.setText("列车方向:" + option.getDestination());

        mPopWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);
    }

    @Override
    public void OnDataChanged(Bundle bd) {

    }


    @Override
    public boolean doBackPress() {
        if (tileMapMode == BigTileMap.TileMapMode.GRID.ordinal()){
            if (mRoutPlanMgr.getRouteSearching()){
                mRoutPlanMgr.resetSearchingStatus();
                mDetailImageView.setVisibility(View.GONE);
                if (mBottomSheetLayout.isSheetShowing())
                    mBottomSheetLayout.dismissSheet();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRouteComputerStart() {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            if (mBottomSheetLayout != null && mBottomSheetLayout.isSheetShowing()) {
                mBottomSheetLayout.dismissSheet();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (tileMapMode != BigTileMap.TileMapMode.GRID.ordinal()) {
            if (mRailOptionMgr != null)
                mRailOptionMgr.startRailDataProcess();
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (tileMapMode != BigTileMap.TileMapMode.GRID.ordinal()) {
            if (mRailOptionMgr != null)
                mRailOptionMgr.startRailDataProcess();
        }
    }

    @Override
    public void onRouteComputeResults(List<StationPassTime> routeList) {
        if (routeList.isEmpty()){
            Snackbar.make(mBigImageView,"当前时间段未搜索到可行路径",Snackbar.LENGTH_SHORT).show();
        }
        else {

            //mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            List<RecyclerRouteView> lsFragment = new ArrayList<>();
            for (StationPassTime stationTime : routeList){
                RecyclerRouteView view = new RecyclerRouteView();
                view.setStationPassTime(stationTime);
                view.setTileMap(mBigImageView);
                lsFragment.add(view);
            }

            mTabLayoutFrame.setFragmentList(lsFragment);
            mBottomSheetLayout.showWithSheetView(mTabLayoutFrame);
            mDetailImageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_search == item.getItemId()){

            Intent intent = new Intent();
            intent.setClass(getContext(), SearchActivity.class);
//            Bundle bd = new Bundle();
//            bd.putString("name",stationItem.getStrStationName());
//            bd.putString("id",stationItem.getStrStationID());
//            bd.putString("desc",stationItem.getStrStationDesc());
//            intent.putExtra("information",bd);
            startActivity(intent);
        }
        return true;
    }

}
