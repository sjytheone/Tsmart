package com.sjy.bigimagemap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/16.
 */
public class BigMapDrawOverlay extends BasicMapOverlay{

    private BigTileMap mTileMap;
    private Bitmap mBackGroundDrawable; //底层
    private Bitmap mBitmapDirection;
    private Bitmap mBitmapRail;
    private Paint paint;
    private boolean isVisible = true;
    private Point mPoint;
    private Rect screenRect;
    private Rect touchRect;
    private boolean mOnlyShowBackGround = true;
    private Bundle mInfo = new Bundle();

    public BigMapDrawOverlay(){
        paint = new Paint(2);
        paint.setColor(Color.YELLOW);
    }

    public void setOnlyShowBackGround(boolean bshow){
        mOnlyShowBackGround = bshow;
    }

    public void setVisible(boolean bVisible){
        isVisible = bVisible;
    }

    public void setPoint(Point pt){
        mPoint = pt;
    }

    @Override
    protected void draw(Canvas canvas) {

        if (isVisible){
            //canvas.drawRect(new Rect(500,500,1000,1000),paint);
            //canvas.drawBitmap(getDrawable(), new Rect(1000,100,100,100), getScreenRect(), this.paint);
            //canvas.drawColor(MyApp.theIns().getResources().getColor(R.color.halftransparent));
            //canvas.drawBitmap();
            Rect rcBackground = getBackGroundDrawableRect();
                //canvas.drawBitmap(getDrawable(),rc.left,rc.top,paint);
            canvas.drawBitmap(getBackGroundDrawable(), null, rcBackground, paint);
            if (!mOnlyShowBackGround){
                if (mBitmapRail != null){
                    //Rect rcRail = getRailDrawableRect();
                    canvas.drawBitmap(mBitmapRail,null,rcBackground,paint);
                }
                if (mBitmapDirection != null){
                    canvas.drawBitmap(mBitmapDirection,null,rcBackground,paint);
                }
            }
        }else {
            //localTCTileMapOverlayItem.getScreenRect(this.tcTileMap, false);
        }

    }

    @Override
    protected void onAdd2TileMap(BigTileMap tileMap) {
        mTileMap = tileMap;
    }

    @Override
    boolean onClick(MotionEvent paramMotionEvent) {
        return false;
    }

    @Override
    protected void onRemoveFromTileMap(BigTileMap tileMap) {
        mTileMap = null;
    }

    public void setBackGroundDrawable(Bitmap bitmap){ mBackGroundDrawable = bitmap;}

    public void setBitmapDirection(Bitmap bitmap){ mBitmapDirection = bitmap;}

    public void setmBitmapRail(Bitmap bitmap){mBitmapRail = bitmap;}


    public Bitmap getBackGroundDrawable(){
        return mBackGroundDrawable;
    }

    Rect getRailDrawableRect(){
        PointF localPoint = mTileMap.sourceToViewCoord(mPoint.x,mPoint.y);
        float fscale = mTileMap.getScale();
        if(fscale < 1.0f) {
            //fscale = 1.0f;
        }
        //localPoint.x += getOffsetX();
        //localPoint.y += getOffsetY();
        Rect rc = new Rect();

        Bitmap localBitmap = mBitmapRail;
        if (localBitmap != null) {
            float bitMapWidth = localBitmap.getWidth() * fscale;
            float bitMapHeight = localBitmap.getHeight() * fscale;
            rc.left = (int) (localPoint.x - bitMapWidth / 2);
            rc.top = (int) (localPoint.y - bitMapHeight / 2);
            rc.right = (rc.left + (int) bitMapWidth);
            rc.bottom = (rc.top + (int) bitMapHeight);
        }
        return rc;

    }

    Rect getBackGroundDrawableRect()
    {
        PointF localPoint = mTileMap.sourceToViewCoord(mPoint.x,mPoint.y);
        float fscale = mTileMap.getScale();
        if(fscale < 1.0f) {
            //fscale = 1.0f;
         }
            //localPoint.x += getOffsetX();
            //localPoint.y += getOffsetY();
            if (this.screenRect == null)
                this.screenRect = new Rect();

            if (touchRect == null)
                touchRect = new Rect();

            //Bitmap localBitmap = getBackGroundDrawable();
            Bitmap localBitmap = getBackGroundDrawable();
            if (localBitmap != null) {
                //screenRect.left = (int)localPoint.x;
                //screenRect.top = (int)localPoint.y;
                float bitMapWidth = localBitmap.getWidth() * fscale;
                float bitMapHeight = localBitmap.getHeight() * fscale;
                screenRect.left = (int) (localPoint.x - bitMapWidth / 2);
                screenRect.top = (int) (localPoint.y - bitMapHeight / 2);
                this.screenRect.right = (screenRect.left + (int) bitMapWidth);
                this.screenRect.bottom = (screenRect.top + (int) bitMapHeight);


                touchRect.left = (int) (mPoint.x - (int) (bitMapWidth / 2));
                touchRect.top = (int) (mPoint.y - (int) (bitMapHeight / 2));
                touchRect.right = (touchRect.left + (int) bitMapWidth);
                touchRect.bottom = (touchRect.top + (int) bitMapHeight);
            }
            return this.screenRect;
    }

    public void PutExtraInfo(String key,Serializable info){
        mInfo.putSerializable(key,info);
    }

    public Serializable GetExtarInfo(String key){
        return mInfo.getSerializable(key);
    }

    public boolean withInRect(int posX,int posY){
        if (touchRect != null){
            return touchRect.contains(posX,posY);
        }
        return  false;
    }
}
