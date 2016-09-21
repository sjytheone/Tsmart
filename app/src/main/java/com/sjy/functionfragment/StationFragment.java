package com.sjy.functionfragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.sjy.baseactivity.ShowStationActivity;
import com.sjy.beans.BigMapstationInfo;
import com.sjy.beans.RailWayLineItem;
import com.sjy.beans.RouteItemBean;
import com.sjy.bushelper.MyApp;
import com.sjy.bushelper.R;
import com.sjy.utils.CharacterParser;
import com.sjy.widget.IndexableListView;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class StationFragment extends Fragment {

    public StationFragment() {
        // Required empty public constructor
    }

    private IndexableListView mIndexableListView;
    private List<BigMapstationInfo> mListReal = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_station, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitView(view);
    }

    @Override
    public void onDestroyView() {
        mListReal.clear();
        super.onDestroyView();
    }

    public void InitView(View v){

        mListReal.clear();
        List<RouteItemBean> jsonStations = MyApp.theIns().getJsonStations();
        List<BigMapstationInfo> bigMapstationInfos = MyApp.theIns().getBigMapStationPos_flat();
        mListReal.addAll(bigMapstationInfos);

        //List<RouteLineItemBean> routeLine = MyApp.theIns().getAllRouteLine();
        List<RailWayLineItem> railWayLines = MyApp.theIns().getRailWayLineItems();

//        for (RouteItemBean itembean : mListReal){
//            if (itembean.getStationFragmentDes() == null){
//                String strDesc = "";
//                for (RailWayLineItem railWayLineItem : railWayLines){
//                    if (itembean.getRouteOder(railWayLineItem.getRailWayLineID()) > 0){
//                        strDesc += railWayLineItem.getRailWayLineName() + " ";
//                    }
//                }
//                itembean.setStationFragmentDes(strDesc);
//            }
//        }
        Collections.sort(mListReal, new PinyinComparator());


        mIndexableListView = (IndexableListView) v.findViewById(R.id.fragemt_staion_indexlistview);
        ContentAdapter adapter = new ContentAdapter(getActivity(),mListReal);
        mIndexableListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        mIndexableListView.setFastScrollEnabled(true);
        mIndexableListView.setOnItemClickListener(mItemClickListener);
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BigMapstationInfo ib = (BigMapstationInfo)mListReal.get(position);
            Intent intent = new Intent();
            //intent.setAction("com.sjy.baseactivity.ShowStationActivity");
            intent.setClass(getActivity().getApplicationContext(), ShowStationActivity.class);
            Bundle bd = new Bundle();
            bd.putString("name",ib.getStationName());
            bd.putString("id",ib.getStationID());
            //bd.putString("desc",ib.getStationFragmentDes());

            intent.putExtra("information",bd);
            startActivity(intent);
        }
    };

    private class ContentAdapter extends BaseAdapter implements SectionIndexer {

        class Holder {
            TextView stationName;
            TextView  description;
        }

        private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        private List<BigMapstationInfo> mDatas;
        private Context mContext;
        public ContentAdapter(Context context, List<BigMapstationInfo> objects) {
            mContext = context;
            mDatas = objects;
        }

        @Override
        public int getPositionForSection(int section) {
            // If there is no item for current section, previous section will be selected
            for (int i = section; i >= 0; i--) {
                for (int j = 0; j < getCount(); j++) {
                    BigMapstationInfo ib = (BigMapstationInfo)getItem(j);
                    if (i == 0) {
                        // For numeric section
                        for (int k = 0; k <= 9; k++) {
                            String strout = CharacterParser.getInstance().getSelling((String)ib.getStationName()).toUpperCase();
                           if (strout.charAt(0) == String.valueOf(k).charAt(0))
                                return j;
                        }
                    } else {
                        String strout = CharacterParser.getInstance().getSelling((String)ib.getStationName()).toUpperCase();
                        String t = String.valueOf(mSections.charAt(i));
                        if (strout.charAt(0) == t.charAt(0))
                            return j;
                    }
                }
            }
            return 0;
        }

        @Override
        public int getSectionForPosition(int position) {
            return 0;
        }

        @Override
        public Object[] getSections() {
            String[] sections = new String[mSections.length()];
            for (int i = 0; i < mSections.length(); i++)
                sections[i] = String.valueOf(mSections.charAt(i));
            return sections;
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Holder holder = null;
            if(null == convertView){
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.adapter_stationlist_item,null);

                holder = new Holder();
                holder.stationName = (TextView) convertView.findViewById(R.id._stationlistitem_station_tv);
                convertView.setTag(holder);
            }else{
                holder = (Holder) convertView.getTag();
            }

            BigMapstationInfo itemBean = (BigMapstationInfo) getItem(position);
            holder.stationName.setText(itemBean.getStationName());
            return convertView;
        }
    }

    public class PinyinComparator implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            char c1 = ((BigMapstationInfo) o1).getStationName().charAt(0);
            char c2 = ((BigMapstationInfo) o2).getStationName().charAt(0);
            return concatPinyinStringArray(
                    PinyinHelper.toHanyuPinyinStringArray(c1)).compareTo(
                    concatPinyinStringArray(PinyinHelper
                            .toHanyuPinyinStringArray(c2)));
        }
        private String concatPinyinStringArray(String[] pinyinArray) {
            StringBuffer pinyinSbf = new StringBuffer();
            if ((pinyinArray != null) && (pinyinArray.length > 0)) {
                for (int i = 0; i < pinyinArray.length; i++) {
                    pinyinSbf.append(pinyinArray[i]);
                }
            }
            return pinyinSbf.toString();
        }
    }
}
