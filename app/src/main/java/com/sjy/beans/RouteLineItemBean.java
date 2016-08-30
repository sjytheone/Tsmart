package com.sjy.beans;

import android.graphics.Color;
import android.graphics.Point;

import com.baidu.mapapi.model.LatLng;
import com.sjy.adapter.RouteLineRecyclerAdapter;
import com.sjy.bushelper.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/7.
 */
public class RouteLineItemBean {
    public String getRouteID() {
        return strRouteID;
    }
    public void setStrRouteID(String StationID) {
        this.strRouteID = StationID;
    }
    public String getStrRouteName() {
        return strRouteName;
    }
    public void setStrRouteName(String strRouteName) {
        this.strRouteName = strRouteName;
    }
    public String getStrRouteDesc() {
        return strRouteDesc;
    }
    public void setStrRouteDesc(String strRouteDesc) {
        this.strRouteDesc = strRouteDesc;
    }
    private String strRouteID;
    private String strRouteName;
    private String strRouteDesc;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    private int color = R.color.theme_blue;

    public String getStrTime() {
        return strTime;
    }

    public void setStrTime(String strTime) {
        this.strTime = strTime;
    }

    private String strTime;

    private List<LatLng> mlslatlng = new ArrayList<>();
    private List<Point>  mlsPoints = new ArrayList<>();

    public void AddRouteLatLng(double lat,double lon){
        mlslatlng.add(new LatLng(lat,lon));
    }

    public void AddRoutePoint(int x, int y){
        mlsPoints.add(new Point(x,y));
    }

    public List<LatLng> getRouteLatLng(){
        return mlslatlng;
    }

    public List<Point> getRoutePoints(){
        return mlsPoints;
    }

    public String getStrStartTime() {
        return strStartTime;
    }

    public void setStrStartTime(String strStartTime) {
        this.strStartTime = strStartTime;
    }

    String strStartTime;

    public String getStrEndTime() {
        return strEndTime;
    }

    public void setStrEndTime(String strEndTime) {
        this.strEndTime = strEndTime;
    }

    String strEndTime;

    public String getStrStartStation() {
        return strStartStation;
    }

    public void setStrStartStation(String strStartStation) {
        this.strStartStation = strStartStation;
    }

    String strStartStation;

    public String getStrEndStation() {
        return strEndStation;
    }

    public void setStrEndStation(String strEndStation) {
        this.strEndStation = strEndStation;
    }

    String strEndStation;

}
