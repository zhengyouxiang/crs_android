package com.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.client.MyApplication;
import com.example.client.R;
import com.main.TaskListActivity.MyRunnable;
import com.tool.client.HttpHelper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MyTaskActivity extends Activity {
	
	private int[] taskIdList;
	private int[] dataIdList;
	private String[] titleList;
	private String[] taskNameList;
	
	
	private ListView listView;
	
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
			
			ArrayList<Map<String,Object>> data;
			data = new ArrayList<Map<String,Object>>();;
			for(int i=0;i<titleList.length;i++) {
	            Map<String,Object> item = new HashMap<String,Object>();
	            item.put("title", titleList[i]);
	            item.put("task_name", taskNameList[i]);
	            data.add(item); 
	        }
			SimpleAdapter adapter = new SimpleAdapter(
					MyTaskActivity.this,
					data,
	        		android.R.layout.simple_list_item_2,
	                new String[]{"title","task_name"},
	                new int[]{android.R.id.text1,android.R.id.text2}
	        );
	        listView.setAdapter(adapter);
	        listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int pos, long id) {
					if(taskIdList[pos]== 0)
						return;
					Intent next = new Intent(MyTaskActivity.this, DataActivity.class);
					next.putExtra("task_id", taskIdList[pos]);
					next.putExtra("data_id", dataIdList[pos]);
					startActivity(next);
				}
			});
	        
	        listView.setEnabled(true);
//	        loadingBar.setVisibility(View.GONE);
	        
		}
	}
    
    class MyRunnable implements Runnable {
    	
		public void run() {
			String url = MyApplication.ANDROID_URL_BASE + "/get_my_data_list";
			String result = HttpHelper.post(url);
			try {
				JSONArray ja = new JSONArray(result);
				int length = ja.length();
				titleList = new String[length];
				taskNameList = new String[length];
				taskIdList = new int[length];
				dataIdList = new int[length];
				
				for(int i=0;i<ja.length();i++) {
					JSONObject jo = ja.getJSONObject(i);
					titleList[i] = jo.getString("title");
					taskNameList[i] = jo.getString("task_name");
					taskIdList[i] = jo.getInt("task_id");
					dataIdList[i] = jo.getInt("data_id");
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
		setContentView(R.layout.activity_my_task);
		listView = (ListView)findViewById(R.id.listView1);
		
		View back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
    	
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		listView.setEnabled(false);
//		loadingBar.setVisibility(View.VISIBLE);
		updateContent();
	}
	
	private void updateContent() {
		Runnable setDataList = new MyRunnable();
		new Thread(setDataList).start();
	}

}
