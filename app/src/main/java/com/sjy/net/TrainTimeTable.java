package com.sjy.net;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/7.
 * 单条运行线路
 */

public class TrainTimeTable {


    public class StaTimeTable
    {
        public String StaCode = ""; //站点ID
        public String Name = "";    //站点名称
        public int ArrivalTime;     //到站时间
        public int DepartureTime;   //离站时间
    }


    public List<StaTimeTable> StaTT = new ArrayList<>();
    public String TrainNo;

    public int StaIndex(String staCode)
    {
        int temp = -1;
        for (int i = 0; i < StaTT.size(); i++)
        {
            if (StaTT.get(i).StaCode == staCode)
            { temp = i; break; }
        }
        return temp;
    }


}
