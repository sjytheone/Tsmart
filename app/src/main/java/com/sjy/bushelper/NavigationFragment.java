package com.sjy.bushelper;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sjy.adapter.ColorListAdapter;
import com.sjy.utils.SPUtils;
import com.sjy.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.zip.Inflater;

import de.greenrobot.event.EventBus;

public class NavigationFragment extends Fragment {
	private ListView mListview;
	private List<NavigationBean> mNavData = new ArrayList<>();

	private int mCurSelect = 0;
	private NavigationAdapter	mNavigatoinAdapter;

	public class NavigationBean{
		public Drawable drawable;
		public String strNavigationName;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_navigation, null);

		mListview = (ListView) v.findViewById(R.id.list_navi);
		//mPlanetTitles = new String[]{"d","a","b","c"};

		NavigationBean ib = new NavigationBean();
		ib.strNavigationName = "主页";
		//Typeface iconfont = Typeface.createFromAsset(getActivity().getAssets(), "iconfont/iconfont.ttf");
		ib.drawable = ContextCompat.getDrawable(getContext(),R.drawable.ic_map_black_24dp);
		mNavData.add(ib);

		ib = new NavigationBean();
		ib.strNavigationName = "线路";
		ib.drawable = ContextCompat.getDrawable(getContext(),R.drawable.ic_railway_24dp);
		mNavData.add(ib);

		ib = new NavigationBean();
		ib.strNavigationName = "车站";
		ib.drawable = ContextCompat.getDrawable(getContext(),R.drawable.ic_stations_24dp);
		mNavData.add(ib);

		ib = new NavigationBean();
		ib.strNavigationName = "路径";
		ib.drawable = ContextCompat.getDrawable(getContext(),R.drawable.ic_route_24dp);
		mNavData.add(ib);

		ib = new NavigationBean();
		ib.strNavigationName = "时刻表";
		ib.drawable = ContextCompat.getDrawable(getContext(),R.drawable.ic_access_time_24dp);
		mNavData.add(ib);

		ib = new NavigationBean();
		ib.strNavigationName = "关于";
		ib.drawable = ContextCompat.getDrawable(getContext(),R.drawable.ic_about);

		mNavData.add(ib);


		mNavigatoinAdapter = new NavigationAdapter(getContext(),mNavData);
		mListview.setAdapter(mNavigatoinAdapter);

		mListview.setSelected(true);
		mListview.setOnItemClickListener(new DrawerItemClickListener());
		mNavigatoinAdapter.notifyDataSetChanged();
		return v;
	}
	
	private class DrawerItemClickListener implements  
    ListView.OnItemClickListener {  
		@Override  
		public void onItemClick(AdapterView<?> parent, View view, int position,  
		        long id) {
			mCurSelect = position;
			mNavigatoinAdapter.notifyDataSetChanged();

			Message msg = new Message();
			msg.what = Eventenum.EventEn.SHOW_FUNCTIONDISPLAYFRAGMENT.getValue();
			msg.arg1 = position;
			EventBus.getDefault().post(msg);

		}  
	}

	private class NavigationAdapter extends BaseAdapter{

		class Holder {
			TextView navigation_name;
			ImageView imageview;
		}

		private Context mContext;
		private List<NavigationBean> mData;
		private int itemtextcolor = Color.BLACK;
		private int itemSelectedtextcolor;
		NavigationAdapter(Context context,List<NavigationBean> ls){
			this.mContext = context;
			this.mData = ls;
			TypedValue typedValue = new  TypedValue();
			mContext.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
			itemSelectedtextcolor = typedValue.data;

			ThemeUtils.Theme currentTheme = ThemeUtils.getCurrentTheme(mContext);
			if (currentTheme.getIntValue() == ThemeUtils.Theme.BLACK.getIntValue()){
				itemtextcolor = ContextCompat.getColor(mContext,R.color.text_color);
			}
		}
		@Override
		public int getCount() {
			return mData== null? 0 : mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (null == convertView){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.navigation_adapteritem,null);
				holder = new Holder();
				holder.navigation_name = (TextView)convertView.findViewById(R.id._navigation_name);
				holder.imageview = (ImageView) convertView.findViewById(R.id.navigation_left_Icon);
				convertView.setTag(holder);
			}
			else {
				holder = (Holder) convertView.getTag();
			}
			NavigationBean nib = (NavigationBean) getItem(position);
			holder.navigation_name.setText(nib.strNavigationName);
			holder.imageview.setImageDrawable(nib.drawable);
			holder.navigation_name.setTextColor(itemtextcolor);
			//tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			//holder.navigation_name.setBackgroundColor(Color.TRANSPARENT);
			if (position == mCurSelect){
				holder.navigation_name.setTextColor(itemSelectedtextcolor);
				//holder.navigation_name.setBackgroundColor(ContextCompat.getColor(mContext,R.color.line_color));
			}

			return convertView;
		}
	}

	public void onEventMainThread(Message msg){
		if (msg == null)
			return;

	}
}
