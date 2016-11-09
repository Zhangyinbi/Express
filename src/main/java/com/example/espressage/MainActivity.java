package com.example.espressage;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.PagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.espressage.Utils.MyDbOpenHelper;
import com.example.espressage.base.BasePager;
import com.example.espressage.extendsbasepager.CallPhone;
import com.example.espressage.extendsbasepager.History;
import com.example.espressage.extendsbasepager.Search;
import com.example.espressage.view.NoScrollViewPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import java.util.ArrayList;

public class MainActivity extends SlidingFragmentActivity {
    private Animation btnAnim;
    private Animation layout;
    private TextView tvTitle;
    private RadioGroup rGroup;
    private ArrayList<BasePager> mPagerlist;

    private NoScrollViewPager mViewPager;
    public FrameLayout main_content;
    private ImageView tvSetting;
    private ImageView ivZan;
    private ImageView ivSum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.e("------执行了----", "onCreate: " );
//        getWindow().setStatusBarColor(getResources().getColor(R.color.red));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setBehindContentView(R.layout.left_menu);//设置侧边栏


        SlidingMenu slidingMenu = getSlidingMenu();//获取侧边栏对象
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);//设置全屏触摸
        slidingMenu.setBehindOffset(260);
//        slidingMenu.setAboveOffset(40);
        setContentView(R.layout.activity_main);
        main_content = (FrameLayout) findViewById(R.id.main_content);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        rGroup = (RadioGroup) findViewById(R.id.rGroup);
        tvSetting = (ImageView) findViewById(R.id.btn_setting);
        ivZan = (ImageView) findViewById(R.id.iv_zan);
        ivSum = (ImageView) findViewById(R.id.iv_sum);
        layout = AnimationUtils.loadAnimation(this, R.anim.layout_anim);
        btnAnim = AnimationUtils.loadAnimation(this, R.anim.btn_anim);
        ivZan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivZan.startAnimation(btnAnim);

            }
        });
        layout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivSum.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        btnAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivSum.setVisibility(View.VISIBLE);
                ivSum.startAnimation(layout);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        tvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSlidingMenu();
            }
        });
        mViewPager = (NoScrollViewPager) findViewById(R.id.vp_viewpager);
        initData();

    }

    @Override
    protected void onStart() {
        super.onStart();
//        Log.e("-----执行了-----", "onStart: " );
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.e("-----执行了-----", "onResume: " );
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Log.e("-----执行了-----", "onRestart: " );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Log.e("------调用了-----", "onDestroy: " );
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.e("----调用了-----", "onPause: " );
    }

    private void initData() {
        mPagerlist = new ArrayList<BasePager>();
        mPagerlist.add((new Search(this)));
        mPagerlist.add(new History(this));
        mPagerlist.add(new CallPhone(this));
        mViewPager.setAdapter(new ContentAdapter());

        rGroup.check(R.id.rb_search);
        tvTitle.setText(((RadioButton) rGroup.getChildAt(0)).getText());
        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_search:
                        setSlidingMenuEnable(true);
                        tvSetting.setVisibility(View.VISIBLE);
                        RadioButton rb = (RadioButton) group.getChildAt(0);
                        mViewPager.setCurrentItem(0, false);
                        tvTitle.setText(rb.getText());
                        break;
                    case R.id.rb_history:
                        setSlidingMenuEnable(false);
                        tvSetting.setVisibility(View.GONE);
                        RadioButton rb1 = (RadioButton) group.getChildAt(1);
                        mViewPager.setCurrentItem(1, false);
                        tvTitle.setText(rb1.getText());
                        break;
                    case R.id.rb_tell:
                        setSlidingMenuEnable(false);
                        tvSetting.setVisibility(View.GONE);
                        RadioButton rb2 = (RadioButton) group.getChildAt(2);
                        mViewPager.setCurrentItem(2, false);
                        tvTitle.setText(rb2.getText());
                        break;
                }
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mPagerlist.get(position) instanceof History) {
                    mPagerlist.get(position).initData();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void toggleSlidingMenu() {
        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.toggle();//切换状态，显示时隐藏，隐藏时显示
    }

    class ContentAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mPagerlist.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mPagerlist.get(position).mRootView);
            return mPagerlist.get(position).mRootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    String companyName = data.getStringExtra("key");
                    int position = data.getIntExtra("num", 0);
                    String simpName = data.getStringExtra("simpleName");
                    Search.setName(companyName, position, simpName);
                }
                break;
            case 9:
                if (resultCode == RESULT_OK) {
                    String num = data.getStringExtra("result");
                    Search.setnum(num);

                }
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setSlidingMenuEnable(boolean enable) {
        SlidingMenu slidingMenu = getSlidingMenu();
        if (enable) {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        } else {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("----调用了------", "onSaveInstanceState: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("-----调用了-----", "onStop: ");
    }

}
