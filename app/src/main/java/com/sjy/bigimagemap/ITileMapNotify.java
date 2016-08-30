package com.sjy.bigimagemap;

import android.view.MotionEvent;

/**
 * Created by sjy on 2016/7/9 0009.
 */
public interface ITileMapNotify {

    void onMapClicked(MotionEvent event);
    void onOverlayClicked(MotionEvent event, BigMapDrawOverlay overlay);
}
