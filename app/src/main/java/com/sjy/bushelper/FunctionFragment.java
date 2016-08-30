package com.sjy.bushelper;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sjy.bigimagemap.BigTileMap;
import com.sjy.functionfragment.AboutFragment;
import com.sjy.functionfragment.BigmapFragment;
import com.sjy.listener.IFragemDataListener;
import com.sjy.functionfragment.RouteFragment;
import com.sjy.functionfragment.StationFragment;
import com.sjy.functionfragment.TimeTableFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/16.
 */
public class FunctionFragment extends Fragment{
    private Toolbar mMyToolBar;
    private IToolbarHelper mToolbarHelper;
    private int mCurIndex = 1;
    private Map<Integer,Fragment> mFragmentmap = new HashMap<>();
    private Map<Integer,IFragemDataListener> mFragmentListener = new HashMap<>();
    private List<IFragemDataListener> mDataListener = new ArrayList<IFragemDataListener>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_displayfunction,null);
        InitViews(v);
        return v;
    }

    protected void InitViews(View v){
        mMyToolBar = (Toolbar)v.findViewById(R.id.tb_functiontoolbar);
        mMyToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        if (mToolbarHelper != null)
            mToolbarHelper.OnRestoreToolbar(mMyToolBar);

        mHandler.post(mRunable);

        //getFragmentManager().beginTransaction().replace(R.id.id_fragmentcontainer,mFragmentmap.get(1)).commit();
    }

    public void setToolbarHelperListener(IToolbarHelper tbHelper){
        mToolbarHelper = tbHelper;
    }


    private Handler mHandler = new Handler();
    private Runnable mRunable = new Runnable() {
        @Override
        public void run() {
            RouteFragment route = new RouteFragment();
            mFragmentmap.put(1, route);

            mFragmentmap.put(2, new StationFragment());

            BigmapFragment tileMapFragment = new BigmapFragment();
            Bundle bd = new Bundle();
            bd.putInt("tileMode", BigTileMap.TileMapMode.GRID.ordinal());
            tileMapFragment.setArguments(bd);
            mFragmentmap.put(3, tileMapFragment);
            mDataListener.add(tileMapFragment);

            TimeTableFragment timeTable = new TimeTableFragment();
            mFragmentmap.put(4, timeTable);
            mDataListener.add(timeTable);

            mFragmentmap.put(5, new AboutFragment());

            for (Map.Entry<Integer,Fragment> entry : mFragmentmap.entrySet()){
                getFragmentManager().beginTransaction().add(R.id.id_fragmentcontainer,entry.getValue()).commit();
            }

            switchChildFragments(1);

        }
    };

    @Override
    public void onDestroyView() {
        mHandler.removeCallbacks(mRunable);
        super.onDestroyView();
    }

    public boolean doBackPress(){

        boolean res = false;
        for (IFragemDataListener listener : mDataListener){
            res = listener.doBackPress();
            if (res)
                return true;
        }
        return false;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    public void switchChildFragments(int index){

        mCurIndex = index;
        for (Map.Entry<Integer,Fragment> entry : mFragmentmap.entrySet()){
            if (index == entry.getKey()){
                getFragmentManager().beginTransaction().show(entry.getValue()).commitNow();
            }else {
                getFragmentManager().beginTransaction().hide(entry.getValue()).commitNow();
            }
        }

        String[] cotext = getResources().getStringArray(R.array.navigation_content);
        mMyToolBar.setTitle(cotext[mCurIndex]);
        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mCurIndex == 3)
            inflater.inflate(R.menu.routemenu,menu);
        else
            menu.clear();

    }
}
