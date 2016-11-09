package com.example.espressage.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.espressage.R;


/**
 * Created by Administrator on 2016/6/29 0029.
 */
public abstract class FailFragment extends Fragment {
    public abstract void show();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fail_fragment,null);
        Button btnRefresh= (Button) view.findViewById(R.id.btn_refresh);
//        btnRefresh.getParent().requestDisallowInterceptTouchEvent(true);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });
        return view;
    }

}
