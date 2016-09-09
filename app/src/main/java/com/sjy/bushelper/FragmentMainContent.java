package com.sjy.bushelper;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.sjy.bigimagemap.BigTileMap;
import com.sjy.functionfragment.BDMapRouteFragment;
import com.sjy.functionfragment.BigmapFragment;
import com.sjy.listener.IFragemDataListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/21.
 */
public class FragmentMainContent extends Fragment
        implements View.OnClickListener{

    private List<Fragment> mTabs = new ArrayList<Fragment>();
    private List<IFragemDataListener> mDataListener = new ArrayList<IFragemDataListener>();
    private ImageButton mFunctionBtn;
    private Toolbar mMyToolbar;
    private IToolbarHelper mToolbarHelper;

    private int[] arrSDrawable = new int[]{R.drawable.map_white_72x72,R.drawable.timeline_white_72x72};

    private int mcurIndex = 0;
    private android.os.Handler mHandler = new android.os.Handler(){
        public void handleMessage(Message msg) {
            if (msg.what == 1001){
                ResetIndicate();
                int index = msg.arg1;
                mFunctionBtn.setImageResource(arrSDrawable[index]);
            }
        }
    };

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onStop() {
        super.onStop();
        int x = 0;
    }

    @Override
    public void onPause() {
        super.onPause();
        int x = 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_maincontent,null);
        InitViews(v);
        initDatas();
        switchFragment(0);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void ResetIndicate(){
        mFunctionBtn.setImageResource(arrSDrawable[0]);
    }

    private void initDatas()
    {
        BigmapFragment bigmapFragment = new BigmapFragment();
        Bundle bd = new Bundle();
        bd.putInt("tileMode", BigTileMap.TileMapMode.NORMAL.ordinal());
        bigmapFragment.setArguments(bd);
        mTabs.add(bigmapFragment);

        //BDMapRouteFragment bdRouteFragment = BDMapRouteFragment.newInstance();
        //mTabs.add(bdRouteFragment);

        //mDataListener.add(bdRouteFragment);


        for (int i = 0 ; i < mTabs.size() ; ++i){
            getFragmentManager().beginTransaction().add(R.id._maincontent_framelayout_container, mTabs.get(i)).commit();
        }
    }

    protected void InitViews(View v){

        mMyToolbar = (Toolbar) v.findViewById(R.id.tb_myToolbar);
        mFunctionBtn = (ImageButton) mMyToolbar.findViewById(R.id.imageRoute);
        mFunctionBtn.setOnClickListener(this);


        mMyToolbar.setTitleTextColor(getResources().getColor(R.color.theme_blue));

        if (mToolbarHelper != null)
            mToolbarHelper.OnRestoreToolbar(mMyToolbar);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageRoute : {
                if (mcurIndex == 0) {
                    mcurIndex = 1;
                    switchFragment(1);
                }else
                {
                    mcurIndex = 0;
                    switchFragment(0);
                }
            }
            break;
            default:
                break;
        }
    }

    public void setToolbarHelperListener(IToolbarHelper tbHelper){
        mToolbarHelper = tbHelper;
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

    public void switchFragment(int index){
        if (index >=0 && index < mTabs.size()){
            for (int i = 0 ; i < mTabs.size() ; ++i){
                getFragmentManager().beginTransaction().hide(mTabs.get(i)).commit();
            }
            getFragmentManager().beginTransaction().show(mTabs.get(index)).commit();
            //getFragmentManager().beginTransaction().replace(R.id._maincontent_framelayout_container, mTabs.get(index)).commit();
            String strTitle = getResources().getString(R.string.app_name);
            switch (index){
                case 0 :
                    strTitle = getResources().getString(R.string.route);
                    break;
                case 1 :
                    strTitle = getResources().getString(R.string.map);
                    break;
            }

            mMyToolbar.setTitle(strTitle);
            Message msg = mHandler.obtainMessage(1001);
            msg.arg1 = index;
            mHandler.sendMessage(msg);
        }
    }

    public void showIndicate(boolean bShow){
        mFunctionBtn.setVisibility(bShow ? View.VISIBLE : View.GONE);
    }
}
