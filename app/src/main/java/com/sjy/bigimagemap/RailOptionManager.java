package com.sjy.bigimagemap;

import android.os.Handler;

import com.sjy.beans.RailWayTimeTable;
import com.sjy.bushelper.MyApp;
import com.sjy.listener.IRailItemMoveListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sjy on 2016/7/9 0009.
 */
public class RailOptionManager {

    private IRailItemMoveListener moveListener;
    private List<TileMapRailOption> mlsRailOptions = new ArrayList<>();
    private BigTileMap  mTileMap;

    public RailOptionManager() {
        mHandler.postDelayed(mRunableRefreshMarker,8000);
    }

    public void setTileMap(BigTileMap tileMap){
        mTileMap = tileMap;
    }

    public void setRailItemMoveListener(IRailItemMoveListener listener){
        moveListener = listener;
    }


    public void startRailDataProcess(){
        InitRailOptionData();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                InitRailOptionData();
            }

        }, 10 * 1000);

    }

    public void InitRailOptionData(){
        mlsRailOptions.clear();
        mTileMap.removeAllRailOption();
        List<RailWayTimeTable> timeTables = MyApp.theIns().getRailWayTimeTables();
        for (RailWayTimeTable table : timeTables){
            if (table.isInTimeSection()){

                TileMapRailOption option = new TileMapRailOption(table.getPassingStations());
                option.setRailWayTimeTable(table);
                option.setRouteID(table.getBelongRouteID());
                option.setStrRailID(table.getmRailWayTrainID());
                option.setStrStartTime(table.getStrStartTime());
                option.setStrEndTime(table.getStrEndTime());
                BigMapDrawOverlay overlay = new BigMapDrawOverlay();
                String drawable = MyApp.theIns().getRailDrawable(table.getBelongRouteID());
                overlay.setDrawable(MyApp.theIns().getBitmapFromAssets("bigmaps/" + drawable));
                option.setOverlayOption(overlay);
                overlay.PutExtraInfo("railinfo", option);
                mlsRailOptions.add(option);
                option.setRailOptionCurPos();
                option.randomStatus();
                mTileMap.addRailOption(overlay);


            }
            //在当前时间内的车次
        }

    }

    public void setRailOptions(List<TileMapRailOption> lsRailInfo){
        mlsRailOptions = lsRailInfo;
    }

    private Handler mHandler = new Handler();

    private Runnable mRunableRefreshMarker = new Runnable() {
        @Override
        public void run() {

            boolean bNeedUpdata = processRailItemPosition();
            if(bNeedUpdata && moveListener != null){
                moveListener.onRailItemMoved();
            }

            mHandler.postDelayed(mRunableRefreshMarker, 5* 1000);
        }
    };


    protected boolean processRailItemPosition(){
        for (TileMapRailOption option : mlsRailOptions){
            option.setRailOptionCurPos();
            option.randomStatus();
        }
        return true;
    }

    public void restoreRailItemStep(){
        for (TileMapRailOption option : mlsRailOptions){
            option.restoreStep();
        }
    }

    public void setRandomRailItemStep(){
        for (TileMapRailOption option : mlsRailOptions){
            option.setCanMove(true);
            option.initRandomPos();
        }
    }

    public Map<String,TileMapRailOption> getStationNearTimeTable(String stationID){
        Map<String,TileMapRailOption> fliter = new LinkedHashMap<>();

        for(TileMapRailOption ioption : mlsRailOptions){
            //
            if (ioption.hasStation(stationID)){
                if (fliter.containsKey(ioption.getRouteID())){
                    TileMapRailOption value = fliter.get(ioption.getRouteID());
                    if (ioption.getLeavingTimeFromStationLong(stationID) < value.getLeavingTimeFromStationLong(stationID)){
                        fliter.put(ioption.getRouteID(),ioption);
                    }
                }else {
                    long leavingTime = ioption.getLeavingTimeFromStationLong(stationID);
                    if (leavingTime != Long.MAX_VALUE)
                        fliter.put(ioption.getRouteID(), ioption);
                }
            }
        }
        return  fliter;
    }
}
