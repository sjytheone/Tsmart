package com.sjy.bigimagemap;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2016/6/16.
 */
public abstract  class BasicMapOverlay {

    protected abstract void draw(Canvas canvas);

    protected abstract void onAdd2TileMap(BigTileMap tileMap);

    abstract boolean onClick(MotionEvent paramMotionEvent);

    protected abstract void onRemoveFromTileMap(BigTileMap tileMap);
}
