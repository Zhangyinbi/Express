package com.example.espressage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.MessageQueue;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.example.espressage.Utils.BitmapCache;
import com.example.espressage.Utils.MyApplication;
import com.example.espressage.bean.Express;
import com.example.espressage.global.DataInterface;
import com.example.espressage.view.RefreshListView;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ExpressList extends Activity {

    private RefreshListView lvCompany;
    public static Express express;
    ArrayList<Express.Data.DataItem> data;
    int i = 1;
    private boolean b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express_list);
        initView();
        initData();
    }

    private void initView() {
        lvCompany = (RefreshListView) findViewById(R.id.lv_company);
        lvCompany.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void upShow() {
//                Log.e("---=====------", "initView: "+lvCompany.getCount() );
//                Log.e("---=====------", "initView: "+lvCompany.getLastVisiblePosition() );
                if (!express.showapi_res_body.flag) {
//                    Log.e("---=-=-=-=-=-", "toast方法执行了 " );
                    Toast.makeText(getApplicationContext(), "没有更多了", Toast.LENGTH_SHORT).show();
                    lvCompany.OnRefreshComplete(false);


                }
            }

            @Override
            public void onLoadMore() {
                if (express.showapi_res_body.flag) {
                    i++;
                    getDataFromServer(i);
                }
            }
        });


        lvCompany.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent in = new Intent();
                if ((view.findViewById(R.id.tv_name)) != null) {
                    in.putExtra("key", ((TextView) view.findViewById(R.id.tv_name)).getText());
                    in.putExtra("num", position);
                    in.putExtra("simpleName", express.showapi_res_body.expressList.get(position).simpleName);
                    setResult(RESULT_OK, in);
                    finish();
                }
            }
        });
    }


    private void initData() {
        getDataFromServer(i);
    }

    private void getDataFromServer(int i) {
        RequestQueue queues = MyApplication.getHttpQueues();
        b = true;
        if (i == 1) {
            b = false;
        }

        StringRequest request = new StringRequest(Request.Method.GET, DataInterface.EXPRESS_COMPANY_URL + i, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                parseData(s, b);
                lvCompany.OnRefreshComplete(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("------",volleyError.getMessage()  );
                Toast.makeText(getApplicationContext(), "加载数据失败", Toast.LENGTH_LONG).show();
                lvCompany.OnRefreshComplete(true);
            }
        });
        request.setTag(DataInterface.EXPRESS_COMPANY_URL);
        queues.add(request);
    }


    private void parseData(String s, boolean b) {
        Gson gson = new Gson();
        express = gson.fromJson(s, Express.class);
        if (express.showapi_res_body.flag == false) {
            Toast.makeText(this, "没有更多了", Toast.LENGTH_SHORT).show();
            lvCompany.OnRefreshComplete(false);
//            lvCompany.removeFooterView(RefreshListView.mFooterView);
//            parseData(s,true);
            return;
        }
        if (!b) {
            data = express.showapi_res_body.expressList;
            lvCompany.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return data.size();
                }

                @Override
                public Express.Data.DataItem getItem(int position) {
                    return data.get(position);
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    HolderView holder;
                    if (convertView == null) {
                        convertView = View.inflate(getApplicationContext(), R.layout.list_item, null);
                        holder = new HolderView();
                        holder.ivPhoto = (ImageView) convertView.findViewById(R.id.iv_photo);
                        holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                        convertView.setTag(holder);
                    } else {
                        holder = (HolderView) convertView.getTag();
                    }
                    ImageLoader loder1 = new ImageLoader(MyApplication.getHttpQueues(), new BitmapCache());
                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.ivPhoto, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
//                   Log.e("=====position",position+"");
                    if (getItem(position).imgUrl != null) {
                        loder1.get(getItem(position).imgUrl, listener);
                        saveLoge(position);
                    } else {
                        holder.ivPhoto.setImageResource(R.drawable.wifi);
                    }
                    holder.tvName.setText(getItem(position).expName);
                    return convertView;
                }
            });
        } else {
            data.addAll(express.showapi_res_body.expressList);
            lvCompany.deferNotifyDataSetChanged();
        }


    }


    private  void saveLoge(final int position) {
        new Thread(){
            @Override
            public void run() {
                Log.e("-------", "saveLoge:--- " + this);
                try {
                        URL mUrl = new URL(data.get(position).imgUrl);
                        HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
                        conn.connect();
                        conn.setConnectTimeout(5000);
                        InputStream is = conn.getInputStream();
                        if (conn.getResponseCode() == 200) {
                            File file = new File(getFilesDir(), data.get(position).simpleName + ".jpg");
                            if (file.exists() == false) {
                                FileOutputStream fos = new FileOutputStream(file);
                                byte[] by = new byte[1024];
                                int len;
                                while ((len = is.read(by)) != -1) {
                                    fos.write(by, 0, len);
                                }
                                is.close();
                                fos.close();
                            }
                        }
                } catch (MalformedURLException e) {
//                    Log.e("----1-----", e.toString());
                    e.printStackTrace();
                } catch (IOException e) {
//                    Log.e("----2-----", e.toString());
                    e.printStackTrace();
                }

            }
        }.start();
    }

    public class HolderView {
        public ImageView ivPhoto;
        public TextView tvName;
    }

}
