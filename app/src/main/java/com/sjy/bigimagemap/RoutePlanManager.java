package com.sjy.bigimagemap;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.sjy.beans.BigMapstationInfo;
import com.sjy.beans.RailWayLineItem;
import com.sjy.beans.RailWayTimeTable;
import com.sjy.beans.RouteItemBean;
import com.sjy.beans.RoutePlanDetailItem;
import com.sjy.beans.TimeItemBean;
import com.sjy.bushelper.MyApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/1.
 */
public class RoutePlanManager {

    public interface IRoutePlanListener{
        void onRouteComputerStart();
        void onRouteComputeResult(List<RoutePlanDetailItem> result);
        void onRouteComputeDetailResult(String strDetail);
    }


    private BigTileMap  mTileMap;
    private BigMapFlagDrawOverlay mOriginationOverlay = null;
    private BigMapFlagDrawOverlay mTerminusOverlay = null;
    private boolean isRouteSearching = false;
    private IRoutePlanListener mRoutePlanListener = null;
    public boolean getRouteSearching(){
        return isRouteSearching;
    }
    private MyApp myApp;
    public void resetSearchingStatus(){
        isRouteSearching = false;
        mOriginationOverlay = null;
        mTerminusOverlay = null;
        mTileMap.RemoveAllNaviOptions();
        mTileMap.setHighlightOverlay(false);
        mTileMap.invalidate();
    }

    public RoutePlanManager(BigTileMap tileMap){
        myApp = MyApp.theIns();
        mTileMap = tileMap;
    }

    public void setRoutePlanListener(IRoutePlanListener listener){
        mRoutePlanListener = listener;
    }

    public void setOriginationStation(String stationID){
        BigMapstationInfo info = MyApp.theIns().findBigmapStation(stationID);
        if (info != null){
            if (isRouteSearching)
                resetSearchingStatus();

            mTileMap.setTileMode(BigTileMap.TileMapMode.GRID);
            if (mOriginationOverlay == null){
                mOriginationOverlay = new BigMapFlagDrawOverlay();
                mOriginationOverlay.setDrawable(MyApp.theIns().getBitmapFromAssets("bigmaps/transit_start_ic.png"));
                mOriginationOverlay.setPoint(new Point((int) info.getDotX(), (int) info.getDotY()));
                mOriginationOverlay.setFlag(BigMapFlagDrawOverlay.ORIGINATION);
                mTileMap.AddNaviOption(mOriginationOverlay);
            }else {
                mOriginationOverlay.setPoint(new Point((int) info.getDotX(), (int) info.getDotY()));
            }
            mOriginationOverlay.setStationID(info.getStationID());
            mTileMap.invalidate();

            if (mTerminusOverlay != null){
                String terminusID = mTerminusOverlay.getStationID();
                RouteSearch(stationID,terminusID,System.currentTimeMillis());
            }
        }
    }

    public void setTerminusStation(String stationID){
        BigMapstationInfo info = MyApp.theIns().findBigmapStation(stationID);
        if (info != null){
            if (isRouteSearching)
                resetSearchingStatus();

            mTileMap.setTileMode(BigTileMap.TileMapMode.GRID);
            if (mTerminusOverlay == null){
                mTerminusOverlay = new BigMapFlagDrawOverlay();
                mTerminusOverlay.setDrawable(MyApp.theIns().getBitmapFromAssets("bigmaps/transit_end_ic.png"));
                mTerminusOverlay.setPoint(new Point((int) info.getDotX(), (int) info.getDotY()));
                mTerminusOverlay.setFlag(BigMapFlagDrawOverlay.TEMINATION);
                mTileMap.AddNaviOption(mTerminusOverlay);
            }else {
                mTerminusOverlay.setPoint(new Point((int) info.getDotX(), (int) info.getDotY()));
            }
            mTerminusOverlay.setStationID(info.getStationID());
            mTileMap.invalidate();

            if (mOriginationOverlay != null){
                String originationID = mOriginationOverlay.getStationID();
                RouteSearch(originationID, stationID,System.currentTimeMillis());
            }
        }
    }



    public void RouteSearch(String originationID,String terminusID,long lcurTime){
        isRouteSearching = true;
        if (mRoutePlanListener != null){
            mRoutePlanListener.onRouteComputerStart();
        }
        String strRoutePlanDetail = "";
        mTileMap.setHighlightOverlay(true);
        List<RoutePlanDetailItem> resultStations = new ArrayList<>();
        RailWayLineItem shortLine = myApp.findShortLine(originationID, terminusID);
        if (shortLine != null){

            RailWayTimeTable railWayTimeTable = shortLine.getNearestTimeTable(originationID,terminusID);
            if (railWayTimeTable != null){
                String raiWayID = railWayTimeTable.getmRailWayTrainID();
                long atEndTime = railWayTimeTable.getArrTime(terminusID).getMillisTime();
                long spendTime = atEndTime - System.currentTimeMillis();
                long minutes = (spendTime % (1000 * 60 * 60)) / (1000 * 60);
                long seconds = (spendTime % (1000 * 60)) / 1000;
                //String dateStr = hours+":"+minutes+":"+seconds;
                String strSpendTime = String.format("%02d'%02d''",minutes,seconds);
                strRoutePlanDetail += "乘坐车次:" + raiWayID + " 到站时间:" + railWayTimeTable.getArrTime(originationID).getstrTime() + "\r\n";
                strRoutePlanDetail += "总耗时:" + strSpendTime;
            }

            //railWayTimeTable.ge
            List<String> sections = shortLine.getStationSections(originationID,terminusID);
            for (String sid : sections){
                BigMapstationInfo binfo = MyApp.theIns().findBigmapStation(sid);
                BigMapFlagDrawOverlay overlay = new BigMapFlagDrawOverlay();
                RoutePlanDetailItem detailItem = new RoutePlanDetailItem();
                detailItem.setStrStationID(binfo.getStationID());
                detailItem.setStrStationName(binfo.getStationName());
                if (railWayTimeTable != null){
                    TimeItemBean tmbean = railWayTimeTable.getArrTime(sid);
                    if (tmbean != null)
                        detailItem.setStrArrayTime(tmbean.getstrTime());
                }

                //detailItem.setStrArrayTime(railWayTimeTable.get);
                Bitmap bmap = MyApp.theIns().getBitmapFromAssets("bigmaps/header_floor_text_56.png");
                if (sid.compareTo(originationID) == 0){
                    bmap = MyApp.theIns().getBitmapFromAssets("bigmaps/transit_start_ic.png");
                }else if (sid.compareTo(terminusID) ==0){
                    bmap = MyApp.theIns().getBitmapFromAssets("bigmaps/transit_end_ic.png");
                }
                overlay.setDrawable(bmap);
                overlay.setPoint(new Point((int) binfo.getDotX(), (int) binfo.getDotY()));
                mTileMap.AddNaviOption(overlay);
                resultStations.add(detailItem);
                mTileMap.invalidate();
            }
        }
        if (mRoutePlanListener != null){
            mRoutePlanListener.onRouteComputeDetailResult(strRoutePlanDetail);
            mRoutePlanListener.onRouteComputeResult(resultStations);
        }
    }
}
