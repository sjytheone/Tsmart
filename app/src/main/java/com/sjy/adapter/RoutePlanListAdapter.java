package com.sjy.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sjy.beans.RoutePlanDetailItem;
import com.sjy.bushelper.R;

import java.util.List;

/**
 * Created by Administrator on 2016/9/10 0010.
 */
public class RoutePlanListAdapter extends BaseAdapter{

    private List<RoutePlanDetailItem> mData;
    private Context mContext;

    public RoutePlanListAdapter(Context context,List<RoutePlanDetailItem> ls){
        mContext = context;
        mData = ls;
    }
    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
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
        Holder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_routeplan_item,parent,false);
            holder = new Holder();
            holder.tvStationName = (TextView) convertView.findViewById(R.id._routeplan_stationname);
            holder.ivLeftIcon = (ImageView) convertView.findViewById(R.id._routeplan_left_icon);
            holder.tvDescription = (TextView) convertView.findViewById(R.id._routeplan_desc);
            holder.tvNeedTime = (TextView) convertView.findViewById(R.id._routeplanitem_needtime);

            convertView.setTag(holder);
        }else{
            holder = (Holder)convertView.getTag();
        }
        RoutePlanDetailItem itemBean = mData.get(position);
        holder.tvStationName.setText(itemBean.getStrStationName());
        holder.tvNeedTime.setText(itemBean.getStrArrayTime());

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

        return convertView;
    }

    static class Holder {
        public TextView tvStationName;
        public ImageView ivLeftIcon;
        public TextView tvDescription;
        public TextView tvNeedTime;
    }
}
