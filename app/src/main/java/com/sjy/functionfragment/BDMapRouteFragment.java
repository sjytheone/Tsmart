package com.sjy.functionfragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.sjy.baseactivity.ShowRailTimeTable;
import com.sjy.baseactivity.ShowStationActivity;
import com.sjy.beans.RailOption;
import com.sjy.beans.RouteItemBean;
import com.sjy.beans.RouteLineItemBean;
import com.sjy.bushelper.MyApp;
import com.sjy.bushelper.R;
import com.sjy.bushelper.RailItemManager;
import com.sjy.listener.IFragemDataListener;
import com.sjy.listener.IRailItemMoveListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/13.
 */
public class BDMapRouteFragment extends Fragment implements IFragemDataListener, IRailItemMoveListener{

    private static final String TAG = "BDMapRouteFragment";
    private MapView bdMapView = null;
    private BaiduMap bdMap = null;
    private CoordinateConverter mConverter = new CoordinateConverter();
    private RailItemManager mRailItemManager = null;
    private PopupWindow mPopWindow = null;
    private TextView mTileMapStart;
    private TextView mTileMapEnd;
    private View mViewStationInfo = null;
    private View mViewRailInfo = null;
    private RelativeLayout mTileStationInfo;
    private List<OverlayOptions> mRouteTextOptions = new ArrayList<>();
    private List<OverlayOptions> mRouteMarkerOptions = new ArrayList<>();
    private List<OverlayOptions> mRouteLineOptions = new ArrayList<>();
    private List<OverlayOptions> mRouteDotOptions = new ArrayList<>();
    private List<OverlayOptions> mListRailOptionMarker = new ArrayList<>();
    private List<OverlayOptions> mRoutePosIndex = new ArrayList<>();
    private Map<Integer,Integer> mMapTextSize = new HashMap<>();
    private Map<Integer,BitmapDescriptor> mapStationMarkerRes =  new HashMap<>();

    private List<LatLng> stationTitles = new ArrayList<>();

    private List<RailOption> mlsRailOptions = new ArrayList<>();

    private int nCurentZoom = 14;
    LatLngBounds mMapbounds = null;

    PolylineOptions mlineTest = new PolylineOptions();
    private List<LatLng> mlineTestDot = new ArrayList<>();

    private List<LatLng> mBaseDot = new ArrayList<>();
    private static BDMapRouteFragment bdFragmentIns = null;

    public static BDMapRouteFragment newInstance(){
        if (bdFragmentIns == null){
            bdFragmentIns = new BDMapRouteFragment();
        }
        return bdFragmentIns;
    }

    private Handler mHandler = new Handler();

    private Runnable mRunableRefreshMarker = new Runnable() {
        @Override
        public void run() {
            UpdataMark();

            //mHandler.postDelayed(mRunableRefreshMarker, 6000);
        }
    };

