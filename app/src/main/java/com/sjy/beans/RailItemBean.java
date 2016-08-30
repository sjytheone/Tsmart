package com.sjy.beans;

/**
 * Created by Administrator on 2016/4/8.
 */
public class RailItemBean {

    public static final int NORMALITEMBEAN = 1;
    public static final int DESCRIBITEMBEAN = 2;

    public String getRailID() {
        return railID;
    }

    public void setRailID(String railID) {
        this.railID = railID;
    }

    public String getRemaningTime() {
        return remaningTime;
    }

    public void setRemaningTime(String remaningTime) {
        this.remaningTime = remaningTime;
    }

    public String getCrowding() {
        return crowding;
    }

    public void setCrowding(String crowding) {
        this.crowding = crowding;
    }

    private String railID; //车次id
    private String remaningTime; //到站时间
    private String crowding; //拥挤度
    private long milisortTimes;

    public long getMilisortTimes() {
        return milisortTimes;
    }

    public void setMilisortTimes(long milisortTimes) {
        this.milisortTimes = milisortTimes;
    }

    public String getRailDesc() {
        return railDesc;
    }

    public void setRailDesc(String railDesc) {
        this.railDesc = railDesc;
    }

    private String railDesc; //描述信息

    public int getType() {
        return nType;
    }

    public void setType(int nType) {
        this.nType = nType;
    }

    private int nType = NORMALITEMBEAN;  //节点类型
}
