package com.example.espressage.extendsbasepager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.espressage.QuearyResult;
import com.example.espressage.R;
import com.example.espressage.base.BasePager;
import com.example.zxing.CaptureActivity;


/**
 * Created by Administrator on 2016/6/27 0027.
 */
public class Search extends BasePager {
    private static int position;
    private static TextView etName;
    private static String simplename;
    private static ImageButton delete;
    private ImageButton del;
    private EditText goodsName;
    private TextView must;
    private ImageButton jump;
    private Button btnSearch;
    private TextView enNum;
    private ImageButton btnPhone;
    private static EditText etnum;

    public Search(Activity activity) {

        super(activity);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public View initViews() {
        View view = View.inflate(mActivity, R.layout.search, null);
        etnum = (EditText) view.findViewById(R.id.et_num);
        btnPhone = (ImageButton) view.findViewById(R.id.btn_photo);
        etName = (TextView) view.findViewById(R.id.et_name);
        delete = (ImageButton) view.findViewById(R.id.btn_delete);
        btnSearch = (Button) view.findViewById(R.id.btn_search);
        del = (ImageButton) view.findViewById(R.id.btn_del);
        enNum = (TextView) view.findViewById(R.id.et_num);
        must = (TextView) view.findViewById(R.id.tv_nomust);
        jump = (ImageButton) view.findViewById(R.id.ib_jump);
        goodsName = (EditText) view.findViewById(R.id.et_goodsName);
        onClick();
        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openCameraIntent = new Intent(mActivity,CaptureActivity.class);
                mActivity.startActivityForResult(openCameraIntent, 9);
            }
        });



        /*if (handler == null) {
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (str != null) {
                        etName.setText(str);
                        delete.setVisibility(View.VISIBLE);
                        str = null;
                    }
                    handler.sendEmptyMessageDelayed(0, 1000);
                }
            };
            handler.sendEmptyMessageDelayed(0, 1000);
        } else {
            handler.sendEmptyMessageDelayed(0, 1000);
        }*/
        return view;
    }

    private void onClick() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number=enNum.getText().toString();
                String name = etName.getText().toString().trim();
                String good = goodsName.getText().toString().trim();
                if (!TextUtils.isEmpty(number)  && !TextUtils.isEmpty(name)) {
                    Intent intent = new Intent(mActivity, QuearyResult.class);
                    intent.putExtra("ExpressNumber", enNum.getText().toString());
                    intent.putExtra("ExpressName", name);
                    intent.putExtra("simpleName", simplename);
                    intent.putExtra("position",position);
                        if (good!=null){
                            intent.putExtra("goodsName",good);
                        }else {
                            intent.putExtra("goodsName","");
                        }
//                    MyDbOpenHelper.dbHelper.close();
                    mActivity.startActivity(intent);
                } else {
                    Toast.makeText(mActivity, "请输入运单号和公司名称", Toast.LENGTH_LONG).show();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etName.getText() != null) {
                    etName.setText("");
                }
                delete.setVisibility(View.GONE);
            }
        });
        goodsName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    del.setVisibility(View.VISIBLE);
                    must.setVisibility(View.GONE);
                    del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if ((goodsName.getText()) != null) {
                                goodsName.setText("");
                                del.setVisibility(View.INVISIBLE);
                                must.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
                if (TextUtils.isEmpty(s)) {
                    del.setVisibility(View.INVISIBLE);
                    must.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  MainActivity mainUi= (MainActivity) mActivity;
                mainUi.main_content.setVisibility(View.INVISIBLE);
                initFragment();*/
                Intent in = new Intent(mActivity, com.example.espressage.ExpressList.class);
                mActivity.startActivityForResult(in, 0);
            }
        };
        jump.setOnClickListener(click);
        etName.setOnClickListener(click);
    }



    public static void setName(String name, int pos,String simple) {
       simplename=simple;
        position=  pos;
        etName.setText(name);
        delete.setVisibility(View.VISIBLE);

    }

    public static void setnum(String num) {
        etnum.setText(num);
    }




   /* private void initFragment() {
        //开启事务
        android.app.FragmentManager fm = mActivity.getFragmentManager();
        android.app.FragmentTransaction ft = fm.beginTransaction();
        //替换帧布局
        ft.replace(R.id.main_content, new ExpressList(), FRAGMENT_MAIN_CONTENT);
        //提交事务
        ft.addToBackStack(null);
        ft.commit();
    }*/


}
