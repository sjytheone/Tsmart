package com.sjy.utils;

import android.app.Activity;
import android.content.Context;

import com.sjy.bushelper.R;

/**
 * Created by lgp on 2015/6/7.
 */
public class ThemeUtils {

    public static void changeTheme(Activity activity, Theme theme){
        if (activity == null)
            return;
        int style = R.style.BlueTheme;
        switch (theme){
            case BROWN:
                style = R.style.BrownTheme;
                break;
            case BLUE:
                style = R.style.BlueTheme;
                break;
            case BLUE_GREY:
                style = R.style.BlueGreyTheme;
                break;
            case YELLOW:
                style = R.style.YellowTheme;
                break;
            case DEEP_PURPLE:
                style = R.style.DeepPurpleTheme;
                break;
            case PINK:
                style = R.style.PinkTheme;
                break;
            case GREEN:
                style = R.style.GreenTheme;
                break;
            case BLACK:
                style = R.style.DarkTheme;
                break;
            default:
                break;
        }
        activity.setTheme(style);
    }

    public static Theme getCurrentTheme(Context context){
        int value = (int) SPUtils.get(context, context.getResources().getString(R.string.change_theme_key), 0);
        return Theme.mapValueToTheme(value);
    }

    public enum Theme{
        BLUE(0x00),
        BLACK(0x01),
        BROWN(0x02),
        BLUE_GREY(0x03),
        YELLOW(0x04),
        DEEP_PURPLE(0x05),
        PINK(0x06),
        GREEN(0x07);

        private int mValue;

        Theme(int value){
            this.mValue = value;
        }

        public static Theme mapValueToTheme(final int value) {
            for (Theme theme : Theme.values()) {
                if (value == theme.getIntValue()) {
                    return theme;
                }
            }
            // If run here, return default
            return BLUE;
        }

        static Theme getDefault()
        {
            return BLUE;
        }
        public int getIntValue() {
            return mValue;
        }
    }
}
