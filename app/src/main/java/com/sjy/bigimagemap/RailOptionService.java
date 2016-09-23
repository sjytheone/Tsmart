package com.sjy.bigimagemap;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2016/9/23.
 */

public class RailOptionService extends Service{

    public class LocalBinder extends Binder {

    }

    @Nullable

    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }
}
