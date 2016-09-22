package com.sjy.bigimagemap;

import android.graphics.Bitmap;
import android.graphics.Point;

import com.sjy.beans.BigMapstationInfo;
import com.sjy.beans.RailWayTimeTable;
import com.sjy.beans.TimeItemBean;
import com.sjy.bushelper.MyApp;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

/**
 * Created by sjy on 2016/7/9 0009.
 */
public class TileMapRailOption implements Serializable {

    public final static int RAILSTATUS_EMPTY = 0X00;
    public final static int RAILSTATUS_NORMAL = 0X01;
    public final static int RAILSTATUS_FULL = 0X02;

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
    private List<String> lsRouteLine; //途径线路
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
    private RailWayTimeTable mRailWayTimeTable;
    public TileMapRailOption(List<String> ls){
        lsRouteLine = ls;
    }

    public void setRailWayTimeTable(RailWayTimeTable timeTable){
        mRailWayTimeTable = timeTable;
    }

    public void setCanMove(boolean bMove){ mbCanMove = bMove;}

    public void setRailStatus(int status){
        mrailstatus = status;
//        if (mRailOption != null){
//            String pngPath = "bigmaps/rail_empty.png";
//            if (status == RAILSTATUS_EMPTY){
//                pngPath = "bigmaps/rail_empty.png";
//            }else if (status == RAILSTATUS_NORMAL){
//                pngPath = "bigmaps/rail_normal.png";
//            }else if (status == RAILSTATUS_FULL){
//                pngPath = "bigmaps/rail_full.png";
//            }
//            mRailOption.setDrawable(MyApp.theIns().getBitmapFromAssets(pngPath));
//        }
    }

    public void randomStatus(){
        mrailstatus = getRandomStatus();
        if (mRailOption != null){
            Bitmap bitmap = MyApp.theIns().getStatusDrawable(mrailstatus);
            mRailOption.setmBitmapRail(bitmap);
        }
    }

    private int mrailstatus = RAILSTATUS_EMPTY;
    private boolean mbCanMove = true;
    //前进
    public BigMapstationInfo stepForward(){
        ncurStep++;
        if (ncurStep >= lsRouteLine.size()){
            ncurStep = 0;
        }
        String strBelongStationID = lsRouteLine.get(ncurStep);
        BigMapstationInfo info = MyApp.theIns().findBigmapStationByBelongID(strBelongStationID);
        if (mRailOption != null){
            mRailOption.setPoint(new Point((int)info.getDotX(),(int)info.getDotY()));
        }
        return info;
    }

    public void restoreStep(){
        ncurStep = 0;
        String strBelongStationID = lsRouteLine.get(ncurStep);
        BigMapstationInfo info = MyApp.theIns().findBigmapStationByBelongID(strBelongStationID);
        if (mRailOption != null){
            mRailOption.setPoint(new Point((int)info.getDotX(),(int)info.getDotY()));
        }
    }

    public boolean canMove(){
        return mbCanMove;
    }

    public void setOverlayOption(BigMapDrawOverlay marker){
        mRailOption = marker;
    }

    private BigMapDrawOverlay mRailOption;

    public void initRandomPos(){
        Random ran =new Random(System.currentTimeMillis());
        ncurStep = ran.nextInt(lsRouteLine.size());
        String strBelongStationID = lsRouteLine.get(ncurStep);
        BigMapstationInfo info = MyApp.theIns().findBigmapStationByBelongID(strBelongStationID);
        if (mRailOption != null){
            mRailOption.setPoint(new Point((int)info.getDotX(),(int)info.getDotY()));
        }
    }

    private int getRandomStatus(){
        Random ran =new Random(System.currentTimeMillis());
        return ran.nextInt(3);
    }

    public String getRailStatus(){
        String strStatus = "空闲";
        if (mrailstatus == RAILSTATUS_EMPTY){
            strStatus = "空闲";
        }else if (mrailstatus == RAILSTATUS_NORMAL){
            strStatus = "正常";
        }else {
            strStatus = "拥挤";
        }

        return strStatus;
    }

    public String getCurStationName(){
        String strStation = "";
        String strBelongStationID = lsRouteLine.get(ncurStep);
        BigMapstationInfo info = MyApp.theIns().findBigmapStationByBelongID(strBelongStationID);
        if (info != null){
            strStation = info.getStationName();
        }
        return strStation;
    }

    public String getCurStationID(){
        String strStation = "";
        String strBelongStationID = lsRouteLine.get(ncurStep);
        BigMapstationInfo info = MyApp.theIns().findBigmapStationByBelongID(strBelongStationID);

        if (info != null){
            strStation = info.getStationID();
        }
        return strStation;
    }

    public String getDestination(){
        String destination = "";
        String strBelongStationID = lsRouteLine.get(lsRouteLine.size() - 1);
        BigMapstationInfo info = MyApp.theIns().findBigmapStationByBelongID(strBelongStationID);
        destination = info.getStationName();
        return destination;
    }

    public String getNextStationLeavingTime(){
        TimeItemBean ib = mRailWayTimeTable.getArrTime(getNextStationID());
        long leavingTime = ib.getMillisTime() - System.currentTimeMillis();
        //long hours = (leavingTime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (leavingTime % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (leavingTime % (1000 * 60)) / 1000;
        //String dateStr = hours+":"+minutes+":"+seconds;
        String dateStr = String.format("%02d'%02d''",minutes,seconds);
        //String.format(dateStr, )
        return dateStr;
    }

    public long getNextStationLevingTimeLong(){
        TimeItemBean ib = mRailWayTimeTable.getArrTime(getNextStationID());
        long leavingTime = ib.getMillisTime() - System.currentTimeMillis();
        return leavingTime;
    }

    public String getNextStationID(){
        int next = ncurStep + 1;
        if (ncurStep >= lsRouteLine.size() - 1){
            next = lsRouteLine.size() - 1;
        }
        return lsRouteLine.get(next);
    }

    public void setRailOptionCurPos(){
        ncurStep = mRailWayTimeTable.getTimeStationStep(System.currentTimeMillis());

        String strBelongID = lsRouteLine.get(ncurStep);
        BigMapstationInfo info = MyApp.theIns().findBigmapStationByBelongID(strBelongID);
        if (mRailOption != null && info != null){
            mRailOption.setPoint(new Point((int)info.getDotX(),(int)info.getDotY()));
        }
    }

    public String getRailArrTime(String strStationID){
        TimeItemBean ib = mRailWayTimeTable.getArrTime(strStationID);
        if (ib != null)
            return ib.getstrTime();
        return null;
    }

    public long getLeavingTimeFromStationLong(String stationID){
        TimeItemBean ibdest = mRailWayTimeTable.getArrTime(stationID);
        if (ibdest == null)
            return Long.MAX_VALUE;
        long res = ibdest.getMillisTime() - System.currentTimeMillis();
        if (res < 0)
            return Long.MAX_VALUE;
        return res;
    }

    public boolean hasStation(String stationID){
        TimeItemBean ib = mRailWayTimeTable.getArrTime(stationID);
        if (ib != null)
            return true;

        return false;
    }
}
