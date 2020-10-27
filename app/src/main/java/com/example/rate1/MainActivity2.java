package com.example.rate1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = "MainActivity2";
    Handler handler;
    EditText dollarRate,euroRate,wonRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();
        final ListView listView = (ListView)findViewById(R.id.mylist);
        //显示列表："one", "two", "three", "four"
//        String data[] = {"one", "two", "three", "four"};
//        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
//        listView.setAdapter(adapter);
        //使用bundle获得数据
        Bundle bundle = intent.getExtras();
        float dollar2 = intent.getFloatExtra("dollar_rate_key",0.0f);
        float euro2 = intent.getFloatExtra("euro_rate_key",0.0f);
        float won2 = intent.getFloatExtra("won_rate_key",0.0f);

        Log.i(TAG,"onCreate:dollar2="+dollar2);
        Log.i(TAG,"onCreate:euro2="+euro2);
        Log.i(TAG,"onCreate:won2="+won2);

        dollarRate = findViewById(R.id.dollarRate);
        euroRate = findViewById(R.id.euroRate);
        wonRate = findViewById(R.id.wonRate);

        dollarRate.setText(String.valueOf(dollar2));
        euroRate.setText(String.valueOf(euro2));
        wonRate.setText(String.valueOf(won2));
        //每天更新一次汇率
        //判断判断Myrate文件里面所存的日期与现在的日期对比，判断是否更新
        SharedPreferences sp = getSharedPreferences("Myrate",Activity.MODE_PRIVATE);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dt = format.format(date);
        String ud = sp.getString("date","");
        Log.i(TAG,"onCreate:the old_date="+ ud);
        //如果日期不匹配，则更新
        if (!ud.equals(dt)){
            Log.i(TAG,"onCreate:the new_date="+ dt);
            //开启子线程
            Thread t = new Thread();
            t.start();
            //线程间消息同步
            handler = new Handler(){
                @Override
                public void handleMessage(Message msg){
                    if (msg.what==5){
                        List<String> list = (List<String>)msg.obj;
                        ListAdapter adapter = new ArrayAdapter<String>(MainActivity2.this,android.R.layout.simple_list_item_1,list);
                        listView.setAdapter(adapter);
                    }
                    super.handleMessage(msg);
                }
            };
        }
    }
    public void save(View btn){
        if (btn.getId()==R.id.btn_save){
            Intent intent_save = getIntent();
            Bundle bdl = new Bundle();
            float newDollar = Float.parseFloat(dollarRate.getText().toString());
            float newEuro = Float.parseFloat(euroRate.getText().toString());
            float newWon = Float.parseFloat(wonRate.getText().toString());
            //获取当前日期
            Date date1 = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String d = format.format(date1);
            //将汇率存入Myrate文件里面
            // 将当前日期存入Myrate文件里面，用于更新时的比对
            SharedPreferences sp = getSharedPreferences("Myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat("dollar_rate",newDollar);
            editor.putFloat("euro_rate",newEuro);
            editor.putFloat("won_rate",newWon);
            editor.putString("date",d);
            editor.apply();

            bdl.putFloat("dollar_rate_key",newDollar);
            bdl.putFloat("euro_rate_key",newEuro);
            bdl.putFloat("won_rate_key",newWon);
            intent_save.putExtras(bdl);

            //设置resultCode及带回的数据
            setResult(1,intent_save);
            //返回到调用页面
            finish();
        }
    }
    public void run() {
        Log.i(TAG,"run:run()……");
        Message msg = handler.obtainMessage(5);
//        //获取msg对象，用于返回主线程
//        Message msg = handler.obtainMessage(5);
//        //msg.what = 5;
//        msg.obj = "Hello from run()";
//        handler.sendMessage(msg);
        try{
            float dollar = 0, euro = 0, won = 0;
            //方法一：
            String url = "http://www.usd-cny.com/bankofchina.htm";
            Document doc = Jsoup.connect(url).get();
            Log.i(TAG, "run: "+ doc.title());
            Elements tables = doc.getElementsByTag("table");
            Element table0 = tables.get(0);
            // 获取 TD 中的数据
            Elements tds = table0.getElementsByTag("td");
            List<String> list2 = new ArrayList<String>();
            for(int i=0; i<tds.size(); i+=6){
                Element td1 = tds.get(i);
                Element td2 = tds.get(i + 5);
                String str1 = td1.text();
                String val = td2.text();
                Log.i(TAG, "run: " + str1 + "==>" + val);
                String s = (String)(str1 + "==>" + val);
                list2.add(s);
                float v = 100f / Float.parseFloat(val);
                float rate =(float)(Math.round(v*100))/100;
                // 获取需要的数据并返回
                if(str1.equals("美元")){
                    dollar = rate;
                    Log.i(TAG, "run: dollar_rate==>" + dollar);
                    dollarRate.setText(String.valueOf(dollar));
                }else if(str1.equals("欧元")){
                    euro = rate;
                    Log.i(TAG, "run: euro_rate==>" + euro);
                    euroRate.setText(String.valueOf(euro));
                }else if(str1.equals("韩元")){
                    won = rate;
                    Log.i(TAG, "run: won_rate==>" + won);
                    wonRate.setText(String.valueOf(won));
                }
            }
            msg.obj = list2;
            handler.sendMessage(msg);
            //方法二：
            /*Element table = doc.getElementsByTag("table").first();
            Elements trs = table.getElementsByTag("tr");
            for(Element tr:trs){
                Elements tds = tr.getElementsByTag("td");
                if(tds.size>0){
                    //获取数据
                    String td1 = tds.get(0).text();
                    String td2 = tds.get(5).text();
                }
            }*/

        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private String inputStream2String(InputStream inputStream)throws IOException{
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream,"gb2312");
        while (true){
            int rsz = in.read(buffer,0,buffer.length);
            if(rsz<0)
                break;
            out.append(buffer,0,rsz);
        }
        return out.toString();
    }
}