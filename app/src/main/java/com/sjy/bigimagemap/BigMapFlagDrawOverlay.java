package com.sjy.bigimagemap;

import com.sjy.beans.BigMapstationInfo;

import java.util.Objects;

/**
 * Created by Administrator on 2016/7/31.
 */
public class BigMapFlagDrawOverlay extends BigMapDrawOverlay{

    public static final int NORMAL = 1;
    public static final int ORIGINATION = 2;
    public static final int TEMINATION = 3;
    public static final int TRANSPORT = 4;
    private BigMapstationInfo mStationInfo;

    public String getStationID() {
        return stationID;
    }

    public void setStationID(String stationID) {
        this.stationID = stationID;
    }

    private String stationID;

    public int getFlag() {
        return nFlag;
    }

    public void setFlag(int nFlag) {
        this.nFlag = nFlag;
    }

    private int nFlag = NORMAL;

    public void setInfo(BigMapstationInfo obj){
        mStationInfo = obj;
    }

    public BigMapstationInfo getInfo(){
        return  mStationInfo;
    }


}
