package com.example.espressage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends Activity {
    private Button mBtn;
    private CirclePageIndicator mIndicator;
    private ViewPager mViewPager;
    private List<View> views = new ArrayList<View>();
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_guide);
        sp = getSharedPreferences("data", MODE_PRIVATE);

        mBtn = (Button) findViewById(R.id.btn_start);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                GuideActivity.this.startActivity(intent);
                sp = getSharedPreferences("data", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("name", "name");
                editor.commit();
                finish();
            }
        });
        if (sp.getString("name", "").equals("name")) {
            Intent intent = new Intent(GuideActivity.this, MainActivity.class);
            GuideActivity.this.startActivity(intent);
            finish();
        }


        mViewPager = (ViewPager) findViewById(R.id.vp_guide);
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        ImageView img = new ImageView(this);
        img.setImageResource(R.drawable.guideone);
        img.setScaleType(ImageView.ScaleType.FIT_XY);

        ImageView img1 = new ImageView(this);
        img1.setImageResource(R.drawable.guidetwo);
        img1.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageView img2 = new ImageView(this);
        img2.setImageResource(R.drawable.guidethree);
        img2.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageView img3 = new ImageView(this);
        img3.setImageResource(R.drawable.guidefour);
        img3.setScaleType(ImageView.ScaleType.FIT_XY);
        views.add(img);
        views.add(img1);
        views.add(img2);
        views.add(img3);

        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View v = views.get(position);

                container.addView(v);
                return v;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(views.get(position));
            }
        });

        mIndicator.setViewPager(mViewPager);
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == views.size()-1) {
                    mBtn.setVisibility(View.VISIBLE);
                }else {
                    mBtn.setVisibility(View.GONE);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mIndicator.onPageSelected(0);
//        mIndicator.setSnap(true);

    }
}