    private Runnable mRunable = new Runnable() {
        @Override
        public void run() {
            InitBDMapInfo();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMapTextSize.put(14, 16);
        mMapTextSize.put(15, 18);
        mMapTextSize.put(16, 20);
        mMapTextSize.put(17, 30);

        BitmapDescriptor icon = BitmapDescriptorFactory.fromAsset("resourcedata/header_floor_text_20.png");
        mapStationMarkerRes.put(14,icon);
        icon = BitmapDescriptorFactory.fromAsset("resourcedata/header_floor_text_28.png");
        mapStationMarkerRes.put(15,icon);
        icon = BitmapDescriptorFactory.fromAsset("resourcedata/header_floor_text_48.png");
        mapStationMarkerRes.put(16,icon);
        icon = BitmapDescriptorFactory.fromAsset("resourcedata/header_floor_text_56.png");
        mapStationMarkerRes.put(17, icon);

        mConverter.from(CoordinateConverter.CoordType.GPS);
        mMapbounds = new LatLngBounds.Builder().include(new LatLng(41.8355, 123.7582)).include(new LatLng(41.6213, 123.3620)).build();

        stationTitles.add(new LatLng(41.67416201246202,123.37980041855477));
        stationTitles.add(new LatLng(41.63916930080816, 123.49996674662451));
        stationTitles.add(new LatLng(41.67273411351248, 123.37998906271184));
        stationTitles.add(new LatLng(41.81954594319106, 123.67864869561423));

        mPopWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopWindow.setOutsideTouchable(true);

        mRailItemManager = new RailItemManager();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        bdMapView.setVisibility(hidden ? View.GONE : View.VISIBLE);
        if (hidden){
            if (bdMap!= null){
                bdMap.clear();
                mHandler.removeCallbacks(mRunableRefreshMarker);
            }

        }else {
            if (bdMap != null){
                mHandler.postDelayed(mRunableRefreshMarker,300);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.bdmap_route_fragment, null, false);
        bdMapView = (MapView) v.findViewById(R.id.bdmap_route_container);
        ImageView imgv = (ImageView) v.findViewById(R.id.bdmap_maplayer);
        if (imgv != null){
            imgv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bdMap.getMapType() == BaiduMap.MAP_TYPE_NONE){
                        bdMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    }else {
                        bdMap.setMapType(BaiduMap.MAP_TYPE_NONE);
                    }
                }
            });
        }

        imgv = (ImageView) v.findViewById(R.id.bdmap_redo);
        imgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlineTestDot.remove(mlineTestDot.size() - 1);
            }
        });
        imgv.setVisibility(View.GONE);

        mHandler.postDelayed(mRunable, 1000);
        return v;
    }

    public void InitStationData(){
        List<RouteItemBean> allStations = MyApp.theIns().getJsonStations();
        for (RouteItemBean station : allStations){
            LatLng latlng = gpsLatLngConvert(new LatLng(station.getLat(), station.getLon()));
            LatLng latText = gpsLatLngConvert(new LatLng(station.getLat() - 0.0008, station.getLon()));
            //BitmapDescriptor bitIcon = BitmapDescriptorFactory.fromAsset("resourcedata/marker_default.png");
            //LatLng latlng = new LatLng(station.getLat(), station.getLon());
            //LatLng latText = new LatLng(station.getLat() - 0.0008, station.getLon());
            BitmapDescriptor bitIcon = BitmapDescriptorFactory.fromAsset("resourcedata/header_floor_text_20.png");
            //BitmapFactory.
            //BitmapDescriptorFactory.fromBitmap()
            //BitmapDrawable bitmapDrawable = new BitmapDrawable();
            //bitmapDrawable.draw();
            //BitmapDescriptor bitIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_default);
            Bundle bdInof = new Bundle();
            bdInof.putSerializable("station", station);
            MarkerOptions marker = new MarkerOptions().position(latlng).icon(bitIcon).title(station.getStrStationName()).zIndex(10).perspective(true).anchor(0,-21).alpha(70);
            marker.extraInfo(bdInof);
            mRouteMarkerOptions.add(marker);
            TextOptions textOpt = new TextOptions().position(latText).text(station.getStrStationName()).fontColor(ContextCompat.getColor(getContext(),R.color.map_text_color)).fontSize(20).align(TextOptions.ALIGN_BOTTOM,TextOptions.ALIGN_BOTTOM);
            mRouteTextOptions.add(textOpt);
        }
    }


    public void InitRouteLines(){

        List<RouteLineItemBean> allrouteLine = MyApp.theIns().getAllRouteLine();
        for (RouteLineItemBean routeItem : allrouteLine){
            if (routeItem.getRouteID().compareTo("route5") == 0){
                //continue;
            }

            PolylineOptions line = new PolylineOptions();
            line.color(routeItem.getColor());
            line.width(8);
            line.keepScale(true);
            List<LatLng> lats = new ArrayList<>();

                List<LatLng> ls = routeItem.getRouteLatLng();
                int x = 1;
                for (LatLng latlng : ls){
                    lats.add(latlng);
                    CircleOptions circ = new CircleOptions().center(latlng).fillColor(routeItem.getColor()).radius(25);
                    mRouteDotOptions.add(circ);

//                    BitmapDescriptor bitIcon = BitmapDescriptorFactory.fromAsset("resourcedata/tranist_detail_map_travel_busstation_ic.png");
//                    MarkerOptions mkcirc = new MarkerOptions().flat(true).icon(bitIcon).position(latlng).perspective(true);
//                    mRouteDotOptions.add(mkcirc);
                    TextOptions tips = new TextOptions().position(latlng).text(""+x).fontColor(Color.RED).fontSize(30);
                    mRoutePosIndex.add(tips);
                    x++;
                }
            line.points(lats);
            mRouteLineOptions.add(line);
        }

        mlineTest.color(Color.parseColor("#C637AF"));
        mlineTest.width(8);
        mlineTest.keepScale(true);
    }

    public LatLng gpsLatLngConvert(LatLng latLng){
        mConverter.coord(latLng);
        return mConverter.convert();
    }

    @Override
    public void OnDataChanged(Bundle bd) {

    }

    @Override
    public boolean doBackPress() {
        if (mPopWindow != null && mPopWindow.isShowing()){
            mPopWindow.dismiss();
            return true;
        }
        return false;
    }


    public View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tile_map_popview_end:
                    break;
                case R.id.tile_map_popview_start:
                    mPopWindow.dismiss();
                    Marker marker = (Marker) v.getTag();
                    marker.setIcon(BitmapDescriptorFactory.fromAsset("resourcedata/RED.png"));
                    RouteItemBean ib = (RouteItemBean) marker.getExtraInfo().get("station");
                    Toast.makeText(getContext(),ib.getStrStationName(),Toast.LENGTH_SHORT).show();
                    break;
                case R.id.main_map_popview_station_info: {
                    mPopWindow.dismiss();
                    RouteItemBean it = (RouteItemBean) v.getTag();
                    Intent intent = new Intent();
                    //intent.setAction("com.sjy.baseactivity.ShowStationActivity");
                    intent.setClass(getActivity().getApplicationContext(), ShowStationActivity.class);
                    Bundle bd = new Bundle();
                    bd.putString("name", it.getStrStationName());
                    bd.putString("id", it.getStrStationID());
                    bd.putString("desc", it.getStationFragmentDes());
                    intent.putExtra("information", bd);
                    startActivity(intent);
                }
                    break;
                case R.id.layout_tilemap_railinfo: {
                    mPopWindow.dismiss();
                    RailOption option = (RailOption) v.getTag();
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
                default:
                    break;
            }
        }
    };


    public void showPopView(Marker marker){

        RouteItemBean ib = (RouteItemBean) marker.getExtraInfo().get("station");
        RailOption option = (RailOption) marker.getExtraInfo().get("railinfo");
        if (ib != null){
            showStationInfo(marker, ib);
        }else if (option != null){
            showRailItemInfo(option);
        }
    }

    public void hidePopView(){
        if (mPopWindow != null){
            mPopWindow.dismiss();
        }
    }

    private  View.OnTouchListener myTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setBackgroundColor(getResources().getColor(R.color.theme_blue_pressed));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.setBackgroundColor(Color.TRANSPARENT);
            }
            return false;
        }
    };

    public void showStationInfo( Marker marker, RouteItemBean ib ){
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
        }
        mPopWindow.setContentView(mViewStationInfo);

        TextView tv = (TextView) mViewStationInfo.findViewById(R.id.tile_map_popview_station_name);
        tv.setText(ib.getStrStationName());
        mTileStationInfo.setTag(ib);
        mTileMapStart.setTag(marker);
        mTileMapEnd.setTag(marker);
        mPopWindow.showAtLocation(bdMapView, Gravity.CENTER, 0, 0);
    }

    public void showRailItemInfo( RailOption option ){
        if (mViewRailInfo == null){
            mViewRailInfo = getActivity().getLayoutInflater().inflate(R.layout.layout_tile_map_railinfo, null);
            RelativeLayout layout = (RelativeLayout) mViewRailInfo.findViewById(R.id.layout_tilemap_railinfo);
            layout.setOnTouchListener(myTouchListener);
            layout.setOnClickListener(mClickListener);
            //mViewRailInfo.setAlpha(0.5f);
            //mViewRailInfo.setBackgroundColor(Color.parseColor("#e0000000"));
            
        }
        mPopWindow.setContentView(mViewRailInfo);
        TextView tvRailID = (TextView) mViewRailInfo.findViewById(R.id.layout_tilemap_railid);
        TextView tvStart = (TextView) mViewRailInfo.findViewById(R.id.layout_tilemap_railinfo_starttime);
        TextView tvEnd = (TextView) mViewRailInfo.findViewById(R.id.layout_tilemap_railinfo_endtime);
        RelativeLayout layout = (RelativeLayout) mViewRailInfo.findViewById(R.id.layout_tilemap_railinfo);
        layout.setTag(option);
        tvStart.setText(option.getStrStartTime());
        tvEnd.setText(option.getStrEndTime());
        tvRailID.setText(option.getStrRailID());
        mPopWindow.showAtLocation(bdMapView, Gravity.CENTER, 0, 0);
    }


    @Override
    public void onResume() {
        //mHandler.removeCallbacks(mRunable);
        super.onResume();
        bdMapView.onResume();
    }
