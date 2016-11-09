package com.example.espressage.extendsbasepager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.espressage.R;
import com.example.espressage.Utils.MyDbOpenHelper;
import com.example.espressage.base.BasePager;
import com.example.espressage.bean.Express;
import com.example.sortlistview.CharacterParser;
import com.example.sortlistview.ClearEditText;
import com.example.sortlistview.PinyinComparator;
import com.example.sortlistview.SideBar;
import com.example.sortlistview.SortAdapter;
import com.example.sortlistview.SortModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by Administrator on 2016/6/27 0027.
 */
public class CallPhone extends BasePager{
    private List<SortModel> SourceDateList;
    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;
    private SideBar sideBar;
    private TextView dialog;
    private ListView sortListView;
    private ClearEditText mClearEditText;
    private SortAdapter adapter;

    public CallPhone(Activity activity) {
        super(activity);
    }
    public View initViews() {
       View view=View.inflate(mActivity, R.layout.help_phone,null);
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        sideBar = (SideBar) view.findViewById(R.id.sidrbar);
        dialog = (TextView) view.findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if(position != -1){
                    sortListView.setSelection(position);
                }

            }
        });

        sortListView = (ListView) view.findViewById(R.id.country_lvcountry);
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //设置监听
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" +SourceDateList.get(position).phone));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
//                Toast.makeText(mActivity.getApplication(), ((SortModel) adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
            }
        });


        ArrayList<SortModel> list=new ArrayList<SortModel>();
        Cursor cursor = MyDbOpenHelper.dbHelper.rawQuery("select * from info", null);
        while (cursor.moveToNext()){
            SortModel data= new SortModel();
            data.name=cursor.getString(cursor.getColumnIndex("expName"));
            data.phone=cursor.getString(cursor.getColumnIndex("phone"));
            list.add(data);
        }
        //数据源，这个是为了获取SortModel的sortLetters的属性值，其实没有多大必要，接口返回数据中有拼音，所以不需要把汉字转化为拼音
        // 很有必要因为不是单纯的拼音，使用了CharacterParser来得到可排序的数据源  只有这样才可以调用Collection.sort方法
        SourceDateList = filledData(list);
        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new SortAdapter(mActivity,SourceDateList);
        sortListView.setAdapter(adapter);
        mClearEditText = (ClearEditText) view.findViewById(R.id.filter_edit);
        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                
            }
        });
        return view;
    }
    /**
     * 为ListView填充数据
     * @param list  为了拿到首字母   完全没必要，接口返回的数据有simpleName，可以直接截取首字母并大写
     *                                很有必要  需要CharacterParser  用的是ASCII码来进行的
     * @return
     */
    private List<SortModel> filledData(ArrayList<SortModel> list){
        List<SortModel> mSortList = new ArrayList<SortModel>();
        for(int i=0; i<list.size(); i++){
            SortModel sortModel = new SortModel();
            sortModel.name=list.get(i).name;
            sortModel.phone=list.get(i).phone;
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(list.get(i).name);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                sortModel.setSortLetters(sortString.toUpperCase());
            }else{
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    private void filterData(String filterStr){
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if(TextUtils.isEmpty(filterStr)){
            filterDateList = SourceDateList;
        }else{
            filterDateList.clear();
            for(SortModel sortModel : SourceDateList){
                String name = sortModel.getName();
                if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

}
