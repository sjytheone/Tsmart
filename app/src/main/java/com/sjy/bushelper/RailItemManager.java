package com.sjy.bushelper;

import android.os.Handler;

import com.sjy.beans.RailOption;
import com.sjy.listener.IRailItemMoveListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2016/5/18.
 */
public class RailItemManager {
    private IRailItemMoveListener moveListener;
    private List<RailOption>      mlsRailInfo = new ArrayList<>();
    private Set<RailOption> msetHashSet = new HashSet<>();

    public RailItemManager() {
        mHandler.postDelayed(mRunableRefreshMarker,1000);
    }

    public void setRailItemMoveListener(IRailItemMoveListener listener){
        moveListener = listener;
    }

    public void setRailInfo(List<RailOption> lsRailInfo){
        mlsRailInfo = lsRailInfo;
    }

    private Handler mHandler = new Handler();

    private Runnable mRunableRefreshMarker = new Runnable() {
        @Override
        public void run() {

            setMovable();
            boolean bNeedUpdata = processRailItemPosition();
            if(bNeedUpdata && moveListener != null){
                moveListener.onRailItemMoved();
            }

            mHandler.postDelayed(mRunableRefreshMarker, 8* 1000);
        }
    };


    protected boolean processRailItemPosition(){
        for (RailOption option : mlsRailInfo){
            if (option.canMove())
                option.stepForward();
        }
        return true;
    }

    public void restoreRailItemStep(){
        for (RailOption option : mlsRailInfo){
            option.restoreStep();
        }
    }

    public void setRandomRailItemStep(){
        for (RailOption option : mlsRailInfo){
            option.setCanMove(true);
            option.initRandomPos();
        }
    }

    public void setMovable(){
        boolean b1 = false,b2 =false,b3 =false,b5 =false;
        for (RailOption option : mlsRailInfo){
            if (option.getRouteID().contains("route1")){
                if (!option.canMove() && !b1){
                    b1 = true;
                    option.setCanMove(true);
                    option.initRandomPos();
                }
            }
            if (option.getRouteID().contains("route2")){
                if (!option.canMove() && !b2){
                    b2 = true;
                    option.setCanMove(true);
                    option.initRandomPos();
                }
            }
            if (option.getRouteID().contains("route3")){
                if (!option.canMove() && !b3){
                    b3 = true;
                    option.setCanMove(true);
                    option.initRandomPos();
                }
            }
            if (option.getRouteID().contains("route5")){
                if (!option.canMove() && !b5){
                    b5 = true;
                    option.setCanMove(true);
                    option.initRandomPos();
                }
            }
        }
    }
}
