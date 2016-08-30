package com.sjy.baseactivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sjy.bushelper.R;

/**
 * Created by Administrator on 2016/8/29.
 */
public class SearchActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        InitView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    void InitView(){
//        Toolbar tb = (Toolbar)findViewById(R.id.tb_functiontoolbar);
//        tb.setTitleTextColor(getResources().getColor(R.color.theme_white));
//        setSupportActionBar(tb);
//        getSupportActionBar().setDisplayShowCustomEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        tb.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        getSupportActionBar().setDisplayShowCustomEnabled(true);
//        getSupportActionBar().setTitle("");

        //mSearchView = (MaterialSearchView) findViewById(R.id.search_view);

    }

}
