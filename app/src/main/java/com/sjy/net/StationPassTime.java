package com.sjy.net;

import com.sjy.bushelper.MyApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/5.
 */
public class StationPassTime {
    public String routecode;
    public long costtime;
    public long lastboardingtime;
    public StaPassTime LastTransferStation;
    public List<StaPassTime> StaList = new ArrayList<>();

    public StationPassTime(String routeTemp)
    {
        routecode = routeTemp;
        String[] temp = routeTemp.split("-");
        for (int i = 0; i < temp.length; i++)
        {
            StaPassTime staTemp = new StaPassTime();
            staTemp.StaCode = temp[i].split("\\|")[0];
            staTemp.StaTime =  Long.parseLong(temp[i].split("\\|")[1]);
            StaList.add(staTemp);
        }

        LastTransferStation = StaList.get(StaList.size() - 1);
    }

    public boolean cover()
    {
        boolean temp = false;
        for (int i = 0; i < StaList.size(); i++)
        {
            for (int j = i + 2; j < StaList.size(); j++) {
                if (StaList.get(i).StaCode == StaList.get(j).StaCode) {
                    temp = true;
                    break;
                }
                if(MyApp.TransferStations.containsKey(StaList.get(i).StaCode)) {
                    if( MyApp.TransferStations.get(StaList.get(i).StaCode).contains(StaList.get(j).StaCode)) {
                        temp = true;
                        break;
                    }
                }
            }
            if (temp == true) break;
        }
        return temp;
    }
    public void adjion(String jion)
    {
        for (int i = 0; i < StaList.size() - 1;i++ )
            if(StaList.get(i).ToString().equals(jion))
                costtime = StaList.get(i).StaTime -StaList.get(0).StaTime + StaList.get(StaList.size() - 1).StaTime - StaList.get(i + 1).StaTime + MyApp.transfertime;
        //未找到，末站
        if (costtime == 0)
            costtime = 0;
    }


}
