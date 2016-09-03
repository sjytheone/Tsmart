package com.sjy.bushelper;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.SDKInitializer;
import com.sjy.beans.BigMapstationInfo;
import com.sjy.beans.RailWayLineItem;
import com.sjy.beans.RailWayTimeTable;
import com.sjy.beans.RouteItemBean;
import com.sjy.listener.IActivityStatusListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import im.fir.sdk.FIR;


/**
 * Created by Administrator on 2016/3/24.
 */
public class MyApp extends Application{

    List<BigMapstationInfo> mFlatBigMapStationpos;
    List<RailWayLineItem> mRailWayLineItes;
    List<RailWayTimeTable> mRailWayTimeTables;
    List<RouteItemBean> mJsonRouteStations = new ArrayList<>();

    private IActivityStatusListener mActivityStatusListener;

    static private MyApp ins = null;


    public static MyApp theIns(){return ins;}
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
        FIR.init(this);
        SoftUpdate.Initialize();
        ins = this;
    }

    public void startInitGlobalData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                InitGlobleData();
            }
        }).start();
    }


    private String getAppFileRoot()
    {
        String str = null;
        File localFile = getExternalFilesDir(null);
        if (localFile != null)
            str = localFile.getAbsolutePath() + "/";
        return str;
    }


    public Bitmap getBitmapFromAssets(String strPath){
        Bitmap btmap = null;
        try {
            //getAssets().open(strFile);
            btmap = BitmapFactory.decodeStream(getAssets().open(strPath));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return  btmap;
    }


    public void InitGlobleData(){
        try {
            //原始的站点信息
            //mJsonRouteStations = LoadLocalJson("station_json");
            //大图片上的坐标点
            mFlatBigMapStationpos = LoadBigMapStations_flat("routestation_pos_flat_json");
            //路线信息，经过的站点
            mRailWayLineItes = LoadRailWayLineItems("net_data/line_stations.json");
            mRailWayTimeTables = LoadRailWayTimeTables("net_data/train.json");

            processRailWayLineTimeTables();
            int y = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mActivityStatusListener != null){
            mActivityStatusListener.onDataInitFinished();
        }
    }

    public List<BigMapstationInfo> getBigMapStationPos_flat(){
        return mFlatBigMapStationpos;
    }

    public List<RouteItemBean> getJsonStations() {return  mJsonRouteStations; }


    private static String ConvertStream2Json(InputStream inputStream)
    {
        String jsonStr = "";
        // ByteArrayOutputStream相当于内存输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        // 将输入流转移到内存输出流中
        try
        {
            while ((len = inputStream.read(buffer, 0, buffer.length)) != -1)
            {
                out.write(buffer, 0, len);
            }
            // 将内存流转换为字符串
            jsonStr = new String(out.toByteArray());
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonStr;
    }


    public List<BigMapstationInfo> LoadBigMapStations_flat(String strJsonPath) throws Exception{
        InputStream is = getApplicationContext().getAssets().open("resourcedata/" + strJsonPath);
        String strJson = ConvertStream2Json(is);

        List<BigMapstationInfo> lsStationInfo = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(strJson);
        JSONArray jsonArray = jsonObject.getJSONArray("gd_stations");
        for (int i = 0 ; i < jsonArray.size() ;++i) {
            JSONObject jsub = jsonArray.getJSONObject(i);
            BigMapstationInfo station = new BigMapstationInfo();
            station.setStationID(jsub.getString("station_id"));
            station.setStationName(jsub.getString("station_name"));
            station.setDotX(jsub.getFloatValue("dotX"));
            station.setDotY(jsub.getFloatValue("dotY"));
           lsStationInfo.add(station);
        }
        return lsStationInfo;
    }

    public List<RailWayLineItem> LoadRailWayLineItems(String strJsonPath) throws Exception{
        InputStream is = getApplicationContext().getAssets().open(strJsonPath);
        String strJson = ConvertStream2Json(is);
        List<RailWayLineItem> lsRailWayItems = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(strJson);
        JSONArray jsonArray = jsonObject.getJSONArray("line_stations");
        for (int i = 0 ; i < jsonArray.size() ;++i) {
            JSONObject jsub = jsonArray.getJSONObject(i);
            RailWayLineItem items = new RailWayLineItem();
            items.setRailWayLineID(jsub.getString("LineName"));
            items.setRailWayLineName(jsub.getString("route_name"));
            items.setFirstRailWayTime(jsub.getString("first_railway"));
            items.setLastRailWayTime(jsub.getString("last_railway"));
            JSONArray arrayStations = jsub.getJSONArray("Stations");
            for (int y = 0 ; y < arrayStations.size() ; ++y){
                JSONObject jstation = arrayStations.getJSONObject(y);
                String strationid = jstation.getString("ID");
                String strationName = jstation.getString("Name");
                items.addStation(strationid);
                if (jstation.getString("Transimition") != null){
                    items.addTransition(strationid,jstation.getString("Transimition"));
                }
                RouteItemBean routeItemBean = new RouteItemBean();
                routeItemBean.setStrStationID(strationid);
                routeItemBean.setStrStationName(strationName);
                mJsonRouteStations.add(routeItemBean);

            }
            lsRailWayItems.add(items);
        }
        return lsRailWayItems;

    }

    public List<RailWayTimeTable> LoadRailWayTimeTables(String strJsonPath) throws Exception{
        InputStream is = getApplicationContext().getAssets().open(strJsonPath);
        String strJson = ConvertStream2Json(is);
        List<RailWayTimeTable> lsRailWayTimeTables = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(strJson);

        JSONArray jsonArray = jsonObject.getJSONArray("railwaytimetable");
        for (int i = 0 ; i < jsonArray.size() ;++i) {
            JSONObject jsub = jsonArray.getJSONObject(i);
            RailWayTimeTable table = new RailWayTimeTable();
            table.setStrRailWayTrainID(jsub.getString("TrainID"));
            JSONArray trainStations = jsub.getJSONArray("TrainStations");
            for (int y = 0 ; y < trainStations.size() ; ++y){
                JSONObject jstation = trainStations.getJSONObject(y);
                String passStation = jstation.getString("PassStationID");
                String ArrTime = jstation.getString("ArrTime");
                String DepTime = jstation.getString("DepTime");
                table.AddNode(passStation, ArrTime);
            }
            table.ProcessDatas();
            lsRailWayTimeTables.add(table);
        }
        return lsRailWayTimeTables;

    }


    public List<RailWayTimeTable> getRailWayTimeTables(){
        return mRailWayTimeTables;
    }

    public RouteItemBean findStation(String strStationID){
        if (strStationID == null)
            return null;
        for(RouteItemBean item : mJsonRouteStations){
            if (item.getStrStationID().compareTo(strStationID) ==0){
                return item;
            }
        }
        return null;
    }

    public BigMapstationInfo findBigmapStation(String strStationID){
        //List<BigMapstationInfo> mFlatBigMapStationpos;
        for (BigMapstationInfo info : mFlatBigMapStationpos){
            if (info.getStationID().compareTo(strStationID) == 0){
                return info;
            }
        }
        return null;
    }

    public void setActivityStatusListener(IActivityStatusListener listener){
        mActivityStatusListener = listener;
    }

    public List<RailWayLineItem> getRailWayLineItems(){
        return mRailWayLineItes;
    }

    public RailWayLineItem getLineItem(String strLineID){
        for (RailWayLineItem item : mRailWayLineItes){
            if (item.getRailWayLineID().compareTo(strLineID) == 0){
                return  item;
            }
        }
        return null;
    }

    public List<String> findeLinesByStation(String stationID){
        List<String> ls = new ArrayList<>();
        for (RailWayLineItem item : mRailWayLineItes){
            if (item.hasStation(stationID)){
                ls.add(item.getRailWayLineID());
            }
        }
        return ls;
    }

    //查找两站点是否在同一路径
    public RailWayLineItem findShortLine(String startID,String endID){
        List<String> lsStart = findeLinesByStation(startID);
        List<String> lsEnd = findeLinesByStation(endID);
        lsStart.retainAll(lsEnd);
        if (!lsStart.isEmpty()){
            return getLineItem(lsStart.get(0));
        }
        return null;
    }

    public void processRailWayLineTimeTables(){
        for(RailWayTimeTable timeTable : mRailWayTimeTables){
            String routeID = timeTable.getBelongRouteID();
            RailWayLineItem line = getLineItem(routeID);
            line.AddRailWayTimeTable(timeTable);
        }
    }

    //通过站点ID查询站点信息的API
    public RouteItemBean getRailWayStation(String strStationID){
        for (RouteItemBean info : mJsonRouteStations){
            if (info.getStrStationID().compareTo(strStationID) == 0){
                return info;
            }
        }
        return null;
    }
}
