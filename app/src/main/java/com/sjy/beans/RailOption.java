package com.sjy.beans;

import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/5/15.
 */
public class RailOption  implements Serializable {

    public String getStrRailID() {
        return strRailID;
    }

    public void setStrRailID(String strRailID) {
        this.strRailID = strRailID;
    }

    private String strRailID;          //车次ID

    public String getRouteID() {
        return routeID;
    }

    public void setRouteID(String routeID) {
        this.routeID = routeID;
    }

    private String routeID;            //路径ID
    private List<LatLng> lsRouteLine; //途径线路
    private int ncurStep = 0;                 //当前步

    public String getStrStartTime() {
        return strStartTime;
    }

    public void setStrStartTime(String strStartTime) {
        this.strStartTime = strStartTime;
    }

    private String strStartTime;

    public String getStrEndTime() {
        return strEndTime;
    }

    public void setStrEndTime(String strEndTime) {
        this.strEndTime = strEndTime;
    }

    private String strEndTime;

    public RailOption(List<LatLng> ls){
        lsRouteLine = ls;
    }

    public void setCanMove(boolean bMove){ mbCanMove = bMove;}

    private boolean mbCanMove = false;
    //前进
    public LatLng stepForward(){
        ncurStep++;
        if (ncurStep >= lsRouteLine.size()){
            ncurStep = 0;
        }
        LatLng ltlng = lsRouteLine.get(ncurStep);
        if (mMarkerOption != null){
            mMarkerOption.position(ltlng);
        }
        return ltlng;
    }

    public void restoreStep(){
        ncurStep = 0;
        LatLng ltlng = lsRouteLine.get(ncurStep);
        if (mMarkerOption != null){
            mMarkerOption.position(ltlng);
        }
    }

    public boolean canMove(){
        return mbCanMove;
    }

    public void setMarkerOption(MarkerOptions marker){
        mMarkerOption = marker;
    }

    private MarkerOptions mMarkerOption;

    public void initRandomPos(){
        Random ran =new Random(System.currentTimeMillis());
        ncurStep = ran.nextInt(lsRouteLine.size());
        LatLng ltlng = lsRouteLine.get(ncurStep);
        if (mMarkerOption != null){
            mMarkerOption.position(ltlng);
        }
    }
}
