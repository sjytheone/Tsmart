package com.sjy.bushelper;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.fir.sdk.FIR;

import static com.sjy.bigimagemap.TileMapRailOption.RAILSTATUS_EMPTY;
import static com.sjy.bigimagemap.TileMapRailOption.RAILSTATUS_FULL;
import static com.sjy.bigimagemap.TileMapRailOption.RAILSTATUS_NORMAL;


/**
 * Created by Administrator on 2016/3/24.
 */
public class MyApp extends Application{

    List<BigMapstationInfo> mFlatBigMapStationpos;
    List<RailWayLineItem> mRailWayLineItes;
    List<RailWayTimeTable> mRailWayTimeTables;
    List<RouteItemBean> mJsonRouteStations = new ArrayList<>();

    //pace 添加全局变量
    static final public Map<String, String> Stations = new HashMap<>();
    static final public Map<String, List<String>> TransferStations = new HashMap<>();
    static final public Map<String, RailWayLineItem> NetTimeTable = new HashMap<>();
    static final public long transfertime = 180000;//换乘时间
    static final public Map<String, String> BusinessInfo = new HashMap<>();
    //static final public Map<String, String> StationsPic = new HashMap<>();;//换乘时间
	
    String mstrCurDateTime;
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
            SimpleDateFormat crsdf =new SimpleDateFormat("yyyy-MM-dd");
            String strData = crsdf.format(new Date());
            mstrCurDateTime = strData;
            //原始的站点信息
            //mJsonRouteStations = LoadLocalJson("station_json");
            //大图片上的坐标点
            long lstart = System.currentTimeMillis();
            mFlatBigMapStationpos = LoadBigMapStations_flat("net_data/bigmap_stationpos.json");
            long vl = System.currentTimeMillis() - lstart;
            Log.d("MyApp","--------mFlatBigMapStationpos time :" + vl);
            //路线信息，经过的站点
            lstart = System.currentTimeMillis();
            mRailWayLineItes = LoadRailWayLineItems("net_data/line_stations.json");
            vl = System.currentTimeMillis() - lstart;
            Log.d("MyApp","-----------mRailWayLineItes time :" + vl);

            lstart = System.currentTimeMillis();
            mRailWayTimeTables = LoadRailWayTimeTables("net_data/train.json");
            vl = System.currentTimeMillis() - lstart;
            Log.d("MyApp","-----------mRailWayTimeTables time :" + vl);

            lstart = System.currentTimeMillis();
            processRailWayLineTimeTables();

            //---------全局变量赋值
            for (RouteItemBean item : mJsonRouteStations) {
                Stations.put(item.getStrStationID(),item.getStrStationName());
            }
            //换乘站
            TransferStations.put("1005",Arrays.asList("3003"));
            TransferStations.put("3003",Arrays.asList("1005"));
            TransferStations.put("1014",Arrays.asList("3012"));
            TransferStations.put("3012",Arrays.asList("1014"));
            TransferStations.put("1016",Arrays.asList("2007"));
            TransferStations.put("2007",Arrays.asList("1016"));
            TransferStations.put("1027",Arrays.asList("2018","5023"));
            TransferStations.put("2018",Arrays.asList("1027","5023"));
            TransferStations.put("5023",Arrays.asList("1027","2018"));

            //时刻表
            for (RailWayLineItem item : mRailWayLineItes) {
                NetTimeTable.put(item.getRailWayLineID().substring(5),item);
            }

            BusinessInfo.put("0028","中国女人街\r\n兴隆大家庭");
            BusinessInfo.put("0027","亿丰时代广场\r\n万达广场");
//            for(BigMapstationInfo item : mFlatBigMapStationpos)
//            {
//                for(String n : Stations.keySet()){
//                    if(item.getStationName().equals(Stations.get(n)))
//                        StationsPic.put(item.getStationID(),n);
//                }
//            }
            //--------end全局变量赋值


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
        InputStream is = getApplicationContext().getAssets().open(strJsonPath);
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
            JSONArray arrayStations = jsub.getJSONArray("line_stations");
            for (int y = 0 ; y < arrayStations.size() ; ++y) {
                JSONObject jstation = arrayStations.getJSONObject(y);
                String strationid = jstation.getString("ID");
                station.addBelongStationIDs(strationid);
            }

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

                table.AddNode(passStation, mstrCurDateTime + " " +ArrTime);
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

    public BigMapstationInfo findBigmapStationByBelongID(String strBelongID){
        for (BigMapstationInfo info : mFlatBigMapStationpos){
            List<String> belongsations = info.getBelongStationIDs();
            for (String strStationID : belongsations){
                if (strStationID.compareTo(strBelongID) == 0)
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

    public Bitmap getRailDrawable(String strRouteID){
        String railDrawable = "bigmaps/rail_bitmaps/rail_4.png";
        if (strRouteID.contains("1")){
            railDrawable = "bigmaps/rail_bitmaps/rail_1.png";
        } else if (strRouteID.contains("2")){
            railDrawable = "bigmaps/rail_bitmaps/rail_2.png";
        } else if (strRouteID.contains("3")){
            railDrawable = "bigmaps/rail_bitmaps/rail_3.png";
        } else if (strRouteID.contains("5")){
            railDrawable = "bigmaps/rail_bitmaps/rail_5.png";
        }
        return getBitmapFromAssets(railDrawable);
    }

    public Bitmap getStatusDrawable(int status){
        String pngPath = "bigmaps/rail_bitmaps/rail_normal.png";
        if (status == RAILSTATUS_EMPTY){
            pngPath = "bigmaps/rail_bitmaps/rail_empty.png";
        }else if (status == RAILSTATUS_NORMAL){
            pngPath = "bigmaps/rail_bitmaps/rail_normal.png";
        }else if (status == RAILSTATUS_FULL){
            pngPath = "bigmaps/rail_bitmaps/rail_full.png";
        }
        return getBitmapFromAssets(pngPath);
    }

    public Bitmap getRailDirection(String strRouteID,int direction){
        String directionPath = "bigmaps/rail_bitmaps/";
        String railID = "rail_4";
        if (strRouteID.contains("1")){
            railID = "rail_1";
        } else if (strRouteID.contains("2")){
            railID = "rail_2";
        } else if (strRouteID.contains("3")){
            railID = "rail_3";
        } else if (strRouteID.contains("5")){
            railID = "rail_5";
        }
        String dirp = "_left";
        if (direction == 2){
            dirp = "_right";
        }
        directionPath += railID + dirp + ".png";
        return getBitmapFromAssets(directionPath);
    }
}
