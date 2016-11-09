package com.example.espressage.fragment;

import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.espressage.*;
import com.example.espressage.Utils.MyDbOpenHelper;
import com.example.espressage.bean.ExpressInfo;
import com.google.gson.Gson;

/**
 * Created by Administrator on 2016/6/29 0029.
 */
public class SusFragment extends Fragment {

    private ListView lvInfo;
    private ExpressInfo expressInfo;
    private TextView tvText;
    private LinearLayout layout;


    public void parseData(String s, String number) {
        Gson gson = new Gson();
        expressInfo = gson.fromJson(s, ExpressInfo.class);

        Cursor cursor = MyDbOpenHelper.dbHelper.rawQuery("select mailNo from expressInfo", null);
        ContentValues values = new ContentValues();
        while (cursor.moveToNext()) {
            String mailNo = cursor.getString(cursor.getColumnIndex("mailNo"));
            if (!TextUtils.isEmpty(mailNo)&&mailNo.equals(expressInfo.showapi_res_body.mailNo)) {
                values.put("status", expressInfo.showapi_res_body.status);
                MyDbOpenHelper.dbHelper.update("expressInfo", values, "mailNo=?", new String[]{expressInfo.showapi_res_body.mailNo});
                saveData(number);
                return;
            }
        }
        values.put("status", expressInfo.showapi_res_body.status);
        MyDbOpenHelper.dbHelper.insert("expressInfo", null, values);
        saveData(number);
    }

    private void saveData(String number) {
        //判断是否已经存储过
        Cursor cursor = MyDbOpenHelper.dbHelper.rawQuery("select mailNo from expressInfo", null);
        while (cursor.moveToNext()) {
            String mailNo = cursor.getString(cursor.getColumnIndex("mailNo"));
            if (expressInfo.showapi_res_body.data != null && expressInfo.showapi_res_body.data.size() > 0) {
                if (mailNo.equals(number)) {
                    ContentValues values = new ContentValues();
                    values.put("content", expressInfo.showapi_res_body.data.get(0).context);
                    MyDbOpenHelper.dbHelper.update("expressInfo", values, "mailNo=?", new String[]{number});
                }
            }

        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sus_fragment, null);
        lvInfo = (ListView) view.findViewById(R.id.lv_info);
        tvText = (TextView) view.findViewById(R.id.tv_noResult);
        layout = (LinearLayout) view.findViewById(R.id.ll_layout);
        lvInfo.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                if (expressInfo.showapi_res_body.data.size()==0) {
                    tvText.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.INVISIBLE);
                    if (expressInfo.showapi_res_body.msg != null) {
                        tvText.setText(expressInfo.showapi_res_body.msg);
                    } else {
                        tvText.setText("啊哦，暂时还没有结果，请稍后再试吧");
                    }
                    return 0;
                }
                return expressInfo.showapi_res_body.data.size();
            }
            @Override
            public ExpressInfo.Info.ChildInfo getItem(int position) {
                return expressInfo.showapi_res_body.data.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
//                Log.e("-------=-=-=-=", "getView:status=进来了 ");

                ViewHolder myHolder;
                if (convertView == null) {
                    convertView = View.inflate(getActivity(), R.layout.info_item, null);
                    myHolder = new ViewHolder();
                    myHolder.tvNews = (TextView) convertView.findViewById(R.id.tv_news);
                    myHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);

                    convertView.setTag(myHolder);
                } else {
                    myHolder = (ViewHolder) convertView.getTag();
                }
                ImageView ivGray = (ImageView) convertView.findViewById(R.id.iv_gray);
                ImageView ivRed = (ImageView) convertView.findViewById(R.id.iv_red);
                TextView duan = (TextView) convertView.findViewById(R.id.view_duan);
                TextView chang = (TextView) convertView.findViewById(R.id.view_chang);
               /* ViewGroup.LayoutParams params = duan.getLayoutParams();
                ViewGroup.LayoutParams params1 = chang.getLayoutParams();*/
                if (expressInfo.showapi_res_body.status == 7||expressInfo.showapi_res_body.status == 1||expressInfo.showapi_res_body.status == -1||expressInfo.showapi_res_body.status == 0) {
                    tvText.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.INVISIBLE);
                    Log.e("-------=-=-=-=", "getView:status==");
                    if (!expressInfo.showapi_res_body.msg.equals("")) {
                        tvText.setText(expressInfo.showapi_res_body.msg);
                    } else {
                        tvText.setText("啊哦，暂时还没有结果，请稍后再试吧");
                    }
                } else {
                    Log.e("-------=-=-=-=", "getView:status不等于=1 ");
                    tvText.setVisibility(View.INVISIBLE);
                    layout.setVisibility(View.VISIBLE);
                    myHolder.tvNews.setText(expressInfo.showapi_res_body.data.get(position).context);
                    myHolder.tv_time.setText(expressInfo.showapi_res_body.data.get(position).time);

                    if (position == 0) {
                        myHolder.tvNews.setTextColor(getResources().getColor(R.color.redd));
                        myHolder.tv_time.setTextColor(getResources().getColor(R.color.redd));
                    } else {
                        myHolder.tvNews.setTextColor(getResources().getColor(R.color.gray));
                        myHolder.tv_time.setTextColor(getResources().getColor(R.color.g));
                    }
                }
                if (position == 0) {
                    duan.setVisibility(View.VISIBLE);
//                    duan.setHeight(convertView.getHeight());
                    chang.setVisibility(View.GONE);
                    ivGray.setVisibility(View.GONE);
                    ivRed.setVisibility(View.VISIBLE);
                    /*switch (myHolder.tvNews.getLineCount()){
                        case 1:
                            break;
                        case 2:
                            params.height=160;
                            duan.setLayoutParams(params);
                            break;
                        case 3:
                            params.height=210;
                            duan.setLayoutParams(params);
                            break;
                    }*/

                } else {
                    duan.setVisibility(View.GONE);
                    chang.setVisibility(View.VISIBLE);
//                    chang.setHeight(convertView.getHeight());
                    ivGray.setVisibility(View.VISIBLE);
                    ivRed.setVisibility(View.GONE);
                    /*switch (myHolder.tvNews.getLineCount()){
                        case 1:
                            break;
                        case 2:
                            params1.height=160;
                            chang.setLayoutParams(params1);
                            break;
                        case 3:
                            params1.height=240;
                            chang.setLayoutParams(params1);
                            break;
                    }*/
                }
                return convertView;
            }
        });
        return view;
    }

    private class ViewHolder {
        private TextView tvNews;
        private TextView tv_time;
    }
}
