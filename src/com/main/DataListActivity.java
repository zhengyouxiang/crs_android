package com.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DataListActivity extends Activity {
	
	private int task_id;
	
	private String[] dataTitleList = {};
	private String[] dataInfoList = {};
	private int[] dataIdList = {};
	
    ArrayList<Map<String,Object>> data;
    
    private static String task_description = new String();
    
    private TextView title, subTitle;
    ListView listView;
    
    private static RelativeLayout receive;

    private ProgressBar loadingBar;
    
    MyHandler handler = new MyHandler();
    ReceiveHandler receiveHandler = new ReceiveHandler();
    
    class ReceiveHandler extends Handler {
    	@Override
		public void handleMessage(Message msg) {
    		int status = msg.what;
    		switch(status) {
    		case 0:
    			Toast.makeText(getApplicationContext(), MyApplication.NO_NETWORK_ERROR, 
						Toast.LENGTH_SHORT).show();
    			break;
    		}
    		receive.setVisibility(View.GONE);
    		listView.setEnabled(true);
    	}
    }
    
    class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			if(msg.what == 0) {
				Toast.makeText(getApplicationContext(), MyApplication.NO_NETWORK_ERROR, 
						Toast.LENGTH_SHORT).show();
				return;
			}
			
			subTitle.setText(task_description);
			
			data = new ArrayList<Map<String,Object>>();;
			for(int i=0;i<dataTitleList.length;i++) {
	            Map<String,Object> item = new HashMap<String,Object>();
	            item.put("title", dataTitleList[i]);
	            item.put("info", dataInfoList[i]);
	            data.add(item); 
	        }
			SimpleAdapter adapter = new SimpleAdapter(
					DataListActivity.this,
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
					if(dataIdList[pos]== 0)
						return;
					receive.setVisibility(View.VISIBLE);
					listView.setEnabled(false);
					Runnable receiveTask = new receive(task_id, dataIdList[pos], dataTitleList[pos]);
					new Thread(receiveTask).start();
					
				}
			});
	        
	        listView.setEnabled(true);
	        loadingBar.setVisibility(View.GONE);
	        
		}
	}
    
    class receive implements Runnable {
    	
    	private int task_id = 0;
    	private int data_id = 0;
    	String title = "";
    	
    	public receive(int a, int b, String c) {
    		task_id = a;
    		data_id = b;
    		title = c;
    	}
    	
    	public void run() {
    		String url = MyApplication.ANDROID_URL_BASE + "/receive_task/" + task_id + "/" + data_id ;
    		String result = HttpHelper.post(url);
    		int status = 0;
    		try {
				JSONObject jo = new JSONObject(result);
				status = jo.getInt("status");
			} catch (JSONException e) {
				e.printStackTrace();
			}
    		
    		if(status == 0) {
    			receiveHandler.sendEmptyMessage(0);
    			return;
    		}
    		receiveHandler.sendEmptyMessage(1);
    		Intent next = new Intent(DataListActivity.this, DataActivity.class);
			next.putExtra("task_id", task_id);
			next.putExtra("data_id", data_id);
			next.putExtra("title", title);
			startActivity(next);
    	}
    }
    
    class MyRunnable implements Runnable {
    	
		public void run() {
			String url = MyApplication.ANDROID_URL_BASE + "/get_data_list/" + task_id;
			String result = HttpHelper.post(url);
			try {
				JSONObject jo = new JSONObject(result);
				int total = jo.getInt("total");
				task_description = jo.getString("task_desc");
				JSONArray data_list = jo.getJSONArray("data_list");
				dataTitleList = new String[total];
				dataInfoList = new String[total];
				dataIdList = new int[total];
				for(int i=0;i<total;i++) {
					JSONObject data = data_list.getJSONObject(i);
					dataTitleList[i] = data.getString("title");
					dataInfoList[i] = data.getString("subtitle");
					dataIdList[i] = data.getInt("data_id");
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
		setContentView(R.layout.activity_data_list);
		
		listView = (ListView)findViewById(R.id.listView1);
		
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		
		receive = (RelativeLayout)findViewById(R.id.receive);
		
		View back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		title = (TextView) findViewById(R.id.title);
		subTitle = (TextView) findViewById(R.id.subTitle);
		
		task_id = this.getIntent().getExtras().getInt("task_id");
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		listView.setEnabled(false);
		loadingBar.setVisibility(View.VISIBLE);
		updateContent();
	}
	
	private void updateContent() {
		Runnable setDataList = new MyRunnable();
		new Thread(setDataList).start();
	}
}
