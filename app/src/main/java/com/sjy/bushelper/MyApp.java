package com.sjy.bushelper;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Xml;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.SDKInitializer;
import com.sjy.beans.BigMapstationInfo;
import com.sjy.beans.RailWayLineItem;
import com.sjy.beans.RailWayTimeTable;
import com.sjy.beans.RouteItemBean;
import com.sjy.beans.RouteLineItemBean;
import com.sjy.listener.IActivityStatusListener;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import im.fir.sdk.FIR;


/**
 * Created by Administrator on 2016/3/24.
 */
public class MyApp extends Application{

    List<BigMapstationInfo> mFlatBigMapStationpos;
    List<RailWayLineItem> mRailWayLineItes;
    List<RailWayTimeTable> mRailWayTimeTables;
    private IActivityStatusListener mActivityStatusListener;

    static private MyApp ins = null;
    List<RouteLineItemBean> mAllRouteLine = new ArrayList<>();
    List<RouteItemBean> mJsonRouteStations = new ArrayList<>();

    private boolean mglobalDataisDone = false;

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
            mAllRouteLine = LoadRouteLineJson("routeline_json");
            mJsonRouteStations = LoadLocalJson("station_json");
            mFlatBigMapStationpos = LoadBigMapStations_flat("routestation_pos_flat_json");
            mRailWayLineItes = LoadRailWayLineItems("railway_station_json");
            mRailWayTimeTables = LoadRailWayTimeTables("railway_train_timetable_json");
            processRailWayLineTimeTables();
            int y = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        mglobalDataisDone = true;
        //
        if (mActivityStatusListener != null){
            mActivityStatusListener.onDataInitFinished();
        }
    }


    public List<RouteLineItemBean> getAllRouteLine(){
            return mAllRouteLine;
    }

    public List<BigMapstationInfo> getBigMapStationPos_flat(){
        return mFlatBigMapStationpos;
    }

    public List<RouteLineItemBean> LoadRouteLine(String strSource) throws Exception {
        XmlPullParser parser = Xml.newPullParser(); //由android.util.Xml创建一个XmlPullParser实例
        InputStream is = null;
        ArrayList<RouteLineItemBean> tables = null;
        RouteLineItemBean items = null;
        is = getApplicationContext().getAssets().open("resourcedata/" + strSource);
        parser.setInput(is, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    tables = new ArrayList<RouteLineItemBean>();
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("routeline")) {
                        //  = new Book();
                        items = new RouteLineItemBean();
                        items.setStrRouteID(parser.getAttributeValue("", "routeid"));
                        items.setStrRouteName(parser.getAttributeValue("", "routename"));
                        items.setStrRouteDesc(parser.getAttributeValue("", "desc"));
                        items.setStrTime(parser.getAttributeValue("","time"));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("routeline")) {
                        tables.add(items);
                    }
                    break;
            }
            eventType = parser.next();

        }
        return tables;
    }


    public List<RouteItemBean> getJsonStations() {return  mJsonRouteStations; }


    public List<RouteItemBean> LoadLocalJson(String strJsonPath) throws Exception{
        InputStream is = getApplicationContext().getAssets().open("resourcedata/" + strJsonPath);
        String strJson = ConvertStream2Json(is);
        List<RouteItemBean> lsStations = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(strJson);
        JSONArray jsonArray = jsonObject.getJSONArray("gd_stations");
        for (int i = 0 ; i < jsonArray.size() ;++i){
            JSONObject jsub = jsonArray.getJSONObject(i);

            RouteItemBean ib = new RouteItemBean();
            ib.setStrStationID(jsub.getString("station_id"));
            ib.setStrStationName(jsub.getString("station_name"));
            ib.setLat(jsub.getDouble("lat"));
            ib.setLon(jsub.getDouble("lon"));

            JSONArray jolder = jsub.getJSONArray("route_older");
            for (int x = 0 ; x < jolder.size() ;++x){
                JSONObject odobj = jolder.getJSONObject(x);
                for (Map.Entry<String, Object> entry : odobj.entrySet()){
                    String routeID = entry.getKey();
                    int oder = Integer.parseInt((String) entry.getValue());
                    ib.AddRouteOder(routeID,oder);
                }
            }
            lsStations.add(ib);
        }

        return lsStations;
    }

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


    public List<RouteLineItemBean> LoadRouteLineJson(String strJsonPath) throws Exception{
        InputStream is = getApplicationContext().getAssets().open("resourcedata/" + strJsonPath);
        String strJson = ConvertStream2Json(is);

        List<RouteLineItemBean> lsRouteLine = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(strJson);
        JSONArray jsonArray = jsonObject.getJSONArray("gd_lines");
        for (int i = 0 ; i < jsonArray.size() ;++i) {
            JSONObject jsub = jsonArray.getJSONObject(i);
            RouteLineItemBean rlitem = new RouteLineItemBean();
            rlitem.setStrRouteID(jsub.getString("route_id"));
            rlitem.setStrRouteName(jsub.getString("route_name"));
            rlitem.setStrRouteDesc(jsub.getString("station_desc"));
            rlitem.setStrTime(jsub.getString("time"));
            rlitem.setColor(Color.parseColor(jsub.getString("route_descolor")));
            rlitem.setStrStartTime(jsub.getString("start_time"));
            rlitem.setStrEndTime(jsub.getString("end_time"));
            rlitem.setStrStartStation(jsub.getString("start_station"));
            rlitem.setStrEndStation(jsub.getString("end_station"));
            JSONArray jolder = jsub.getJSONArray("route_node");
            for (int x = 0 ; x < jolder.size() ;++x){
                JSONObject odobj = jolder.getJSONObject(x);
                double lat = odobj.getDoubleValue("lat");
                double lon = odobj.getDoubleValue("lon");
                rlitem.AddRouteLatLng(lat,lon);
            }

//            JSONArray jpoint = jsub.getJSONArray("route_point");
//            for (int x = 0 ; x < jpoint.size() ;++x){
//                JSONObject odobj = jpoint.getJSONObject(x);
//                int xp = odobj.getIntValue("x");
//                int yp = odobj.getIntValue("y");
//                rlitem.AddRoutePoint(xp,yp);
//            }
            lsRouteLine.add(rlitem);
        }

        return lsRouteLine;
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
        InputStream is = getApplicationContext().getAssets().open("resourcedata/" + strJsonPath);
        String strJson = ConvertStream2Json(is);
        List<RailWayLineItem> lsRailWayItems = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(strJson);
        JSONArray jsonArray = jsonObject.getJSONArray("gd_stations");
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
                items.addStation(strationid);
                if (jstation.getString("Transimition") != null){
                    items.addTransition(strationid,jstation.getString("Transimition"));
                }
            }
            lsRailWayItems.add(items);
        }
        return lsRailWayItems;

    }

    public List<RailWayTimeTable> LoadRailWayTimeTables(String strJsonPath) throws Exception{
        InputStream is = getApplicationContext().getAssets().open("resourcedata/" + strJsonPath);
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

    public boolean getGlobalDataIsDone(){
        return mglobalDataisDone;
    }

    public List<RailWayLineItem> getRailWayLineItes(){
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
}
