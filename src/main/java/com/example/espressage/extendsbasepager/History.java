package com.example.espressage.extendsbasepager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.espressage.QuearyResult;
import com.example.espressage.R;
import com.example.espressage.Utils.MyDbOpenHelper;
import com.example.espressage.base.BasePager;
import com.example.espressage.bean.HistoryInfo;
import com.example.espressage.view.SwipeMenu;
import com.example.espressage.view.SwipeMenuCreator;
import com.example.espressage.view.SwipeMenuItem;
import com.example.espressage.view.SwipeMenuListView;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/6/27 0027.
 */
public class History extends BasePager {


    private LinearLayout linearLayout;
    private SwipeMenuListView myListView;
    private ArrayList<HistoryInfo> myDataList;
    private BaseAdapter mAdapter;

    public History(Activity activity) {
        super(activity);
    }

    public View initViews() {
        View view = View.inflate(mActivity, R.layout.history_layout, null);
        linearLayout = (LinearLayout) view.findViewById(R.id.ll_layout);
        myListView = (SwipeMenuListView) view.findViewById(R.id.lv_history_list);


        return view;
    }

    @Override
    public void initData() {
        Cursor cursor = MyDbOpenHelper.dbHelper.rawQuery("select _id from expressInfo", null);
        if (cursor.getCount() == 0) {
            linearLayout.setVisibility(View.VISIBLE);
            myListView.setVisibility(View.INVISIBLE);
        } else {
            //  int i = MyDbOpenHelper.dbHelper.delete("expressInfo", "mailNo=?", new String[]{"5115120784"});
            //返回值为1删除成功，返回值为0删除失败
            // MyDbOpenHelper.dbHelper.execSQL("select * from expressInfo order by time desc");
            cursor = MyDbOpenHelper.dbHelper.query("expressInfo", null, null, null, null, null, "time desc");
//            cursor = MyDbOpenHelper.dbHelper.rawQuery("select _id from expressInfo", null);
            myDataList = new ArrayList();
            while (cursor.moveToNext()) {
                HistoryInfo info = new HistoryInfo();
                info.content = cursor.getString(cursor.getColumnIndex("content"));
                info.expName = cursor.getString(cursor.getColumnIndex("expName"));
                info.goodsName = cursor.getString(cursor.getColumnIndex("goodsName"));
                info.mailNo = cursor.getString(cursor.getColumnIndex("mailNo"));
                info.simpleName = cursor.getString(cursor.getColumnIndex("simpleName"));
                myDataList.add(info);
            }
            linearLayout.setVisibility(View.INVISIBLE);
            myListView.setVisibility(View.VISIBLE);
            mAdapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return myDataList.size();
                }

                @Override
                public HistoryInfo getItem(int position) {
                    return myDataList.get(position);
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    ViewHolder myHolder;
                    if (convertView == null) {
                        myHolder = new ViewHolder();
                        convertView = View.inflate(mActivity, R.layout.history_info_ietm, null);
                        myHolder.ivLogo = (ImageView) convertView.findViewById(R.id.iv_logo);
                        myHolder.ivStatus = (ImageView) convertView.findViewById(R.id.iv_status);
                        myHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
                        myHolder.tvExpName = (TextView) convertView.findViewById(R.id.tv_expName);
                        myHolder.tvMailNo = (TextView) convertView.findViewById(R.id.tv_mailNo);
                        myHolder.goodsName = (TextView) convertView.findViewById(R.id.tv_goodsName);
                        convertView.setTag(myHolder);
                    } else {
                        myHolder = (ViewHolder) convertView.getTag();
                    }
                    Bitmap bitmap = BitmapFactory.decodeFile(mActivity.getFilesDir().getAbsolutePath() + "/" + myDataList.get(position).simpleName + ".jpg");
                    myHolder.ivLogo.setImageBitmap(bitmap);
                    myHolder.tvExpName.setText(myDataList.get(position).expName);
                    myHolder.tvMailNo.setText(myDataList.get(position).mailNo);
                    if (TextUtils.isEmpty(myDataList.get(position).goodsName) || myDataList.get(position).goodsName.equals("")) {
                        myHolder.goodsName.setVisibility(View.INVISIBLE);
                    } else {
                        myHolder.goodsName.setVisibility(View.VISIBLE);
                        myHolder.goodsName.setText(myDataList.get(position).goodsName);
                    }
                    Cursor cursor1;
                    String status = null;
                    try {
                        cursor1 = MyDbOpenHelper.dbHelper.rawQuery("select status from expressInfo where mailNo=?", new String[]{myDataList.get(position).mailNo});
                        cursor1.moveToNext();
                        status = cursor1.getString(cursor1.getColumnIndex("status"));
                    } catch (IllegalArgumentException E) {
                        E.printStackTrace();

                    }

                    if (TextUtils.isEmpty(status) || status.equals("-1") || status.equals("2") || status.equals("1")) {
                        myHolder.tvContent.setText("暂无结果，点击继续查询");
                        myHolder.tvContent.setTextColor(mActivity.getResources().getColor(R.color.zi));
                        myHolder.ivStatus.setVisibility(View.GONE);
                    } else if (status.equals("4")) {
                        myHolder.tvContent.setText("快递已签收");
                        myHolder.tvContent.setTextColor(mActivity.getResources().getColor(R.color.lan));
                        myHolder.ivStatus.setVisibility(View.VISIBLE);
                        myHolder.ivStatus.setImageResource(R.mipmap.receive);
                    } else {
                        myHolder.tvContent.setText(myDataList.get(position).content);
                        myHolder.tvContent.setTextColor(mActivity.getResources().getColor(R.color.redd));
                        myHolder.ivStatus.setVisibility(View.VISIBLE);
                        myHolder.ivStatus.setImageResource(R.mipmap.status);
                    }

                    return convertView;
                }
            };
            myListView.setAdapter(mAdapter);
            myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(mActivity, QuearyResult.class);
                    intent.putExtra("ExpressNumber", myDataList.get(position).mailNo);
                    intent.putExtra("ExpressName", myDataList.get(position).expName);
                    intent.putExtra("simpleName", myDataList.get(position).simpleName);
                    intent.putExtra("flag", true);
                    mActivity.startActivity(intent);
                }
            });
            SwipeMenuCreator creator = new SwipeMenuCreator() {
                @Override
                public void create(SwipeMenu menu) {
                  /*  // create "open" item
                    SwipeMenuItem deleteItem = new SwipeMenuItem(mActivity.getApplicationContext());
                    // set item background
//                    deleteItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                    // set item width
                    deleteItem.setWidth(dp2px(mActivity.getApplicationContext(), 80));
                    // set item title
                    deleteItem.setTitle("删除");
                    // set item title fontsize
                    deleteItem.setTitleSize(16);
                    // set item title font color
                    deleteItem.setTitleColor(mActivity.getResources().getColor(R.color.write));
                    // add to menu
                    menu.addMenuItem(deleteItem);*/

                    // create "delete" item
                    SwipeMenuItem deleteItem = new SwipeMenuItem(mActivity.getApplicationContext());
                    // set item background
//                    deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
//                            0x3F, 0x25)));
                    // set item width
                    deleteItem.setWidth(dp2px(mActivity.getApplicationContext(), 90));
                    // set a icon
                    deleteItem.setIcon(R.drawable.delete);
                    // add to menu
                    menu.addMenuItem(deleteItem);
                }
            };
            myListView.setMenuCreator(creator);
            myListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                    switch (index) {
                        case 0:
                            MyDbOpenHelper.dbHelper.delete("expressInfo", "mailNo=?", new String[]{myDataList.get(position).mailNo});
                            myDataList.remove(position);
                            if (myDataList.size() == 0) {
                                linearLayout.setVisibility(View.VISIBLE);
                                myListView.setVisibility(View.INVISIBLE);
                            }
                            mAdapter.notifyDataSetChanged();
                            break;
                    }
                    return false;
                }
            });
        }

    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private class ViewHolder {
        private ImageView ivLogo;
        private ImageView ivStatus;
        private TextView tvContent;
        private TextView tvExpName;
        private TextView tvMailNo;
        private TextView goodsName;
    }

}
