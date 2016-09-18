package com.sjy.baseactivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.TransitionInflater;

import com.sjy.bushelper.R;
import com.sjy.utils.ThemeUtils;

/**
 * Created by Administrator on 2016/8/17.
 */
public class BasicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initTheme();
        setUpAnimations();
        super.onCreate(savedInstanceState);

    }

    private void initTheme() {
        ThemeUtils.Theme currentTheme = ThemeUtils.getCurrentTheme(this);
        ThemeUtils.changeTheme(this, currentTheme);
    }

    public void reload(boolean anim) {
        Intent intent = getIntent();
        if (!anim) {
            overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }
        finish();
        if (!anim) {
            overridePendingTransition(0, 0);
        }
        startActivity(intent);
    }

    protected void setUpAnimations(){
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.fade_activity);
        //Fade fade = TransitionInflater.from(this).inflateTransition(R.transition.fade_activity);
        //getWindow().setEnterTransition(fade);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(fade);
        }
    }
}
