package com.example.espressage;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.espressage.Utils.MyApplication;
import com.example.espressage.Utils.MyDbOpenHelper;
import com.example.espressage.bean.Express;
import com.example.espressage.global.DataInterface;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends Activity {
    private ImageView rlRoot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        if (!isTaskRoot()){
            finish();
            return;
        }

       /* ActivityManager mAm=(ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);;
        List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo rti : taskList) {

                finish();
            }
        }*/




        MyDbOpenHelper helper =new MyDbOpenHelper(this);
        setContentView(R.layout.activity_splash);
        rlRoot = (ImageView) findViewById(R.id.rl_Root);//找到需要旋转的控件
        startAnimation();//启动动漫（一张图片的旋转）
        getDataFromServer();

    }
    private void getDataFromServer() {

        RequestQueue queues = MyApplication.getHttpQueues();
        StringRequest request = new StringRequest(Request.Method.GET, DataInterface.EXPRESS_COMPANY_URLL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                parseData(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                getDataFromServer();
            }
        });
        request.setTag(DataInterface.EXPRESS_COMPANY_URL);
        queues.add(request);
    }

    private void parseData(String s) {
        Gson gson=new Gson();
        Express express = gson.fromJson(s, Express.class);
        ArrayList<Express.Data.DataItem> expressList = express.showapi_res_body.expressList;
        for (Express.Data.DataItem dataItem:expressList){
            ContentValues values = new ContentValues();
            values.put("expName",dataItem.expName);
            values.put("phone",dataItem.phone);
            MyDbOpenHelper.dbHelper.insert("info",null,values);
        }
    }

    private void startAnimation() {
        //RotateAnimation 旋转动画效果
        RotateAnimation ra = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(2000);//动画执行时间
        ra.setFillAfter(true);//动画执行完后保持最终状态


        //AlphaAnimation 透明度动画效果
        AlphaAnimation aa = new AlphaAnimation(0f, 1f);
        aa.setDuration(2000);
        aa.setFillAfter(true);

        //ScaleAnimation 缩放动画效果
        ScaleAnimation sa = new ScaleAnimation(0f, 1f, 0f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(2000);
        sa.setFillAfter(true);

        //增加一个动漫的集合，把效果都添加进去
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(ra);
        set.addAnimation(aa);
        set.addAnimation(sa);

        //设置动画的监听事件
        set.setAnimationListener(new Animation.AnimationListener() {
            /**
             * 动漫开始的时候
             * @param animation
             */
            @Override
            public void onAnimationStart(Animation animation) {

            }

            /**
             * 动画结束
             * @param animation
             */
            @Override
            public void onAnimationEnd(Animation animation) {
                //跳转到引导界面
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
                if (sp.getString("name", "").equals("name")) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(intent);
                    finish();
                }else{
                    startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                    finish();//结束当前页面
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        rlRoot.startAnimation(set);
    }
}
