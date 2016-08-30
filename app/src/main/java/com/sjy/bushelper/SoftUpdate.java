package com.sjy.bushelper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sjy.utils.AppUtils;
import com.sjy.utils.Download;

import java.io.File;

import im.fir.sdk.FIR;
import im.fir.sdk.VersionCheckCallback;

/**
 * Created by Administrator on 2016/8/15.
 */
public class SoftUpdate {

    public static void Initialize(){
        if (mIns == null){
            mIns = new SoftUpdate();
        }
    }

    public static SoftUpdate getIns(){
        return mIns;
    }

    public void checkforUpdate(Context ct, final View snakeView){
        mContext = ct;
        FIR.checkForUpdateInFIR("5b2693777f4bc377b3ef72c90456dc8f" , new VersionCheckCallback() {
            @Override
            public void onSuccess(String versionJson) {
                Log.i("fir","check from fir.im success! " + "\n" + versionJson);
                JSONObject jsonObject = JSON.parseObject(versionJson);
                String strVersion = jsonObject.getString("version");
                String strVersionShort = jsonObject.getString("versionShort");
                String strInstallUrl = jsonObject.getString("installUrl");
                String strChangeLog = jsonObject.getString("changelog");
                int ver = Integer.parseInt(strVersion);
                if (ver > AppUtils.getVersionCode(mContext)){

                    mInstallUrl = strInstallUrl;
                    AlertDialog isExit = new AlertDialog.Builder(mContext).create();
                    isExit.setTitle("检测到新版本");
                    String content = String.format("版本号:%s\r\n更新内容:\r\n%s",strVersionShort,strChangeLog);
                    isExit.setMessage(content);

                    isExit.setButton(AlertDialog.BUTTON_POSITIVE,"下载安装",mDialogClickListener);
                    isExit.setButton(AlertDialog.BUTTON_NEGATIVE, "下次再说", mDialogClickListener);
                    isExit.show();
                }else {
                    mInstallUrl = "";
                    if (snakeView != null){
                        //Toast.makeText(mContext,"已是最新版本!",Toast.LENGTH_SHORT).show();
                        Snackbar.make(snakeView,"已是最新版本!",Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFail(Exception exception) {
                Log.i("fir", "check fir.im fail! " + "\n" + exception.getMessage());
            }

            @Override
            public void onStart() {
                //Toast.makeText(mContext, "正在获取", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                //Toast.makeText(mContext, "获取完成", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static SoftUpdate mIns;
    private Context mContext;
    private String mInstallUrl;
    private int curSize = 0;
    private int totalSize = 0;
    Looper looper = Looper.getMainLooper();
    private Handler handler = new Handler(looper){
        @Override
        public void handleMessage(Message msg) {

            }

    };

    AlertDialog.OnClickListener mDialogClickListener = new AlertDialog.OnClickListener(){

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == AlertDialog.BUTTON_POSITIVE){
                //startDownLoad();

                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(mInstallUrl);
                intent.setData(content_url);
                mContext.startActivity(intent);

            }else if (which == AlertDialog.BUTTON_NEGATIVE){
                dialog.dismiss();
            }
        }
    };


    private void startDownLoad(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                //另起线程执行下载，安卓最新sdk规范，网络操作不能再主线程。
                Download l = new Download(mInstallUrl);
                totalSize = l.getLength();
                //load.setMax(l.getLength());

                /**
                 * 下载文件到sd卡，虚拟设备必须要开始设置sd卡容量
                 * downhandler是Download的内部类，作为回调接口实时显示下载数据
                 */
                int status = l.down2sd("tsmart/", "tsmart.apk", l.new downhandler() {
                    @Override
                    public void setSize(int size) {
                        curSize += size;
                        if (curSize >= totalSize) {
                            Toast.makeText(mContext,"下载完成",Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onfileDownLoad(File file) {
                        Intent install = new Intent();
                        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        install.setAction(android.content.Intent.ACTION_VIEW);
                        install.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
                        mContext.startActivity(install);
                    }
                });
                //log输出
                Log.d("log", Integer.toString(status));

            }
        }).start();

    }

}
