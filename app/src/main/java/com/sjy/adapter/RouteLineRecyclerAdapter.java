package com.sjy.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.jakewharton.rxbinding.view.RxView;
import com.sjy.baseactivity.ShowDetailRouteActivity;
import com.sjy.baseactivity.ShowStationActivity;
import com.sjy.beans.RailWayLineItem;
import com.sjy.beans.RouteItemBean;
import com.sjy.bushelper.MyApp;
import com.sjy.bushelper.R;
import com.sjy.divider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by Administrator on 2016/4/29.
 */
public class RouteLineRecyclerAdapter extends RecyclerView.Adapter<RouteLineRecyclerAdapter.RouteLineHolder> {

    final static String TAG = RouteLineRecyclerAdapter.class.getSimpleName();
    public final static int FLAG_RAILWAY = 0;
    public final static int FLAG_TIMETABLE = 1;
    private Context mContext;
    private List<RailWayLineItem> mData;
    private OnAdapterItemClickListener mOnAdapterItemClickListener;
    private int mFlag = FLAG_RAILWAY;
    public RouteLineRecyclerAdapter(Context context, List<RailWayLineItem> data,int nflag) {
        mContext = context;
        mData = data;
        mFlag = nflag;
    }

    @Override
    public RouteLineRecyclerAdapter.RouteLineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vItem = LayoutInflater.from(mContext).inflate(
                R.layout.recycler_rout_item, parent, false);
        RouteLineHolder holder = new RouteLineHolder(vItem);
        return holder;
    }

    @Override
    public void onBindViewHolder(RouteLineHolder holder, int position) {
        //RouteLineItemBean itemBean = mData.get(position);
        RailWayLineItem itemBean = mData.get(position);
        String strMain= String.format("起点:%s->终点:%s",itemBean.getFirstStation(),itemBean.getLastStation());
        holder.routeMainItem.setText(itemBean.getRailWayLineName());
        holder.routeMainItem2.setText(strMain);
        String strSub= String.format("首班车:%s 末班车:%s",itemBean.getFirstRailWayTime(),itemBean.getLastRailWayTime());
        holder.routeSubItem.setText(strSub);
        if (mFlag == FLAG_TIMETABLE){
            Drawable dw = ContextCompat.getDrawable(mContext,R.drawable.ic_alarm_black_24dp);
            holder.imageView.setImageDrawable(dw);
            //holder.imageView.setImageResource(R.drawable.access_alarms_black_54x54);
            //holder.imageView.setImageBitmap();
        }else {

            Drawable dw = ContextCompat.getDrawable(mContext,R.drawable.ic_railway_24dp);
            holder.imageView.setImageDrawable(dw);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class RouteLineHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView routeMainItem;
        TextView routeMainItem2;
        TextView routeSubItem;
        MaterialRippleLayout rippleLayout;

        public RouteLineHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.left_imageview);
            routeMainItem = (TextView) itemView.findViewById(R.id._routeitem_maintv);
            routeSubItem = (TextView) itemView.findViewById(R.id._routeitem_subtv);
            routeMainItem2 = (TextView) itemView.findViewById(R.id._routeitem_maintv2);
            rippleLayout = (MaterialRippleLayout) itemView.findViewById(R.id._adpter_ripple);
            RxView.clicks(rippleLayout).throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
                @Override
                public void call(Void aVoid) {
                    if (mOnAdapterItemClickListener != null) {
                        mOnAdapterItemClickListener.onAdapterItemClickListener(rippleLayout, getLayoutPosition());
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
        }
    }

    public void setOnAdapterItemClickListener(OnAdapterItemClickListener clickListener) {
        mOnAdapterItemClickListener = clickListener;
    }
}
