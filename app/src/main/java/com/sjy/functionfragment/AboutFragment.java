package com.sjy.functionfragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sjy.adapter.ColorListAdapter;
import com.sjy.bushelper.Eventenum;
import com.sjy.bushelper.R;
import com.sjy.bushelper.SoftUpdate;
import com.sjy.utils.AppUtils;
import com.sjy.utils.SPUtils;

import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;

public class AboutFragment extends Fragment {

    private WebView mWebView;
    private WebSettings mWebSettings;
    private View mView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mView = view;
        RelativeLayout layout = (RelativeLayout) mView.findViewById(R.id.aboutus_checkupdate_html);
        layout.setClickable(true);
        layout.setOnTouchListener(mOnTouchListener);
        layout.setOnClickListener(mOnClickListener);
        layout = (RelativeLayout) mView.findViewById(R.id.aboutus_changetheme);
        layout.setClickable(true);
        layout.setOnTouchListener(mOnTouchListener);
        layout.setOnClickListener(mOnClickListener);



        TextView tvVersionInfo = (TextView) mView.findViewById(R.id.cm_aboutus_app_info);
        tvVersionInfo.setText("版本: " + AppUtils.getVersionName(getContext()));
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.aboutus_checkupdate_html){
                SoftUpdate.getIns().checkforUpdate(getActivity(),mView);
            }else if (v.getId() == R.id.aboutus_changetheme){
                showChangeThemeDialog();
            }
        }
    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setBackgroundColor(getResources().getColor(R.color.light_grey));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.setBackgroundColor(Color.TRANSPARENT);
            }
            return false;        }
    };


    public void showChangeThemeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("更换主题");
        Integer[] res = new Integer[]{R.drawable.blue_round, R.drawable.black_round, R.drawable.brown_round,
                R.drawable.blue_grey_round, R.drawable.yellow_round, R.drawable.deep_purple_round,
                R.drawable.pink_round, R.drawable.green_round};
        List<Integer> list = Arrays.asList(res);
        ColorListAdapter adapter = new ColorListAdapter(getActivity(), list);
        int value = (int) SPUtils.get(getActivity(), getActivity().getResources().getString(R.string.change_theme_key), 0);
        adapter.setCheckItem(value);
        GridView gridView = (GridView) LayoutInflater.from(getActivity()).inflate(R.layout.colors_panel_layout, null);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setCacheColorHint(0);
        gridView.setAdapter(adapter);
        builder.setView(gridView);
        final AlertDialog dialog = builder.show();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();

                SPUtils.put(getContext(), getContext().getResources().getString(R.string.change_theme_key), position);
                Snackbar.make(getView(),"主题将在下次启动时生效",Snackbar.LENGTH_SHORT).show();
                Message msg = new Message();
                msg.what = Eventenum.EventEn.THEME_CHANGED.getValue();
                //EventBus.getDefault().post(msg);
            }
        });

    }
}
