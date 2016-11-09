package com.example.espressage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.espressage.R;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
public class RefreshListView extends ListView {
    public static View mFooterView;
    private int mFooterViewHeight;
    private int dy;
    private int endY;

    public RefreshListView(Context context) {
        super(context);
        initFooterView();
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFooterView();
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFooterView();
    }

    int startY = -1;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                startY = (int) ev.getRawY();

                break;
            case MotionEvent.ACTION_MOVE:
                if (startY == -1) {
                    startY = (int) ev.getRawY();
                }
               /* if ( getLastVisiblePosition()==getCount()-1){
                    endY = (int) ev.getRawY();
                    dy = startY-endY;
                    if (dy>0){

                        int padding= dy -mFooterViewHeight;
                        mFooterView.setPadding(0,0,0,dy);
                    }*/
                endY = (int) ev.getRawY();
                dy = startY - endY;

                if (dy > 0 && getLastVisiblePosition() == getCount() - 1) {
                    int padding = dy - mFooterViewHeight;
                    mFooterView.setPadding(0, 0, 0, padding);
                }
                break;
            case MotionEvent.ACTION_UP:
                int y = (int) ev.getRawY();
                if (dy > mFooterViewHeight) {
                    mFooterView.setPadding(0, 0, 0, 0);
                } else {
                    mFooterView.setPadding(0, 0, 0, -mFooterViewHeight);
                }
                if (startY - y > 200) {
                    if (mListener != null) {
                        mListener.upShow();
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }


    private void initFooterView() {
        mFooterView = View.inflate(getContext(), R.layout.refresh_listview_footer, null);
        this.addFooterView(mFooterView);
        mFooterView.measure(0, 0);
        mFooterViewHeight = mFooterView.getMeasuredHeight();
        mFooterView.setPadding(0, 0, 0, -mFooterViewHeight);
        this.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING) {
                    if (getLastVisiblePosition() == getCount() - 1 && flag) {
                        mFooterView.setPadding(0, 0, 0, 0);
                        setSelection(getCount() - 1);//改变listView的显示位置
                        if (mListener != null) {
                            mListener.onLoadMore();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private boolean flag;

    public void OnRefreshComplete(boolean fl) {
        flag = fl;
        mFooterView.setPadding(0, 0, 0, -mFooterViewHeight);
    }

    OnRefreshListener mListener;

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public interface OnRefreshListener {
        void upShow();

        void onLoadMore();
    }
}
