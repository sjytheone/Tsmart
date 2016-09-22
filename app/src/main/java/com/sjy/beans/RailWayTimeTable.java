package com.sjy.beans;

import com.sjy.bushelper.MyApp;
import com.sjy.net.StaPassTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 * Created by Administrator on 2016/7/16.
 */
public class RailWayTimeTable {

    private String mRailWayTrainID;
    private Map<String,TimeItemBean> mMapStationVTime= new LinkedHashMap<>();
    private MyApp myApp = MyApp.theIns();
    //pace
    public  List<StaPassTime> StaTT = new ArrayList<>();

    public String getStrStartTime() {
        return strStartTime;
    }

    private String strStartTime;
    private String startStationID;
    private String strEndStationID;


    public String getStrEndTime() {
        return strEndTime;
    }


    private String strEndTime;
    private String firstStation;
    private int railwayDirection = 1;
    public void setStrRailWayTrainID(String trainID){
        mRailWayTrainID = trainID;
    }

    public String getmRailWayTrainID(){
        return mRailWayTrainID;
    }

    public Map<String,TimeItemBean> getStationVtimeMap(){
        return mMapStationVTime;
    }

    public void AddNode(String stationID,String arrTime){
        TimeItemBean ib = new TimeItemBean(arrTime);
//        if (mMapStationVTime.size() == 0){
//            firstStation = stationID;
//        }
        mMapStationVTime.put(stationID,ib);

        //pace
        StaPassTime staTemp = new StaPassTime();
        staTemp.StaCode = stationID;
        staTemp.StaTime = mMapStationVTime.get(stationID).getMillisTime();
        StaTT.add(staTemp);
    }

    //pace
    public int StaIndex(String staCode)
    {
        int temp = -1;
        for (int i = 0; i < StaTT.size(); i++)
        {
            if (StaTT.get(i).StaCode.equals(staCode))
            { temp = i; break; }
        }
        return temp;
    }

    public TimeItemBean getArrTime(String stationID){
        TimeItemBean ib = mMapStationVTime.get(stationID);
        return ib;
    }

    public int StationIndex(String stationID){
        int temp = -1;
        Set<String> keySet = mMapStationVTime.keySet();
        int nCount = 0;
        for (String str : keySet){
            if (str.compareTo(stationID) == 0){
                temp = nCount; break;
            }
            nCount++;

        }
        return temp;
    }


    public int getTimeStationStep(long MillisTime){
        int minIndex = 0; long resMin = Long.MAX_VALUE;
        int count = 0;
        Set<Map.Entry<String,TimeItemBean>> entrySet = mMapStationVTime.entrySet();
        for (Map.Entry<String,TimeItemBean> entry : entrySet){
            long res = entry.getValue().getMillisTime() - MillisTime;
            if (res < resMin && res >= 0){
                resMin = res;
                minIndex = count;
            }
            count++;
        }
        return minIndex;

    }

    public String getBelongRouteID(){
        String routeID = "";
        char first = mRailWayTrainID.charAt(0);
        if (first == 'K')
            first = mRailWayTrainID.charAt(1);
        switch (first){
            case '1' : routeID = "route1"; break;
            case '2' : routeID = "route2"; break;
            case '3' : routeID = "route3"; break;
            case '5' : routeID = "route5"; break;
            default: break;
        }
        return routeID;
    }


    public String getTimeDesc() {
        Collection<TimeItemBean> s = mMapStationVTime.values();
        List<TimeItemBean> ls = new ArrayList<>(s);
        TimeItemBean first = ls.get(0);
        TimeItemBean last = ls.get(ls.size() - 1);

        long abs = first.getMillisTime() - last.getMillisTime();
        String desc = "始发:" + first.getstrTime() + "-终到" + last.getstrTime();
        if (abs >= 0){
            desc = "始发:" + last.getstrTime() + "-终到" + first.getstrTime();
        }

        return desc;
    }

    public void ProcessDatas(){
        List arrayList = new ArrayList(mMapStationVTime.entrySet());
        Collections.sort(arrayList, new Comparator() {
            public int compare(Object o1, Object o2) {
                Map.Entry obj1 = (Map.Entry) o1;
                Map.Entry obj2 = (Map.Entry) o2;

                TimeItemBean ib1 = (TimeItemBean) obj1.getValue();
                TimeItemBean ib2 = (TimeItemBean) obj2.getValue();
                Long l1 = (Long)ib1.getMillisTime();
                Long l2 = (Long)ib2.getMillisTime();

                return l1.compareTo(l2);
            }
        });

        mMapStationVTime.clear();
        for (int i = 0 ; i < arrayList.size() ; ++i){
            Map.Entry<String,TimeItemBean >  entry = (Map.Entry<String, TimeItemBean>) arrayList.get(i);
            mMapStationVTime.put(entry.getKey(),entry.getValue());
            if (i == 0){
               this.startStationID = entry.getKey();
               this.strStartTime = entry.getValue().getstrTime();
            }else if (i == arrayList.size() - 1){
                this.strEndStationID = entry.getKey();
                this.strEndTime = entry.getValue().getstrTime();
            }
        }
//        if(strEndStationID.compareTo(firstStation) !=0){
//            railwayDirection = 2;
//        }
    }

    public boolean isInTimeSection(){
        long curTime = System.currentTimeMillis();
        Collection<TimeItemBean> s = mMapStationVTime.values();
        List<TimeItemBean> ls = new ArrayList<>(s);
        TimeItemBean first = ls.get(0);
        TimeItemBean last = ls.get(ls.size() - 1);
        if (curTime >= first.getMillisTime() && curTime <= last.getMillisTime())
            return true;

        return false;
    }

    public List<String> getPassingStations(){
        List<String> bigInfoList = new ArrayList<>();
        Set<String> keySet = mMapStationVTime.keySet();
        Iterator it = keySet.iterator();
        bigInfoList.addAll(keySet);
        return bigInfoList;
    }

    public long getStationCurTimeIntervals(String stationID){
        TimeItemBean ib = getArrTime(stationID);
        if (ib != null){
            long mililistime = ib.getMillisTime();
            long lIntervals = mililistime - System.currentTimeMillis();
            if (mililistime - System.currentTimeMillis() >= 0)
            {
                return lIntervals;
            }
        }
        return Long.MAX_VALUE;
    }


    public boolean hasPassStation(String stationID){
        TimeItemBean ib = getArrTime(stationID);
        return ib == null ? false : true;
    }

    public int railWayDerection(String sid,String eid){
        if (StaIndex(sid) > StaIndex(eid))
            return 2;
        return 1;
    }

    public int getRailwayDirection(){
        int x = 2;
        int s = Integer.parseInt(startStationID);
        int e = Integer.parseInt(strEndStationID);
        if (s >= e){
            x = 1;
        }
        return x;
        //return railWayDerection(startStationID,strEndStationID);
    }
    //获取列车终点站名称
    public String getDestinationID(){
        return strEndStationID;
    }
}
