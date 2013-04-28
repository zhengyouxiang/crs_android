package com.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.client.MyApplication;
import com.example.client.R;
import com.tool.client.HttpHelper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TaskListActivity extends Activity {
	
	
	private String[] taskTitleList = {};
	private String[] taskInfoList = {};
	private int[] taskIdList = {};
//	private String[] orderList = {"123"};
	
	private TextView title, subTitle;
	
    ArrayList<Map<String,Object>> data;
    
    ListView listView;
    Spinner order;
    
    private ProgressBar loadingBar;
    
    String taskType;
    
    MyHandler handler = new MyHandler();
    
    class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			if(msg.what == 0) {
				Toast.makeText(getApplicationContext(), MyApplication.NO_NETWORK_ERROR, 
						Toast.LENGTH_SHORT).show();
				return;
			}
			
			data = new ArrayList<Map<String,Object>>();;
			for(int i=0;i<taskTitleList.length;i++) {
	            Map<String,Object> item = new HashMap<String,Object>();
	            item.put("title", taskTitleList[i]);
	            item.put("info", taskInfoList[i]);
	            data.add(item); 
	        }
			SimpleAdapter adapter = new SimpleAdapter(
					TaskListActivity.this,
					data,
	        		android.R.layout.simple_list_item_2,
	                new String[]{"title","info"},
	                new int[]{android.R.id.text1,android.R.id.text2}
	        );
	        listView.setAdapter(adapter);
	        listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int pos, long id) {
					if(taskIdList[pos]== 0)
						return;
					Intent next = new Intent(TaskListActivity.this, DataListActivity.class);
					next.putExtra("task_id", taskIdList[pos]);
					startActivity(next);
				}
			});
	        
	        listView.setEnabled(true);
	        loadingBar.setVisibility(View.GONE);
	        
		}
	}
    
    class MyRunnable implements Runnable {
    	
    	String type;
    	
    	public MyRunnable(String str) {
			type = str;
		}
    	
		public void run() {
			String url = MyApplication.ANDROID_URL_BASE + "/get_task_list";
			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("type", type));
			String result = HttpHelper.get(url, params);
			try {
				JSONObject jo = new JSONObject(result);
				int total = jo.getInt("total");
				JSONArray task_list = jo.getJSONArray("task_list");
				taskTitleList = new String[total];
				taskInfoList = new String[total];
				taskIdList = new int[total];
				for(int i=0;i<total;i++) {
					JSONObject task = task_list.getJSONObject(i);
					taskTitleList[i] = task.getString("task_name");
					taskInfoList[i] = task.getString("task_desc");
					taskIdList[i] = task.getInt("task_id");
				}
				handler.sendEmptyMessage(1);
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
	    	handler.sendEmptyMessage(0);
		}
	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_task_list);
		
		listView = (ListView)findViewById(R.id.listView1);
		
		order = (Spinner)findViewById(R.id.order);
		
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		
		View back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		title = (TextView) findViewById(R.id.title);
		subTitle = (TextView) findViewById(R.id.subTitle);
		
		taskType = this.getIntent().getExtras().getString("type");
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		listView.setEnabled(false);
		loadingBar.setVisibility(View.VISIBLE);
		updateContent();
	}
	
	private void updateContent() {
		Runnable setDataList = new MyRunnable(taskType);
		new Thread(setDataList).start();
	}
}
