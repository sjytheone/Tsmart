package com.sjy.beans;

import android.renderscript.Sampler;

import com.sjy.bushelper.MyApp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/16.
 */
public class RailWayLineItem {
    private String mRailWayLineID;

    private List<RailWayTimeTable> mRailWayTimeTabels = new ArrayList<>();

    private Map<String,String> mapTransition = new LinkedHashMap<>();

    public String getFirstRailWayTime() {
        return firstRailWayTime;
    }

    public void setFirstRailWayTime(String firstRailWayTime) {
        this.firstRailWayTime = firstRailWayTime;
    }

    private String firstRailWayTime;

    public String getLastRailWayTime() {
        return lastRailWayTime;
    }

    public void setLastRailWayTime(String lastRailWayTime) {
        this.lastRailWayTime = lastRailWayTime;
    }

    private String lastRailWayTime;
    public String getRailWayLineName() {
        return mRailWayLineName;
    }

    private String mRailWayLineName;


    private List<String> mlsStations = new ArrayList<>();

    public void setRailWayLineID(String id){
        mRailWayLineID = id;
    }

    public String getRailWayLineID(){
        return mRailWayLineID;
    }

    public void addStation(String stationID){
        mlsStations.add(stationID);
    }

    public void addTransition(String stationID,String routeID ){
        mapTransition.put(stationID, routeID);
    }

    public List<String> getRailWayStations(){
        return mlsStations;
    }

    public void setRailWayLineName(String stationName){
        mRailWayLineName = stationName;
    }

    public String getFirstStation(){
        String stationID = mlsStations.get(0);
        RouteItemBean item = MyApp.theIns().getRailWayStation(stationID);
        if (item != null)
            return item.getStrStationName();
        return null;
    }

    public String getLastStation(){
        String stationID = mlsStations.get(mlsStations.size() - 1);
        RouteItemBean item = MyApp.theIns().getRailWayStation(stationID);
        if (item != null)
            return item.getStrStationName();
        return null;
    }

    public List<String> getStationSections(String s,String t){
        int sindex = mlsStations.indexOf(s);
        int tindex = mlsStations.indexOf(t);
        List<String> ls = new ArrayList<>();
        if (sindex > tindex){
            for (int i = sindex; i >= tindex; i--){
                ls.add(mlsStations.get(i));
            }
        }else {
            for (int i = sindex; i<= tindex; i++){
                ls.add(mlsStations.get(i));
            }
        }
        return ls;
    }

    public boolean hasStation(String stationID){
        return mlsStations.contains(stationID);
    }

    public void AddRailWayTimeTable(RailWayTimeTable timeTable){
        mRailWayTimeTabels.add(timeTable);
    }

    public List<RailWayTimeTable> getmRailWayTimeTabels(){
        return mRailWayTimeTabels;
    }

    public RailWayTimeTable getNearestTimeTable(String startStation,String endStation){
        long minIntervals = Long.MAX_VALUE;
        int index = 0,curpos = -1;
        for (RailWayTimeTable timeTable : mRailWayTimeTabels){
            if (timeTable.hasPassStation(endStation) && timeTable.railWayDerection(startStation,endStation) == 1){
                long interval = timeTable.getStationCurTimeIntervals(startStation);
                if (interval < minIntervals){
                    minIntervals = interval;
                    curpos = index;
                }
            }
            ++index;
        }
        if (curpos >=0)
            return mRailWayTimeTabels.get(curpos);
        else
            return null;
    }
}
