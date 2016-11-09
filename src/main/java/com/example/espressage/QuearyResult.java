package com.example.espressage;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.espressage.Utils.MyApplication;
import com.example.espressage.Utils.MyDbOpenHelper;
import com.example.espressage.fragment.FailFragment;
import com.example.espressage.fragment.SusFragment;
import com.example.espressage.global.DataInterface;

public class QuearyResult extends Activity {
    private String expressName;
    private String expressNumber;
    private TextView tvName;
    private TextView tvNum;
    private ImageView image;
    private int position;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private ProgressBar pb;
    private String mUrl;
    private String url;
    private FrameLayout fl;
    private String goodsName;
    private boolean flag;
    private String simpleName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queary_result);
        Intent intent = getIntent();
        expressName = intent.getStringExtra("ExpressName");
        expressNumber = intent.getStringExtra("ExpressNumber");
        goodsName = intent.getStringExtra("goodsName");
        simpleName = intent.getStringExtra("simpleName");
        position = intent.getIntExtra("position", 0);
        flag = intent.getBooleanExtra("flag", false);
        tvName = (TextView) findViewById(R.id.tv_express_name);
        tvNum = (TextView) findViewById(R.id.tv_expree_num);
        image = (ImageView) findViewById(R.id.iv_express_pic);
        pb = (ProgressBar) findViewById(R.id.pb_progressBar);
        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        fl = (FrameLayout) findViewById(R.id.fl_view);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /*ImageLoader loder1 = new ImageLoader(MyApplication.getHttpQueues(), new BitmapCache());
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(image, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
        loder1.get(ExpressList.express.showapi_res_body.expressList.get(position).imgUrl, listener);*/
        Bitmap bitmap = BitmapFactory.decodeFile(getFilesDir().getAbsolutePath() + "/" + simpleName + ".jpg");
//        Log.e("-----",getFilesDir().getAbsolutePath() + "/" + ExpressList.express.showapi_res_body.expressList.get(position).simpleName + ",jpg");
        image.setImageBitmap(bitmap);
        tvName.setText(expressName);
        tvNum.setText(expressNumber);
        initData();
        if (!flag){
            saveData();
        }
    }

    private void saveData() {
        //判断是否已经存储过
        Cursor cursor = MyDbOpenHelper.dbHelper.rawQuery("select mailNo from expressInfo", null);
        ContentValues values = new ContentValues();
        while (cursor.moveToNext()) {
            String mailNo = cursor.getString(cursor.getColumnIndex("mailNo"));
//            Log.e("--------=mailNo-----", mailNo );
//            Log.e("-------------",expressNumber);
            if (mailNo!=null){
                if (mailNo.equals(expressNumber)) {
                    values.put("expName", expressName);
                    values.put("goodsName", goodsName);
                    values.put("imgUrl", ExpressList.express.showapi_res_body.expressList.get(position).imgUrl);
                    values.put("simpleName", ExpressList.express.showapi_res_body.expressList.get(position).simpleName);
                    values.put("phone", ExpressList.express.showapi_res_body.expressList.get(position).phone);
                    values.put("time",System.currentTimeMillis());
                    MyDbOpenHelper.dbHelper.update("expressInfo", values, "mailNo=?", new String []{expressNumber});
                    return;
                }
            }
        }
        values.put("phone", ExpressList.express.showapi_res_body.expressList.get(position).phone);
        values.put("simpleName", ExpressList.express.showapi_res_body.expressList.get(position).simpleName);
        values.put("imgUrl", ExpressList.express.showapi_res_body.expressList.get(position).imgUrl);
        values.put("mailNo", expressNumber);
        values.put("expName", expressName);
        values.put("goodsName", goodsName);
        values.put("time",System.currentTimeMillis());
        MyDbOpenHelper.dbHelper.insert("expressInfo", null, values);
    }

    public void initData() {
        getDataFromServer();
    }

    private void getDataFromServer() {
        fm = getFragmentManager();
        ft = fm.beginTransaction();
        pb.setVisibility(View.VISIBLE);
        if (flag){
            mUrl = DataInterface.RESEARCH_URL + "nu=" + expressNumber + "&com=" + simpleName;
        }else {
            mUrl = DataInterface.RESEARCH_URL + "nu=" + expressNumber + "&com=" + ExpressList.express.showapi_res_body.expressList.get(position).simpleName;
        }
//        Log.e("expressNumber-------", mUrl );
        StringRequest request = new StringRequest(Request.Method.GET, mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
//                Log.e("---------", s);
                pb.setVisibility(View.INVISIBLE);
                SusFragment susFragment = new SusFragment();
                susFragment.parseData(s,expressNumber);
                ft.replace(R.id.fl_view,susFragment, "sus");
                ft.commitAllowingStateLoss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pb.setVisibility(View.INVISIBLE);
//                Log.e("----------------------", "onErrorResponse: " );

                ft.replace(R.id.fl_view, new FailFragment() {
                    @Override
                    public void show() {
//                        Fragment fail = fm.findFragmentByTag("fail");
                        /*fail.setUserVisibleHint(true);
                        fail.setMenuVisibility(true);*/
//                        fm.popBackStack();
//                        ft.remove(fail);
//                        ft.commit();
                       /* Log.e("----------------------", "查询失败: " );
                        ft.replace(R.id.fl_view,new SusFragment());*/
                        fm = getFragmentManager();
                        ft = fm.beginTransaction();
                        ft.replace(R.id.fl_view, new Fragment());
                        ft.commitAllowingStateLoss();
                        getDataFromServer();
                    }
                }, "fail");
                ft.commit();
            }
        });
        request.setTag("result");
        MyApplication.getHttpQueues().add(request);
    }


}
