package com.example.rate1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

//    EditText inp;
    float dollarRate=6.77f, euroRate=8.70f, wonRate=0.0058f;
    private static final String TAG = "MainActivity2";
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
        dollarRate = sharedPreferences.getFloat("dollar_rate",0.0f);
        euroRate = sharedPreferences.getFloat("euro_rate",0.0f);
        wonRate = sharedPreferences.getFloat("won_rate",0.0f);
        Log.i(TAG,"onCreate:sp dollarRate=" + dollarRate);
        Log.i(TAG,"onCreate:sp euroRate=" + euroRate);
        Log.i(TAG,"onCreate:sp wonRate=" + wonRate);

        //开启子线程
        Thread t = new Thread((Runnable) this);
        t.start();

        handler =  new Handler(){
            @Override
            public void handleMessage(Message msg){
                if (msg.what==5){
                    String str = (String)msg.obj;
                    Log.i(TAG,"handleMessage: getMessage msg ="+ str);
//                    show.setText(str);
                }
                super.handleMessage(msg);
            }
        };
    }
    public void onClick(View v){
        EditText inp = findViewById(R.id.inp);
        TextView out = findViewById(R.id.out);
        String a = inp.getText().toString();
        float t = Float.parseFloat(a);
        float b;
//        double dollarRate=6.77, euroRate=8.70, wonRate=0.0058;
        if (inp.getText().toString()==null){
            Toast.makeText(this,"请输入金额",Toast.LENGTH_SHORT).show();
        }else {
            if(v.getId()==R.id.btn_dollar){
                b = t/dollarRate;
                out.setText(b+"");
          }
            if (v.getId()==R.id.btn_euro){
                b = t/euroRate;
                out.setText(b+"");
            }
            if (v.getId()==R.id.btn_won){
                b = t/wonRate;
                out.setText(b+"");
            }
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && requestCode == 2) {
            Bundle bundle = data.getExtras();
            dollarRate = bundle.getFloat("key_dollar",0.1f);
            euroRate = bundle.getFloat("key_euro",0.1f);
            wonRate = bundle.getFloat("key_won",0.1f);
            Log.i(TAG,"onActivityResult: dollarRate=" + dollarRate);
            Log.i(TAG,"onActivityResult: euroRate=" + euroRate);
            Log.i(TAG,"onActivityResult: wonRate=" + wonRate);
        }
        //将新设置的汇率写到sp里
        SharedPreferences sharedPreferences = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("dollar_rate",dollarRate);
        editor.putFloat("euro_rate",euroRate);
        editor.putFloat("won_rate",wonRate);
        editor.commit();
        Log.i(TAG,"onActivityResult: 数据已保存到sharedPreferences");
    }
    public void open (View v){
        Intent config = new Intent(this,MainActivity2.class);
        config.putExtra("dollar_rate_key",dollarRate);
        config.putExtra("euro_rate_key",euroRate);
        config.putExtra("won_rate_key",wonRate);
        Log.i(TAG,"open:dollarRate="+ dollarRate);
        Log.i(TAG,"open:euroRate="+ euroRate);
        Log.i(TAG,"open:wonRate="+ wonRate);
        //startActivity(config);
        startActivityForResult(config,1);
//
//        Intent intent = new Intent(this,MainActivity2.class);
//        intent.putExtra("dollar_rate_key",dollarRate);
//        Bundle bdl = new Bundle();
//        bdl.putDouble("key_dollar",newDollar);
//        bdl.putDouble("key_euro",newEuro);
//        bdl.putDouble("key_won",newWon);
//        intent.putExtra(bdl);
//        setResult(2,intent);

        //获取SharedPreferences对象
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        PreferenceManager.getDefaultSharedPreferences(this);
        dollarRate = sharedPreferences.getFloat("dollar_rate",0.0f);
        euroRate = sharedPreferences.getFloat("euro_rate",0.0f);
        wonRate = sharedPreferences.getFloat("won_rate",0.0f);
        //修改保存内容
        SharedPreferences sp = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat("dollar_rate",dollarRate);
        editor.putFloat("euro_rate",euroRate);
        editor.putFloat("won_rate",wonRate);
        editor.apply();
    }
    public void run(){
        Log.i(TAG,"run:run()……");
        for(int i=1;i<3;i++){
            Log.i(TAG,"run:i=" + i);
            try {
                Thread.sleep(2000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        //获取msg对象，用于返回主线程
        Message msg = handler.obtainMessage(5);
//        msg.what = 5
        msg.obj = "hello from run()";
        handler.sendMessage(msg);

        //获取网络数据
        URL url = null;
        try {
            url = new URL("http://www.usd-cny.com/icbc.htm");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream in = http.getInputStream();
            String html = inputStream2String(in);
            Log.i(TAG,"run:html="+ html);
//            Document doc = Jsoup.parse(html);
        } catch (MalformedURLException e) {
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