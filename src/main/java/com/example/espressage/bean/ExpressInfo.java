package com.example.espressage.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
public class ExpressInfo {
    public Info showapi_res_body;

    public class Info {
        public String msg;
        public ArrayList<ChildInfo> data;
        public int status;
        public String mailNo;

        public class ChildInfo {
            public String time;
            public String context;
        }
    }
}
