package com.sjy.bushelper;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.SDKInitializer;
import com.sjy.baseactivity.BasicActivity;
import com.sjy.utils.AppUtils;

import de.greenrobot.event.EventBus;
import im.fir.sdk.FIR;
import im.fir.sdk.VersionCheckCallback;

public class MainActivity extends BasicActivity
	implements OnClickListener,IToolbarHelper {

	//members
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private NavigationFragment	mMyNavigationFragment;
	private FragmentMainContent mFragmentMainContent;
	private FunctionFragment	mFragmentFunction;
	private int nCurShow = 0;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		//SDKInitializer.initialize(getApplicationContext());

		setContentView(R.layout.activity_main);
		initView();
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        EventBus.getDefault().register(this);

		SoftUpdate.getIns().checkforUpdate(this,null);
    }

    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	EventBus.getDefault().unregister(this);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	private Handler mHandler = new Handler();
	//member function
	private void initView()
	{
		mFragmentMainContent = new FragmentMainContent();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.main_content, mFragmentMainContent).commit();

		mFragmentMainContent.setToolbarHelperListener(this);

		mFragmentFunction = new FunctionFragment();
		getSupportFragmentManager().beginTransaction()
				.add(R.id.main_content, mFragmentFunction).hide(mFragmentFunction).commit();

		mFragmentFunction.setToolbarHelperListener(this);

		mMyNavigationFragment = new NavigationFragment();
		getSupportFragmentManager().beginTransaction().
				add(R.id.navigation_content, mMyNavigationFragment).commit();


		mDrawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
		mDrawerLayout.openDrawer(Gravity.LEFT | Gravity.BOTTOM);

		//mDrawerLayout.setOnDragListener();
	}

	public void onEventMainThread(Message msg) {
		if(msg == null)
			return;

		if(msg.what == Eventenum.EventEn.ROUTE_SHOWORHIDETOOLBAR.getValue()){

		}
		else if (msg.what == Eventenum.EventEn.SHOW_FUNCTIONDISPLAYFRAGMENT.getValue()){
			//mHandler.sendMessage(msg);
			int pos = msg.arg1;
			if (pos == 0) {
				if (!mFragmentMainContent.isVisible()) {
					getSupportFragmentManager().beginTransaction()
							.hide(mFragmentFunction).show(mFragmentMainContent).commit();
				}
				mFragmentMainContent.switchFragment(0);
			}else {
//			}else if (pos == 3){
//				if (!mFragmentMainContent.isVisible()){
//					getSupportFragmentManager().beginTransaction()
//							.hide(mFragmentFunction).show(mFragmentMainContent).commit();
//				}
//				mFragmentMainContent.showIndicate(false);
//				mFragmentMainContent.switchFragment(1);
//			}else{
				if (mFragmentMainContent.isVisible()){
					getSupportFragmentManager().beginTransaction().hide(mFragmentMainContent).commit();
				}
				getSupportFragmentManager().beginTransaction().show(mFragmentFunction).commit();
				mFragmentFunction.switchChildFragments(pos);
			}
			mDrawerLayout.closeDrawer(Gravity.LEFT);
		}
		else if (msg.what == Eventenum.EventEn.THEME_CHANGED.getValue()){
			reload(false);
		}
	}

	@Override
	public void onBackPressed() {
		//super.onBackPressed();

		if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
			mDrawerLayout.closeDrawer(Gravity.LEFT);
			return;
		}

		if (mFragmentMainContent.isVisible() && mFragmentMainContent.doBackPress()){
			return;
		}
		if (mFragmentFunction.isVisible() && mFragmentFunction.doBackPress()){
			return;
		}
		AlertDialog isExit = new AlertDialog.Builder(this).create();

		// 设置对话框标题
		isExit.setTitle("系统提示");
		// 设置对话框消息
		isExit.setMessage("确定要退出吗");
		// 添加选择按钮并注册监听
		isExit.setButton(AlertDialog.BUTTON_POSITIVE,"确定",mDialogClickListener);
		isExit.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", mDialogClickListener);
		// 显示对话框
		isExit.show();
	}


	AlertDialog.OnClickListener mDialogClickListener = new AlertDialog.OnClickListener(){

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == AlertDialog.BUTTON_POSITIVE){
				//finish();
				System.exit(0);
			}else if (which == AlertDialog.BUTTON_NEGATIVE){

			}
		}
	};

	@Override
	public void OnRestoreToolbar(Toolbar tb) {

		setSupportActionBar(tb);
		tb.setTitleTextColor(getResources().getColor(R.color.theme_white));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowCustomEnabled(true);

		//mMyToolbar.setOnMenuItemClickListener(mMenuItemListener);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, tb, R.string.hello_world, R.string.app_name) {
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);

			}
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
			}
		};

		mDrawerToggle.syncState();
		//mDrawerToggle.
		mDrawerLayout.addDrawerListener(mDrawerToggle);
		//mDrawerLayout.openDrawer();
	}

}
