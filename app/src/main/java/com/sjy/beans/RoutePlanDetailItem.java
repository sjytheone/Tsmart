package com.sjy.beans;

/**
 * Created by Administrator on 2016/8/11.
 */
public class RoutePlanDetailItem {
    String strStationID;
    String strStationName;
    String strArrayTime;
    String strTransDesc;
    boolean bTransport;

    public String getStrStationID() {
        return strStationID;
    }

    public void setStrStationID(String strStationID) {
        this.strStationID = strStationID;
    }

    public String getStrStationName() {
        return strStationName;
    }

    public void setStrStationName(String strStationName) {
        this.strStationName = strStationName;
    }

    public String getStrArrayTime() {
        return strArrayTime;
    }

    public void setStrArrayTime(String strArrayTime) {
        this.strArrayTime = strArrayTime;
    }

    public String getStrTransDesc() {
        return strTransDesc;
    }

    public void setStrTransDesc(String strTransDesc) {
        this.strTransDesc = strTransDesc;
    }

    public boolean isbTransport() {
        return bTransport;
    }

    public void setbTransport(boolean bTransport) {
        this.bTransport = bTransport;
    }
}
