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
import com.sjy.net.StaPassTime;
import com.sjy.net.StationPassTime;

import java.security.PublicKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2016/8/1.
 */
public class RoutePlanManager {

    public interface IRoutePlanListener{
        void onRouteComputerStart();
        void onRouteComputeResults(List<StationPassTime> routeList);
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
            mOriginationOverlay.setStationID(stationID);
            mOriginationOverlay.setInfo(info);
            mTileMap.invalidate();

            if (mTerminusOverlay != null){
                BigMapstationInfo terminus = mTerminusOverlay.getInfo();
                AllRouteSearch(info,terminus);
                //RouteSearch(stationID,terminusID,System.currentTimeMillis());
                //ReturnRoutes(stationID, terminusID, System.currentTimeMillis());
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
            mTerminusOverlay.setStationID(stationID);
            mTerminusOverlay.setInfo(info);
            mTileMap.invalidate();

            if (mOriginationOverlay != null){
                BigMapstationInfo origination = mOriginationOverlay.getInfo();
                //RouteSearch(originationID, stationID,System.currentTimeMillis());

                AllRouteSearch(origination,info);
                //ReturnRoutes(originationID, stationID, System.currentTimeMillis());
                //ReturnRoutes(MyApp.StationsPic.get(originationID), MyApp.StationsPic.get(stationID), System.currentTimeMillis());
            }
        }
    }

    public void AllRouteSearch(BigMapstationInfo infoO,BigMapstationInfo infoD){
        isRouteSearching = true;
        if (mRoutePlanListener != null){
            mRoutePlanListener.onRouteComputerStart();
        }

        allRouteList.clear();
        long lcurTime = System.currentTimeMillis();
        List<String> lsO = infoO.getBelongStationIDs();
        List<String> lsD = infoD.getBelongStationIDs();
        for (String strOID : lsO){
            for (String strDID : lsD){
                ReturnRoutes(strOID,strDID,lcurTime);
                allRouteList.addAll(routeList);
            }
        }
        //将结果告知外面
        RoutePlanResultCallBack();

    }
    private  Map<String, StationPassTime> routeO = new HashMap<>();
    private  Map<String, StationPassTime> routeD = new HashMap<>();
    private  List<StationPassTime> routeList = new ArrayList<>();//结果
    private  List<StationPassTime> allRouteList = new ArrayList<>(); //所有搜索结果的集合

