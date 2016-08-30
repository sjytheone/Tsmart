package com.sjy.bigimagemap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.sjy.bushelper.MyApp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.sjy.bushelper.MyApp;
import com.sjy.bushelper.R;
/**
 * Created by Administrator on 2016/6/16.
 */
public class BigTileMap extends SubsamplingScaleImageView{


    public enum TileMapMode {NORMAL, GRID};

    private List<BigMapDrawOverlay> mlsRailInfo;
    private List<BigMapFlagDrawOverlay> mlsNaviInfo;
    private GestureDetector mGestureDetector;
    private ITileMapNotify  mTileMapNotify;
    private TileMapMode mTileMode = TileMapMode.NORMAL;
    private boolean mbShowRailOption = true;
    private boolean mhighlightOverlays = false;
    private final static int HIGHLIGHTCOLOR = Color.argb(127,111,111,111);

    public BigTileMap(Context context) {
        super(context);
        mlsRailInfo = new ArrayList<>();
        mlsNaviInfo = new ArrayList<>();
        mGestureDetector = new GestureDetector(getContext(),mSimpleOnGestureListener);
    }

    public BigTileMap(Context context,AttributeSet attributes){
        super(context,attributes);
        mlsRailInfo = new ArrayList<>();
        mlsNaviInfo = new ArrayList<>();
        mGestureDetector = new GestureDetector(getContext(),mSimpleOnGestureListener);
    }

    public void setTileMode(TileMapMode mode){
        mTileMode = mode;
    }

    public void setTileMapNotify(ITileMapNotify notify){
        mTileMapNotify = notify;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isImageLoaded())
            return;

        if (mhighlightOverlays)
            canvas.drawColor(HIGHLIGHTCOLOR);
        //canvas.drawColor(MyApp.theIns().getResources().getColor(R.color.halftransparent));
        if (mTileMode == TileMapMode.NORMAL){
            if (mlsRailInfo != null && mbShowRailOption){
                Iterator<BigMapDrawOverlay> iterator = mlsRailInfo.iterator();
                while (iterator.hasNext()){
                    BigMapDrawOverlay option = iterator.next();
                    option.draw(canvas);
                }
            }
        }
        else if (mTileMode == TileMapMode.GRID){
            if (!mlsNaviInfo.isEmpty()){
                Iterator<BigMapFlagDrawOverlay> iterator = mlsNaviInfo.iterator();
                while (iterator.hasNext()){
                    BigMapFlagDrawOverlay option = iterator.next();
                    option.draw(canvas);
                }
            }
        }

        //canvas.drawColor(0);
    }

    public void addRailOption(BigMapDrawOverlay option){
        if (mlsRailInfo != null){
            mlsRailInfo.add(option);
            option.onAdd2TileMap(this);
            invalidate();
        }
    }

    public void removeRailOption(BigMapDrawOverlay option){
        if (mlsRailInfo != null){
            mlsRailInfo.remove(option);
            option.onRemoveFromTileMap(this);
            invalidate();
        }
    }

    public void removeAllRailOption(){
        if (mlsRailInfo != null){
            mlsRailInfo.clear();
        }
    }


    public void AddNaviOption(BigMapFlagDrawOverlay option){
        mlsNaviInfo.add(option);
        option.onAdd2TileMap(this);
        invalidate();
    }

    public void RemoveNaviOption(BigMapFlagDrawOverlay option){
        mlsNaviInfo.remove(option);
        option.onRemoveFromTileMap(this);
        invalidate();
    }

    public void RemoveAllNaviOptions(){
        mlsNaviInfo.clear();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       mGestureDetector.onTouchEvent(event);
       return super.onTouchEvent(event);
    }

    private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener(){

        public boolean onDoubleTap(MotionEvent paramAnonymousMotionEvent)
        {
            return false;
        }

        public void onLongPress(MotionEvent event)
        {

        }

        public boolean onSingleTapConfirmed(MotionEvent event)
        {
            if (isImageLoaded()){
                if (mTileMode == TileMapMode.NORMAL) {
                    if (mlsRailInfo != null && mbShowRailOption) {
                        Iterator<BigMapDrawOverlay> iterator = mlsRailInfo.iterator();
                        while (iterator.hasNext()) {
                            BigMapDrawOverlay option = iterator.next();
                            PointF fpoint = viewToSourceCoord(event.getX(), event.getY());
                            boolean b = option.withInRect((int) fpoint.x, (int) fpoint.y);
                            if (b) {
                                if (mTileMapNotify != null) {
                                    mTileMapNotify.onOverlayClicked(event, option);
                                }
                                return true;
                            }
                        }
                    }
                }
                if (mTileMapNotify != null){
                    mTileMapNotify.onMapClicked(event);
                }
            }
            return true;
        }
    };

    public void setShowRailOptions(boolean bShow){
        mbShowRailOption = bShow;
        invalidate();
    }

    public boolean isShowRailOptions(){
        return  mbShowRailOption;
    }

    public void setHighlightOverlay(boolean bHighLight){
        mhighlightOverlays = bHighLight;
    }

    public boolean isHighlightOverlay(){
        return mhighlightOverlays;
    }
}
