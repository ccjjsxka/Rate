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
        //接受传入的数据
        float dollar2 = intent.getFloatExtra("dollar_rate_key",0.0f);
        float euro2 = intent.getFloatExtra("euro_rate_key",0.0f);
        float won2 = intent.getFloatExtra("won_rate_key",0.0f);

        Log.i(TAG,"onCreate:dollar2="+dollar2);
        Log.i(TAG,"onCreate:euro2="+euro2);
        Log.i(TAG,"onCreate:won2="+won2);

        //获取控件并显示接收的参数值
        dollarRate = findViewById(R.id.dollarRate);
        euroRate = findViewById(R.id.euroRate);
        wonRate = findViewById(R.id.wonRate);
        //显示数据到控件
        dollarRate.setText(String.valueOf(dollar2));
        euroRate.setText(String.valueOf(euro2));
        wonRate.setText(String.valueOf(won2));

    }
    public void save(View btn){
       Log.i(TAG,"save:");
       //获取新的值
        float newDollar = Float.parseFloat(dollarRate.getText().toString());
        float newEuro = Float.parseFloat(euroRate.getText().toString());
        float newWon = Float.parseFloat(wonRate.getText().toString());

        Log.i(TAG,"save:获取到新的值");
        Log.i(TAG,"save:newDollar="+ newDollar);
        Log.i(TAG,"save:newEuro="+ newEuro);
        Log.i(TAG,"save:newWon="+ newWon);
        //通过intent对象向调用页面返回数据，此处用bundle对象带回
        Intent intent = getIntent();
        Bundle bdl = new Bundle();
        bdl.putFloat("key_dollar",newDollar);
        bdl.putFloat("key_euro",newEuro);
        bdl.putFloat("key_won",newWon);
        intent.putExtras(bdl);
        setResult(2,intent);
        //返回到调用页面
        finish();
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

}