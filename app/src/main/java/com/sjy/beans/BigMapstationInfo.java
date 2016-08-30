package com.sjy.beans;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/15.
 */
public class BigMapstationInfo implements Serializable{

    final static float FloatBound = 45.0F;
    public String getStationID() {
        return stationID;
    }

    public void setStationID(String stationID) {
        this.stationID = stationID;
    }

    private String stationID;

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    private String stationName;

    public float getDotX() {
        return dotX;
    }

    public void setDotX(float dotX) {
        this.dotX = dotX;
    }

    private float dotX = 0.0f;

    public float getDotY() {
        return dotY;
    }

    public void setDotY(float dotY) {
        this.dotY = dotY;
    }

    private float dotY= 0.0f;

    private boolean bTransmition =false;



    public boolean isPointWithin(int x, int y)
    {
        //return (isPointWithinRectangle(x, y)) || (isPointWithinDot(x, y));
        return isPointWithinDot(x, y);
    }

    public boolean isPointWithinDot(int paramInt1, int paramInt2)
    {
        return (paramInt1 >= this.dotX - FloatBound) && (paramInt1 <= this.dotX + FloatBound) && (paramInt2 >= this.dotY - FloatBound) && (paramInt2 <= this.dotY + FloatBound);
    }

//    public boolean isPointWithinRectangle(int paramInt1, int paramInt2)
//    {
//        return (paramInt1 >= this.btnOrgX) && (paramInt1 <= this.btnOrgX + this.btnWidth) && (paramInt2 >= this.btnOrgY) && (paramInt2 <= this.btnOrgY + this.btnHeight);
//    }

}
