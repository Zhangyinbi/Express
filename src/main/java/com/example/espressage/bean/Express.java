package com.example.espressage.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/28 0028.
 */
public class Express {
    public Data showapi_res_body;

    public class Data {
        public ArrayList<DataItem> expressList;
        public boolean flag;
        public int ret_code;

        public class DataItem {
            public String expName;
            public String imgUrl;
            public String phone;
            public String simpleName;

        }
    }
}
