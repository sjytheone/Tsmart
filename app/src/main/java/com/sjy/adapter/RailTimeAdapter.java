package com.sjy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sjy.beans.RailItemBean;
import com.sjy.bushelper.R;

import java.util.List;

/**
 * Created by Administrator on 2016/4/8.
 */
public class RailTimeAdapter extends BaseAdapter{

    private List<RailItemBean> mData;
    private Context mContext;

    class Holder {
        TextView railName;
        TextView  description;
        TextView  remaningTime;
    }

    public RailTimeAdapter(List<RailItemBean> Data,Context context){
        super();
        this.mData = Data;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return mData.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = null;
        if(null == convertView){
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_railtime_item,null);

            holder = new Holder();
            holder.railName = (TextView) convertView.findViewById(R.id._railtime_maintv);
            holder.description = (TextView) convertView.findViewById(R.id._railtime_subtv);
            holder.remaningTime = (TextView)convertView.findViewById(R.id._railtime_remainding);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }

        RailItemBean itemBean = (RailItemBean) getItem(position);
        holder.railName.setText(itemBean.getRailID());
        holder.description.setText(itemBean.getRailDesc());
        holder.remaningTime.setText(itemBean.getRemaningTime());

        return convertView;
    }

}
