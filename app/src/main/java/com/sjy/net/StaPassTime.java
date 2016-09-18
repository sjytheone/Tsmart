package com.sjy.net;

/**
 * Created by Administrator on 2016/9/5.
 */
public class StaPassTime {
    public String StaCode;
    public long StaTime;

    public String ToString()
    {
        return StaCode + "|" + Long.toString(StaTime);
    }
}
