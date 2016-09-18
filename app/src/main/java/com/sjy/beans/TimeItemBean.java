package com.sjy.beans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/7/21.
 */
public class TimeItemBean {

    public TimeItemBean(String strTime) {
        //mstrTime = strTime;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //sdf.set2DigitYearStart(curdate);
        Date df = null;
        try {
            df = sdf.parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mMillisTime = df.getTime();
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        Date df2 = new Date(mMillisTime);
        mstrTime = sdf2.format(df2);
    }
    public TimeItemBean(long millisTime){
        mMillisTime = millisTime;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date df = new Date(millisTime);
        mstrTime = sdf.format(df);
    }

    public String getstrTime() {
        return mstrTime;
    }

    public long getMillisTime() {
        return mMillisTime;
    }

    private String mstrTime;
    private long mMillisTime;

}
