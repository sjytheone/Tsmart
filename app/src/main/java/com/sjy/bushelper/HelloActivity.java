package com.sjy.bushelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;

import com.baidu.mapapi.SDKInitializer;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.sjy.baseactivity.BasicActivity;
import com.sjy.listener.IActivityStatusListener;

public class HelloActivity extends BasicActivity implements IActivityStatusListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_hello);

		SDKInitializer.initialize(getApplicationContext());
		CircleProgressBar progressBar = (CircleProgressBar) findViewById(R.id.helloactivity_progressBar);
		progressBar.setCircleBackgroundEnabled(true);
		progressBar.setBackgroundColor(getResources().getColor(R.color.theme_blue));
		progressBar.setColorSchemeColors(getResources().getColor(R.color.theme_blue));
		//progressBar.setShowArrow(true);

		MyApp.theIns().setActivityStatusListener(this);
		MyApp.theIns().startInitGlobalData();
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onDataInitFinished() {
		Intent intent = new Intent(HelloActivity.this,
				MainActivity.class);
		startActivity(intent);
		MyApp.theIns().setActivityStatusListener(null);
		HelloActivity.this.finish();
	}
}
