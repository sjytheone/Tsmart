package com.sjy.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by Administrator on 2016/8/15.
 */
public class AppUtils {

    public static int getVersionCode(Context context){
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
            return pi.versionCode;
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
            return 0;
        }
    }

    public static String getVersionName(Context context){
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
            return pi.versionName;
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
            return "";
        }
    }

}
