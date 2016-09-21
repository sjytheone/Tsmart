package com.sjy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.jakewharton.rxbinding.view.RxView;
import com.sjy.beans.RailItemBean;
import com.sjy.bushelper.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by Administrator on 2016/5/4.
 */
public class RailTimeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private List<RailItemBean> mData;
    private OnAdapterItemClickListener onAdapterItemClickListener;
    public RailTimeRecyclerAdapter(Context context, List<RailItemBean> data ){
        mContext = context;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder Viewholder = null;
        if (viewType == RailItemBean.NORMALITEMBEAN){
            View view = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_railtime_item, parent, false);
            RailTimeHolder holder = new RailTimeHolder(view);
            Viewholder = holder;
        }else if (viewType == RailItemBean.DESCRIBITEMBEAN){

        }

        return Viewholder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RailItemBean item = mData.get(position);
        if (holder instanceof RailTimeHolder){
            RailTimeHolder rholder = (RailTimeHolder)holder;
            rholder.railName.setText(item.getRailID());
            rholder.description.setText(item.getRailDesc());
            ((RailTimeHolder) holder).rippleLayout.setTag(item);
        }else if (holder instanceof  RailTimeTitleHolder){
            RailTimeTitleHolder tholder = (RailTimeTitleHolder)holder;
            tholder.tvRouteName.setText(item.getRailID());

        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getType();
    }

    public class RailTimeHolder extends RecyclerView.ViewHolder{
        TextView railName;
        TextView  description;
        TextView  remaningTime;
        MaterialRippleLayout rippleLayout;

        public RailTimeHolder(View itemView) {
            super(itemView);
            railName = (TextView) itemView.findViewById(R.id._railtime_maintv);
            description = (TextView) itemView.findViewById(R.id._railtime_subtv);
            remaningTime = (TextView)itemView.findViewById(R.id._railtime_remainding);
            rippleLayout = (MaterialRippleLayout) itemView.findViewById(R.id._adpter_railtime_ripple);
            RxView.clicks(rippleLayout).throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
                @Override
                public void call(Void aVoid) {
                    if (onAdapterItemClickListener != null){
                        onAdapterItemClickListener.onAdapterItemClickListener(rippleLayout,getLayoutPosition());
                    }
//                    if (onAdapterItemClickListener != null){
//                        onAdapterItemClickListener.onAdapterItemClickListener(rippleLayout,getLayoutPosition());
//                    }
                }
            });
        }

    }

    public class RailTimeTitleHolder extends RecyclerView.ViewHolder{

        TextView tvRouteName;
        public RailTimeTitleHolder(View itemView) {
            super(itemView);
            //tvRouteName = (TextView) itemView.findViewById(R.id.railtimerecycler_title_name);
        }
    }
    public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener){
        onAdapterItemClickListener = listener;
    }
}
