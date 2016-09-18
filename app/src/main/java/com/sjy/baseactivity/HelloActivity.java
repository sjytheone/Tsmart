package com.sjy.baseactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

import com.baidu.mapapi.SDKInitializer;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.sjy.bushelper.MainActivity;
import com.sjy.bushelper.MyApp;
import com.sjy.bushelper.R;
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
		progressBar.setBackgroundColor(getResources().getColor(R.color.white));
		progressBar.setColorSchemeColors(getResources().getColor(R.color.white));
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
