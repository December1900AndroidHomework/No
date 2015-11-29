package com.example.december.no;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ProgressDialog dialog ;
    public static final String TAG = "ASYNC_TASK";
    int id = 1;
    Button search;
    EditText edt;
    SimpleAdapter adapter;
    MyTask myTask;
     private final String str = "https://api.douban.com/v2/user?q=1";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edt = (EditText) findViewById(R.id.edt);
        search = (Button) findViewById(R.id.search);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        final String request_str = str+id;
     //   Log.i("what",request_str);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        search.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //每次只能新建一次new
                myTask = new MyTask();
                try {
                     myTask.execute(request_str);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


    }
    class MyTask extends AsyncTask<String,String,String>  {
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Log.i(TAG, "onPreExecute() called");}

        @Override
        protected String doInBackground(String... params) {
            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(str);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                  BufferedReader reader = new BufferedReader(new InputStreamReader(
                                connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                Log.i("what",sb.toString());
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        //onPostExecute方法用于在执行任务完后台任务后更新U，刷新
        @Override
        protected void onPostExecute(String result) {
            List<Map<String, Object>> list;
            try {
                super.onPostExecute(result);
                JSONObject obj =new JSONObject(result);
                JSONArray transitListArray = obj.getJSONArray("users");
                list = new ArrayList<>();
                for (int i = 0;i < transitListArray.length();i++) {
                    // Build a map for the attributes
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("name", transitListArray.getJSONObject(i).get("name"));
                    map.put("id", transitListArray.getJSONObject(i).get("id"));
                    map.put("created", transitListArray.getJSONObject(i).get("created"));
                    map.put("signature", transitListArray.getJSONObject(i).get("signature"));
                    list.add(map);
                }
                ListView lv = (ListView) findViewById(R.id.lv);
                adapter = new SimpleAdapter(MainActivity.this,list,R.layout.listview,new String[]{"name","id","created","signature"},new int[]{R.id.name,R.id.id,R.id.loc_name,R.id.desc});
                lv.setAdapter(adapter);

            }catch (Exception e){
                e.printStackTrace();
            }



        }



    }

}
