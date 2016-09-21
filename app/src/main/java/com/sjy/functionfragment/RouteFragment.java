package com.sjy.functionfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sjy.adapter.OnAdapterItemClickListener;
import com.sjy.adapter.RouteLineRecyclerAdapter;
import com.sjy.baseactivity.ShowDetailRouteActivity;
import com.sjy.beans.RailWayLineItem;
import com.sjy.bushelper.MyApp;
import com.sjy.bushelper.R;
import com.sjy.divider.HorizontalDividerItemDecoration;

import java.util.List;

public class RouteFragment extends Fragment {

	private RecyclerView mRecyclerView;
	private RouteLineRecyclerAdapter mRecyclerAdapter;
	private List<RailWayLineItem> items;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_route, null);
		mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_route_recyclerView);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).color(ContextCompat.getColor(getContext(),R.color.deep_dark)).size(2).build());
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());

		InitData();
		
		return view;
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		UninitData();
		super.onDestroyView();
	}


	private void InitData(){

		items = MyApp.theIns().getRailWayLineItems();
		mRecyclerAdapter = new RouteLineRecyclerAdapter(getContext(),items,RouteLineRecyclerAdapter.FLAG_RAILWAY);

		mRecyclerAdapter.setOnAdapterItemClickListener(new OnAdapterItemClickListener() {
			@Override
			public void onAdapterItemClickListener(View v, int postion) {
				RailWayLineItem itemBean = items.get(postion);
				Intent intent = new Intent();
				intent.setClass(getContext(),ShowDetailRouteActivity.class);
				Bundle bd = new Bundle();
				String strMain= String.format("起点:%s->终点:%s",itemBean.getFirstStation(),itemBean.getLastStation());
				String strSub= String.format("首班车:%s 末班车:%s",itemBean.getFirstRailWayTime(),itemBean.getLastRailWayTime());
				bd.putString("strRouteID",itemBean.getRailWayLineID());
				bd.putString("strDestination",strMain);
				bd.putString("strTime",strSub);
				intent.putExtra("information",bd);
				startActivity(intent);
			}
		});

		mRecyclerView.setAdapter(mRecyclerAdapter);
		mRecyclerAdapter.notifyDataSetChanged();

	}
	
	private void UninitData(){
		mRecyclerAdapter = null;
	}


}
