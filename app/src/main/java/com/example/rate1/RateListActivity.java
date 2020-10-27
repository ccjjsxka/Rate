package com.example.rate1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

public class RateListActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_rate_list);
        List<String>list1= new ArrayList<String>();
        for (int i=1;i<100;i++){
            list1.add("item"+i);
        }
        String[] list_data = {"one","tow","three","four"};
        ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_data);
        setListAdapter(adapter);

        //获取网络数据
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 7) {
                    List<String> list2 = (List<String>) msg.obj;
                    ListAdapter adapter = new ArrayAdapter<String>(RateListActivity.this, android.R.layout.simple_list_item_1, list2);
                    setListAdapter(adapter);
                }
                super.handleMessage(msg);
            }
        };
    }
}