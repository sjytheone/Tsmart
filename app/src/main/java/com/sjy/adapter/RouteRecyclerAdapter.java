package com.sjy.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.jakewharton.rxbinding.view.RxView;
import com.sjy.beans.RouteItemBean;
import com.sjy.bushelper.R;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by Administrator on 2016/4/29.
 */
public class RouteRecyclerAdapter extends RecyclerView.Adapter<RouteRecyclerAdapter.RouteHolder>{

    public static final int ROUTE_FRAGMENT = 1;
    public static final int STATION_FRAGMENT = 2;
    public static final int RAILTIMETABLE_ACTIVITY = 3;

    private Context mContext;
    private int mType;
    private List<RouteItemBean> mData;
    private boolean mbHighLight = true;
    private OnAdapterItemClickListener mOnAdapterItemClickListener;

    public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener){
        mOnAdapterItemClickListener = listener;
    }
    public RouteRecyclerAdapter(List<RouteItemBean> data,Context context,int type){
        mContext = context;
        mType = type;
        mData = data;
    }
    @Override
    public RouteHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View vItem = LayoutInflater.from(mContext).inflate(
                R.layout.adapter_routelist_item,parent,false);
        RouteHolder holder = new RouteHolder(vItem);
        return holder;
    }

    @Override
    public void onBindViewHolder(RouteHolder holder, int position) {
        RouteItemBean itemBean = mData.get(position);
        holder.stationName.setText(itemBean.getStrStationName());
        holder.itemView.setTag(itemBean);

        if (mType == ROUTE_FRAGMENT)
            holder.description.setText(itemBean.getStrStationDesc());
        else if (mType == STATION_FRAGMENT){
            holder.description.setText(itemBean.getStationFragmentDes());
        }else if (mType == RAILTIMETABLE_ACTIVITY){
            holder.arrtime.setText(itemBean.getStrArrayTime());
            //holder.stationName.setText(itemBean.getStrStationName() + ":" + itemBean.getStrArrayTime());
            //holder.description.setText("到站时间:" + itemBean.getStrArrayTime());
        }

        if(position == 0) {
            holder.leftIcon.setTag("begin");
            holder.tvItemIndex.setText("");
        }else if(position == mData.size() -1) {
            holder.leftIcon.setTag("end");
            holder.tvItemIndex.setText("");
        }else{
            holder.tvItemIndex.setText(String.valueOf(position));
            holder.leftIcon.setTag("normal");
        }


        String iconTag = (String) holder.leftIcon.getTag();
        if (mbHighLight){
            if(iconTag == "begin"){
                holder.leftIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.transit_map_travel_start_ic));
            }else if(iconTag == "end"){
                holder.leftIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.tranist_detail_map_travel_end_ic));
            }else {
                holder.leftIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.panorama_fisheye_black_54x54));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public Object getItem(int position){
        return  mData.get(position);
    }

    public class RouteHolder extends RecyclerView.ViewHolder{
        ImageView leftIcon;
        TextView stationName;
        TextView  description;
        LinearLayout layoutComming;
        TextView   arrtime;
        TextView tvItemIndex;
        MaterialRippleLayout rippleLayout;
        public RouteHolder(View itemView) {
            super(itemView);

            leftIcon = (ImageView) itemView.findViewById(R.id._routeadapter_left_Icon);
            stationName = (TextView) itemView.findViewById(R.id._routeadapter_station_tv);
            description = (TextView) itemView.findViewById(R.id._routeadapter_descrip_tv);
            layoutComming = (LinearLayout) itemView.findViewById(R.id._comming_layout);
            arrtime = (TextView) itemView.findViewById(R.id._routeadapter_arrtime);
            tvItemIndex = (TextView) itemView.findViewById(R.id._routeadapter_routeindex);
            rippleLayout = (MaterialRippleLayout) itemView.findViewById(R.id._recycle_adpter_ripple);
            RxView.clicks(rippleLayout).throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
                @Override
                public void call(Void aVoid) {
                    if (mOnAdapterItemClickListener != null){
                        mOnAdapterItemClickListener.onAdapterItemClickListener(rippleLayout,getLayoutPosition());
                    }
//                    if (onAdapterItemClickListener != null){
//                        onAdapterItemClickListener.onAdapterItemClickListener(rippleLayout,getLayoutPosition());
//                    }
                }
            });
        }
    }

    public void setAdapterBase(List<RouteItemBean> Data){
        mData = Data;
    }
}