    public void ReturnRoutes(String StaOCode, String StaDCode, long curtime)
    {
        SimpleDateFormat crsdf =new SimpleDateFormat("yyyy-MM-dd");
        Date curdate = new Date();
        String strData = crsdf.format(curdate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date df = null;
        try {
            df = sdf.parse(strData +" " + "08:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //开始时间
        long startTime = curtime;

        try {
            df = sdf.parse(strData +" " + "24:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //结束时间
        long endTime =  df.getTime();

        //Start from O
        routeO.clear();
        String rouStrO = "";
        SearchTrain(StaOCode,StaDCode, startTime, endTime, rouStrO);

        //Start from D
        routeD.clear();
        String rouStrD = "";
        SearchTrainReverse(StaOCode,StaDCode, startTime, endTime, rouStrD);

        Merge(StaOCode,StaDCode,startTime);

    }


    void SearchTrain(String StaOCode,String StaDCode, long beginTime, long endTime, String routeStr)
    {
        //找到目标线路
        String LineTTtraget = StaOCode.substring(0, 1);

        //找到能够乘坐的车次
        List<Integer> TrainTTtraget = new ArrayList<>();
        List<Integer> StaTTtraget = new ArrayList<>();
        List<String> direction = new ArrayList<>();//辅助用，表明当前运行线是上行还是下行，1为未标记过，2为已标记过一次
        int StaIndex = -1;
        for (int i = 0; i < MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().size(); i++)
        {
            StaIndex = MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(i).StaIndex(StaOCode);
            if (StaIndex != -1 && StaIndex !=  MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(i).StaTT.size() - 1)//该运行线经过该车站
            {
                if (MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(i).StaTT.get(StaIndex).StaTime >= beginTime){
                    if( MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(i).StaTT.get(StaIndex + 1).StaCode != ""
                            && !direction.contains(MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(i).StaTT.get(StaIndex + 1).StaCode)) //direction小于2，区分线路上下行
                    {
                        TrainTTtraget.add(i);
                        StaTTtraget.add(StaIndex);
                        direction.add(MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(i).StaTT.get(StaIndex + 1).StaCode);
                    }
                }
            }
        }
        for (int i = 0; i < direction.size(); i++)
        {
            String routeStrTemp = routeStr;
            for (int j = StaTTtraget.get(i); j < MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget.get(i)).StaTT.size(); j++)//搜索接下来的车站
            {
                String stationcode =  MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget.get(i)).StaTT.get(j).StaCode;

                //判断是否有回路,是否在同一换乘站旋转
                if (routeStrTemp != "" && stationcode != "")
                {
                    String[] stacount = routeStrTemp.substring(0, routeStrTemp.length() - 1).split("-");
                    boolean circle = false;
                    for (int k = 0; k < stacount.length - 1; k++)
                    {
                        String staTemp = stacount[k].split("\\|")[0];
                        //有回路,是否在同一换乘站旋转
                        if(MyApp.TransferStations.containsKey(stationcode)) {
                            if (MyApp.TransferStations.get(stationcode).contains(staTemp)) {
                                circle = true; break;
                            }
                        }
                        if(stationcode.equals(staTemp)) {
                            circle = true; break;
                        }
                    }
                    if (circle == true)
                        break;
                }
                //到达线路终点
                if(MyApp.Stations.containsKey(stationcode) == false){
                    break;
                }
                //判断是否到达时间节点
                //未达到最后一个车站
                if (endTime <= MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget.get(i)).StaTT.get(j).StaTime){
                    break;
                }

                //如果到达目的站
                if(MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget.get(i)).StaTT.get(j).StaCode.equals( StaDCode)){
                    //换乘站也按本线路继续走行
                    routeStrTemp += stationcode + "|" + String.valueOf(MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget.get(i)).StaTT.get(j).StaTime) + "-";

                    //----------记录
                    routeStrTemp = routeStrTemp.substring(0, routeStrTemp.length() - 1);
                    StationPassTime staTimeTemp = new StationPassTime(routeStrTemp);
                    if (!routeO.containsKey(staTimeTemp.LastTransferStation.StaCode))
                    {
                        routeO.put(staTimeTemp.LastTransferStation.StaCode, staTimeTemp);
                    }
                    else
                    {   //如果存在该换乘站，对比到达时间，如果早于记录时间，替换该路径
                        if(routeO.get(staTimeTemp.LastTransferStation.StaCode).LastTransferStation.StaTime > staTimeTemp.LastTransferStation.StaTime)
                        {
                            routeO.remove(staTimeTemp.LastTransferStation.StaCode);
                            routeO.put(staTimeTemp.LastTransferStation.StaCode, staTimeTemp);
                        }
                    }
                    break;
                }

                //记录路径
                if (!MyApp.TransferStations.containsKey(stationcode))
                {
                    //按本线路继续走行
                    routeStrTemp += stationcode + "|" + String.valueOf(MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget.get(i)).StaTT.get(j).StaTime) + "-";
                }
                else if (MyApp.TransferStations.containsKey(stationcode))//若是换乘站，则递归
                {
                    //换乘站也按本线路继续走行
                    routeStrTemp += stationcode + "|" + String.valueOf(MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget.get(i)).StaTT.get(j).StaTime) + "-";

                    //----------记录
                    {
                        routeStrTemp = routeStrTemp.substring(0, routeStrTemp.length() - 1);
                        StationPassTime staTimeTemp = new StationPassTime(routeStrTemp);
                        //相同名称（换乘站）车站集合
                        List<String> stationTemp = new ArrayList<>();
                        stationTemp.add(staTimeTemp.LastTransferStation.StaCode);
                        if (MyApp.TransferStations.containsKey(staTimeTemp.LastTransferStation.StaCode)) {
                            for(String item : MyApp.TransferStations.get(staTimeTemp.LastTransferStation.StaCode))
                                stationTemp.add(item);
                        }
                        //判断routeO中是否存在该车站
                        String isExist = "";
                        for(String item : stationTemp){
                            if(routeO.containsKey(item)){
                                isExist = item;break;
                            }
                        }
                        if (isExist == ""){
                            routeO.put(staTimeTemp.LastTransferStation.StaCode, staTimeTemp);
                        }
                        else
                        {   //如果存在该换乘站，对比到达时间，如果早于记录时间，替换该路径
                            if(routeO.get(isExist).LastTransferStation.StaTime > staTimeTemp.LastTransferStation.StaTime)
                            {
                                routeO.remove(isExist);
                                routeO.put(staTimeTemp.LastTransferStation.StaCode, staTimeTemp);
                            }
                        }
                    }

                    routeStrTemp = routeStrTemp + "-";
                    long time = MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget.get(i)).StaTT.get(j).StaTime;
                    //查找可换乘车站
                    for (int k = 0; k < MyApp.TransferStations.get(stationcode).size(); k++)
                    {
                        //换乘时间
                        long timeTemp = MyApp.transfertime;
                        //判断是否有回路,是否在同一换乘站旋转
                        String stationTemp = MyApp.TransferStations.get(stationcode).get(k);
                        if (routeStrTemp != "" && stationTemp != "")
                        {
                            String[] stacount = routeStrTemp.substring(0, routeStrTemp.length() - 1).split("-");
                            boolean circle = false;
                            for (int s = 0; s < stacount.length - 1; s++)
                            {
                                String staTemp = stacount[s].split("\\|")[0];
                                //有回路,是否在同一换乘站旋转
                                if(MyApp.TransferStations.containsKey(stationTemp)) {
                                    if (MyApp.TransferStations.get(stationTemp).contains(staTemp)) {
                                        circle = true; break;
                                    }
                                }
                                if(stationTemp.equals(staTemp)) {
                                    circle = true; break;
                                }
                            }
                            if (circle == true)
                                continue;
                        }
                        //递归
                        SearchTrain(stationTemp, StaDCode, time + timeTemp , endTime, routeStrTemp);
                    }
                }
            }
        }
    }


    void SearchTrainReverse(String StaOCode,String StaDCode, long beginTime, long endTime , String routeStr)
    {
        //找到目标线路
        String LineTTtraget = StaDCode.substring(0, 1);

        //找到能够乘坐的车次
        List<Integer> TrainTTtraget = new ArrayList<>();
        List<Integer> StaTTtraget = new ArrayList<>();
        List<String> direction = new ArrayList<>();//辅助用，表明当前运行线是上行还是下行，1为未标记过，2为已标记过一次
        int StaIndex = -1;
        for (int i =  MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().size() - 1; i >= 0 ; i--)
        {
            StaIndex = MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(i).StaIndex(StaDCode);
            if (StaIndex != -1 && StaIndex !=  0)//该运行线经过该车站
            {
                if (MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(i).StaTT.get(StaIndex).StaTime <= endTime
                        && MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(i).StaTT.get(StaIndex - 1).StaCode != ""
                        && !direction.contains(MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(i).StaTT.get(StaIndex - 1).StaCode)) //direction小于2，区分线路上下行
                {
                    TrainTTtraget.add(i);
                    StaTTtraget.add(StaIndex);
                    direction.add(MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(i).StaTT.get(StaIndex - 1).StaCode);
                }
            }
        }
        for (int i = 0; i < direction.size(); i++)
        {
            String routeStrTemp = routeStr;
            for (int j = StaTTtraget.get(i); j >= 0; j--)//搜索接下来的车站
            {
                String stationcode =  MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget.get(i)).StaTT.get(j).StaCode;

                //判断是否有回路
                if (routeStrTemp != "" && stationcode != "")
                {
                    String[] stacount = routeStrTemp.substring(0, routeStrTemp.length() - 1).split("-");
                    boolean circle = false;
                    for (int k = 0; k < stacount.length - 1; k++)
                    {
                        String staTemp = stacount[k].split("\\|")[0];
                        //有回路,是否在同一换乘站旋转
                        if(MyApp.TransferStations.containsKey(stationcode)) {
                            if (MyApp.TransferStations.get(stationcode).contains(staTemp)) {
                                circle = true; break;
                            }
                        }
                        if(stationcode .equals( staTemp)) {
                            circle = true; break;
                        }
                    }
                    if (circle == true)
                        break;
                }
                //到达线路终点
                if(MyApp.Stations.containsKey(stationcode) == false){
                    break;
                }
                //判断是否到达时间节点
               //未达到最后一个车站
                if (beginTime >=MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget.get(i)).StaTT.get(j).StaTime){
                    break;
                }

                //如果到达起始站
                if(MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget.get(i)).StaTT.get(j).StaCode.equals( StaOCode)) {
                    routeStrTemp += stationcode + "|" + String.valueOf(MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget.get(i)).StaTT.get(j).StaTime) + "-";
                    //----------记录
                    routeStrTemp = routeStrTemp.substring(0, routeStrTemp.length() - 1);
                    StationPassTime staTimeTemp = new StationPassTime(routeStrTemp);
                    if (!routeD.containsKey(staTimeTemp.LastTransferStation.StaCode))
                    {
                        routeD.put(staTimeTemp.LastTransferStation.StaCode, staTimeTemp);
                    }
                    else
                    {   //如果存在该换乘站，对比到达时间，如果早于记录时间，替换该路径
                        if (routeD.get(staTimeTemp.LastTransferStation.StaCode).LastTransferStation.StaTime < staTimeTemp.LastTransferStation.StaTime)
                        {
                            routeD.remove(staTimeTemp.LastTransferStation.StaCode);
                            routeD.put(staTimeTemp.LastTransferStation.StaCode, staTimeTemp);
                        }
                    }
                    break;
                }

                //记录路径
                if (!MyApp.TransferStations.containsKey(stationcode))
                {
                    //按本线路继续走行
                    routeStrTemp += stationcode + "|" + String.valueOf(MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget.get(i)).StaTT.get(j).StaTime) + "-";
                }
                else if (MyApp.TransferStations.containsKey(stationcode))//若是换乘站，则递归
                {
                    //换乘站也按本线路继续走行
                    routeStrTemp += stationcode + "|" + String.valueOf(MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget.get(i)).StaTT.get(j).StaTime) + "-";

                    //----------记录
                    {
                        routeStrTemp = routeStrTemp.substring(0, routeStrTemp.length() - 1);
                        StationPassTime staTimeTemp = new StationPassTime(routeStrTemp);
                        //相同名称（换乘站）车站集合
                        List<String> stationTemp = new ArrayList<>();
                        stationTemp.add(staTimeTemp.LastTransferStation.StaCode);
                        if (MyApp.TransferStations.containsKey(staTimeTemp.LastTransferStation.StaCode)) {
                            for (String item : MyApp.TransferStations.get(staTimeTemp.LastTransferStation.StaCode))
                                stationTemp.add(item);
                        }
                        //判断routeO中是否存在该车站
                        String isExist = "";
                        for (String item : stationTemp) {
                            if (routeD.containsKey(item)) {
                                isExist = item;
                                break;
                            }
                        }
                        if (isExist == "") {
                            routeD.put(staTimeTemp.LastTransferStation.StaCode, staTimeTemp);
                        } else {   //如果存在该换乘站，对比到达时间，如果早于记录时间，替换该路径
                            if (routeD.get(isExist).LastTransferStation.StaTime < staTimeTemp.LastTransferStation.StaTime) {
                                routeD.remove(isExist);
                                routeD.put(staTimeTemp.LastTransferStation.StaCode, staTimeTemp);
                            }
                        }
                    }

                    routeStrTemp = routeStrTemp + "-";
                    long time = MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget.get(i)).StaTT.get(j).StaTime;
                    //查找可换乘车站
                    for (int k = 0; k < MyApp.TransferStations.get(stationcode).size(); k++)
                    {
                        //换乘时间
                        long timeTemp = MyApp.transfertime;
                        //判断是否有回路,是否在同一换乘站旋转
                        String stationTemp = MyApp.TransferStations.get(stationcode).get(k);
                        if (routeStrTemp != "" && stationTemp != "")
                        {
                            String[] stacount = routeStrTemp.substring(0, routeStrTemp.length() - 1).split("-");
                            boolean circle = false;
                            for (int s = 0; s < stacount.length - 1; s++)
                            {
                                String staTemp = stacount[s].split("\\|")[0];
                                //有回路,是否在同一换乘站旋转
                                if(MyApp.TransferStations.containsKey(stationTemp)) {
                                    if (MyApp.TransferStations.get(stationTemp).contains(staTemp)) {
                                        circle = true; break;
                                    }
                                }
                                if(stationTemp.equals(staTemp)) {
                                    circle = true; break;
                                }
                            }
                            if (circle == true)
                                continue;
                        }
                        //递归
                        SearchTrainReverse(StaOCode, MyApp.TransferStations.get(stationcode).get(k), beginTime, time - timeTemp, routeStrTemp);
                    }
                }
            }

        }
    }

    void Merge(String OStaCode,String DStaCode, long StartTime)
    {
        List<String> StaOD = new  ArrayList<>();
        Map<String, StationPassTime> routeSta = new HashMap<>();
        //O点取经过站点的最早时间
        Map<String, Long> StaPassO = new HashMap<>();
        for (StationPassTime n : routeO.values()) {
            StaPassTime staTemp = n.LastTransferStation;

            if (!StaPassO.containsKey(staTemp.StaCode))
                StaPassO.put(staTemp.StaCode, staTemp.StaTime);
        }
        //D点取经过的最晚时间
        Map<String, Long> StaPassD = new HashMap<>();
        for (StationPassTime n : routeD.values()) {
            StaPassTime staTemp = n.LastTransferStation;

            if (!StaPassD.containsKey(staTemp.StaCode))
                StaPassD.put(staTemp.StaCode, staTemp.StaTime);
        }
        //取交集
        for(String n : StaPassO.keySet())
        {
            for (String m : StaPassD.keySet())
            {
                //规定在换乘站等待 或首末站
                if( MyApp.TransferStations.containsKey(n) && MyApp.TransferStations.containsKey((m))) {
                    //相同换乘站拼接处理
                    for (String item : MyApp.TransferStations.get(n)) {
                        if (item.equals(m)) {
                            int transfertime = 180;
                            if (StaPassD.get(m) - StaPassO.get(n) >= transfertime)
                                StaOD.add(n + "-" + StaPassO.get(n) + "-" + m + "-" + StaPassD.get(m));
                        }
                    }
                }
            }
        }

        //叠加可能的直达路径
        for(String n : StaPassO.keySet()) {
            if(n.equals(DStaCode))
                routeSta.put(routeO.get(n).routecode, routeO.get(n));
        }

        //绘制走行路径
        for(String n : StaOD)
        {
            String routeTemp = "";
            //O点截取路径
            String[] temp = n.split("-");
            routeTemp = routeO.get(temp[0]).routecode;

            //D点截取路径
            String[] passStationD = routeD.get(temp[2]).routecode.split("-");
            for (int j = passStationD.length - 1; j >= 0;j-- ){
                routeTemp += "-" + passStationD[j];
            }

            //处理路径
            StationPassTime staTemp = new StationPassTime(routeTemp);
            //剔除重复到达点的路径
            if (staTemp.cover())
            { continue; }
            else
            {//剔除相同线路走行
                if (!routeSta.containsKey(staTemp.routecode))  {
                    routeSta.put(staTemp.routecode, staTemp);
                }
            }
        }

        //重新搜索路径时间
        for(StationPassTime item : routeSta.values()) {
            int startIndex = 0;
            int endIndex = 2;
            //首站是换乘站
            if(!item.StaList.get(0).StaCode.substring(0, 1) .equals( item.StaList.get(1).StaCode.substring(0, 1)))
                startIndex = 1;
            //末站是换乘站
            if(!item.StaList.get(item.StaList.size() - 1).StaCode.substring(0, 1).equals( item.StaList.get(item.StaList.size() - 2).StaCode.substring(0, 1)))
                endIndex = 3;

            int TrainTTtraget = -1;
            {
                String LineTTtraget = item.StaList.get(startIndex).StaCode.substring(0, 1);
                int StaIndex = -1;
                for (int j = 0; j < MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().size(); j++) {
                    StaIndex = MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(j).StaIndex(item.StaList.get(startIndex).StaCode);
                    if (StaIndex != -1 && StaIndex !=  MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(j).StaTT.size() - 1
                            && MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(j).StaTT.get(StaIndex).StaTime >= StartTime
                            && MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(j).StaTT.get(StaIndex + 1).StaCode.equals(item.StaList.get(startIndex + 1).StaCode)) {
                        TrainTTtraget = j; break;
                    }
                }
            }
            for (int i = startIndex; i < item.StaList.size() - endIndex; i++) {
                if(TrainTTtraget != -1){
                    String LineTTtraget = item.StaList.get(i).StaCode.substring(0, 1);
                    int StaIndex = MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget).StaIndex(item.StaList.get(i).StaCode);
                    item.StaList.get(i).StaTime = MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget).StaTT.get(StaIndex).StaTime;
                }
                //如果遇上换乘站
                if (item.StaList.get(i).StaCode.substring(0, 1).equals(item.StaList.get(i + 1).StaCode.substring(0, 1)) == false) {
                    long substarttime = item.StaList.get(i).StaTime + MyApp.transfertime;

                    String LineTTtraget = item.StaList.get(i + 1).StaCode.substring(0, 1);
                    int StaIndex = -1;
                    for (int j = 0; j < MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().size(); j++) {
                        StaIndex = MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(j).StaIndex(item.StaList.get(i + 1).StaCode);
                        if (StaIndex != -1 && StaIndex !=  MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(j).StaTT.size() - 1
                                && MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(j).StaTT.get(StaIndex).StaTime >= substarttime
                                && MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(j).StaTT.get(StaIndex + 1).StaCode.equals(item.StaList.get(i + 2).StaCode)) {
                            TrainTTtraget = j; break;
                        }
                    }
                }
            }
            for(int i = item.StaList.size() - endIndex;i<item.StaList.size() - endIndex + 2;i++) {
                if (TrainTTtraget != -1) {
                    String LineTTtraget = item.StaList.get(i).StaCode.substring(0, 1);
                    int StaIndex = MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget).StaIndex(item.StaList.get(i).StaCode);
                    item.StaList.get(i).StaTime = MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget).StaTT.get(StaIndex).StaTime;
                }
            }

            //如果首末站是换乘站，则重新赋值
            if(startIndex == 1)
                item.StaList.get(0).StaTime =  item.StaList.get(1).StaTime;
            if(endIndex == 3)
                item.StaList.get(item.StaList.size() - 1).StaTime =  item.StaList.get(item.StaList.size() - 2).StaTime;

            //重新生成code
            item.routecode="";
            for(StaPassTime n : item.StaList){
                item.routecode += n.StaCode+"|"+n.StaTime+"-";
            }
            item.routecode = item.routecode.substring(0, item.routecode.length() - 1);

            //计算路径costtime
            item.costtime = item.StaList.get(item.StaList.size() - 1).StaTime - item.StaList.get(0).StaTime;
        }
        //以及 最晚上车末班车时间
        for(StationPassTime item : routeSta.values()) {
            int startIndex = 0;
            int endIndex = 0;
            //首站是换乘站
            if(!item.StaList.get(0).StaCode.substring(0, 1) .equals( item.StaList.get(1).StaCode.substring(0, 1)))
                startIndex = 1;
            //末站是换乘站
            if(!item.StaList.get(item.StaList.size() - 1).StaCode.substring(0, 1).equals( item.StaList.get(item.StaList.size() - 2).StaCode.substring(0, 1)))
                endIndex = 1;

            int TrainTTtraget = -1;
            for (int i = item.StaList.size() - 1 - endIndex; i >= 1 + startIndex ; i--) {
                if(TrainTTtraget == -1){
                    String LineTTtraget = item.StaList.get(i).StaCode.substring(0, 1);
                    int StaIndex = -1;
                    for (int j =  MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().size() - 1; j >= 0 ; j--)  {
                        StaIndex = MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(j).StaIndex(item.StaList.get(i).StaCode);
                        if (StaIndex != -1 && StaIndex != 0
                                && MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(j).StaTT.get(StaIndex - 1).StaCode.equals(item.StaList.get(i - 1).StaCode)) {
                            TrainTTtraget = j; break;
                        }
                    }
                }
                //如果遇上换乘站
                if (item.StaList.get(i).StaCode.substring(0, 1).equals(item.StaList.get(i - 1).StaCode.substring(0, 1)) == false) {
                    String LineTTtraget = item.StaList.get(i).StaCode.substring(0, 1);
                    int StaIndex = MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget).StaIndex(item.StaList.get(i).StaCode);

                    long subendtime = MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget).StaTT.get(StaIndex).StaTime
                            - MyApp.transfertime;

                    LineTTtraget = item.StaList.get(i - 1).StaCode.substring(0, 1);
                    for (int j =  MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().size() - 1; j >= 0 ; j--)  {
                        StaIndex = MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(j).StaIndex(item.StaList.get(i - 1).StaCode);
                        if (StaIndex != -1 && StaIndex != 0
                                && MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(j).StaTT.get(StaIndex).StaTime <= subendtime
                                && MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(j).StaTT.get(StaIndex - 1).StaCode.equals(item.StaList.get(i - 2).StaCode)) {
                            TrainTTtraget = j; break;
                        }
                    }
                }
            }
            //最晚上车时间赋值
            if (TrainTTtraget != -1) {
                String LineTTtraget = item.StaList.get(startIndex).StaCode.substring(0, 1);
                int StaIndex = MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget).StaIndex(item.StaList.get(startIndex).StaCode);
                item.lastboardingtime = MyApp.NetTimeTable.get(LineTTtraget).getmRailWayTimeTabels().get(TrainTTtraget).StaTT.get(StaIndex).StaTime;
            }
        }

        routeList.clear();
        List<Long> orderedCostTime = new ArrayList<>();
        List<String> orderedCodeTime = new ArrayList<>();
        for (String n : routeSta.keySet())
        {
            if(!orderedCodeTime.contains(routeSta.get(n).routecode)) {
                orderedCodeTime.add(routeSta.get(n).routecode);
                if (routeList.size() < 3) {
                    //添加新项
                    orderedCostTime.add(routeSta.get(n).costtime);
                    routeList.add(routeSta.get(n));
                } else {
                    if (Collections.max(orderedCostTime) > routeSta.get(n).costtime) {
                        //删除原来
                        routeList.remove(orderedCostTime.indexOf(Collections.max(orderedCostTime)));
                        orderedCostTime.remove(Collections.max(orderedCostTime));

                        //添加新项
                        orderedCostTime.add(routeSta.get(n).costtime);
                        routeList.add(routeSta.get(n));
                    }
                }
            }
        }

        //如果routeList为空继续向下执行会导致程序崩溃
        if (routeList.size() == 0)
            return;
        //剔除不合理路径
        long costtimemin = routeList.get(0).costtime;
        for(StationPassTime n : routeList){
            if(n.costtime < costtimemin){
                costtimemin = n.costtime;
            }
        }
        for(int i=0;i<routeList.size();i++){
            if(routeList.get(i).costtime > costtimemin * 2){
                routeList.remove(i);i--;
            }
        }
    }

    //将结果回掉到外部显示
    public void RoutePlanResultCallBack(){

        if (mRoutePlanListener != null){
            mRoutePlanListener.onRouteComputeResults(allRouteList);
        }


    }
}
