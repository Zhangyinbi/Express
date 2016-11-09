package com.example.espressage.base;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.espressage.MainActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * Created by Administrator on 2016/6/27 0027.
 */
public abstract class BasePager {
    public Activity mActivity;
    public BasePager(Activity activity) {
        mActivity=activity;
        mRootView= initViews();
    }
    public View mRootView;
    public View initViews() {
        return null;
    }

    public void initData() {
    }
}
