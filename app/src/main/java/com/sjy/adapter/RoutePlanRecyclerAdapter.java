package com.sjy.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sjy.beans.BigMapstationInfo;
import com.sjy.beans.RoutePlanDetailItem;
import com.sjy.bushelper.R;

import java.util.List;

/**
 * Created by Administrator on 2016/8/6.
 */
public class RoutePlanRecyclerAdapter extends RecyclerView.Adapter<RoutePlanRecyclerAdapter.RoutePlanHolder>{


    private OnAdapterItemClickListener mOnAdapterItemClickListener;
    private List<RoutePlanDetailItem> mData;
    private Context mContext;

    public RoutePlanRecyclerAdapter(List<RoutePlanDetailItem> data, Context context){
        mContext = context;
        mData = data;
    }
    public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener){
        mOnAdapterItemClickListener = listener;
    }

    @Override
    public RoutePlanHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View vItem = LayoutInflater.from(mContext).inflate(
                R.layout.adapter_routeplan_item,parent,false);
        RoutePlanHolder holder = new RoutePlanHolder(vItem);
        vItem.setOnClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(RoutePlanHolder holder, int position) {
        RoutePlanDetailItem itemBean = mData.get(position);
        holder.tvStationName.setText(itemBean.getStrStationName());
        holder.tvNeedTime.setText(itemBean.getStrArrayTime());
        holder.itemView.setTag(itemBean);
        //if (itemBean.)


        if(position == 0) {
            holder.ivLeftIcon.setTag("begin");
        }else if(position == mData.size() -1) {
            holder.ivLeftIcon.setTag("end");
        }else{
            holder.ivLeftIcon.setTag("normal");
        }
        String iconTag = (String) holder.ivLeftIcon.getTag();
        if(iconTag == "begin"){
            holder.ivLeftIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.transit_map_travel_start_ic));
        }else if(iconTag == "end"){
            holder.ivLeftIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.tranist_detail_map_travel_end_ic));
        }else {
            holder.ivLeftIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.tranist_detail_map_travel_busstation_ic));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class RoutePlanHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView tvStationName;
        public ImageView ivLeftIcon;
        public TextView tvDescription;
        public TextView tvNeedTime;
        public RoutePlanHolder(View itemView) {
            super(itemView);
            tvStationName = (TextView) itemView.findViewById(R.id._routeplan_stationname);
            ivLeftIcon = (ImageView) itemView.findViewById(R.id._routeplan_left_icon);
            tvDescription = (TextView) itemView.findViewById(R.id._routeplan_desc);
            tvNeedTime = (TextView) itemView.findViewById(R.id._routeplanitem_needtime);
        }


        @Override
        public void onClick(View v) {
            if (mOnAdapterItemClickListener != null){
                mOnAdapterItemClickListener.onAdapterItemClickListener(v,getLayoutPosition());
            }
        }
    }

    public void setBaseData(List<RoutePlanDetailItem> data){
        mData = data;
        notifyDataSetChanged();
    }
}