//
@Override
    public void onPause() {
        mHandler.removeCallbacks(mRunable);
    super.onPause();
    bdMapView.onPause();
    }
//
    @Override
    public void onDestroyView() {
        mHandler.removeCallbacks(mRunable);
        super.onDestroyView();
        bdMapView.onDestroy();
    }

    public void InitBDMapInfo(){

        bdMap = bdMapView.getMap();
        InitStationData();
        InitRouteLines();
        //InitRailOptions();

//        mRailItemManager.setRailItemMoveListener(this);
//        mRailItemManager.setRailInfo(mlsRailOptions);
//        mRailItemManager.restoreRailItemStep();
        //mRailItemManager.setRandomRailItemStep();

            try {
                if (bdMap == null)
                    throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
            }

        bdMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                bdMap.setMapStatusLimits(mMapbounds);
                bdMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(41.6815235, 123.4614121)));
                bdMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(14));
                RefreshBDMapOverLayer(14);
            }
        });
        bdMap.setMaxAndMinZoomLevel(17, 14);
        bdMap.setBuildingsEnabled(false);

        bdMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        bdMap.showMapPoi(false);


        bdMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //String("BDMapLatLng:","\"lat\":"+"\"" +latLng.latitude+"\""+" \"lon\":" +"\"" +latLng.longitude+"\"");
                //startDrawLine2(latLng);

            }
        });


        bdMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                showPopView(marker);
                return false;
            }
        });

        bdMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //bdMap.hideInfoWindow();
                hidePopView();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });


        bdMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                //if (mapStatus.zoom)
                int nZoom = (int) mapStatus.zoom;
                if (nZoom == nCurentZoom)
                    return;

                if (mMapTextSize.containsKey(nZoom)) {
                    int textsize = mMapTextSize.get(nZoom);
                    for (OverlayOptions topt : mRouteTextOptions) {
                        TextOptions t = (TextOptions) topt;
                        t.fontSize(textsize);
                    }
                    BitmapDescriptor icon = mapStationMarkerRes.get(nZoom);
                    for (OverlayOptions marker : mRouteMarkerOptions){
                        MarkerOptions markerOption = (MarkerOptions) marker;
                        markerOption.icon(icon);
                    }
                    RefreshBDMapOverLayer(nZoom);
                }
                nCurentZoom = nZoom;
            }
        });

        //RefreshBDMapOverLayer();
    }

    private synchronized void RefreshBDMapOverLayer(int nZoomLevel){

        bdMap.clear();
        //bdMap.addOverlays(mRouteDotOptions);
        if (nZoomLevel > 14){
            bdMap.addOverlays(mRouteTextOptions);
            bdMap.addOverlays(mRouteMarkerOptions);

            if (!mListRailOptionMarker.isEmpty())
                bdMap.addOverlays(mListRailOptionMarker);
        }

        bdMap.addOverlays(mRouteLineOptions);

        //bdMap.addOverlays(mRoutePosIndex);


        if (mlineTestDot.size() >= 2)
            bdMap.addOverlay(mlineTest);

    }

    public synchronized void UpdataMark(){
        if (mlsRailOptions.isEmpty())
            return;

        RefreshBDMapOverLayer(nCurentZoom);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bdMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onRailItemMoved() {

        mHandler.postDelayed(mRunableRefreshMarker, 1000);

    }

    protected void startDrawLine2(LatLng latlng){

        mlineTestDot.add(latlng);
        if (mlineTestDot.size() <= 2)
            return;
        mlineTest.points(mlineTestDot);
        RefreshBDMapOverLayer(19);
    }

}
