package com.main;

import com.example.client.MyApplication;
import com.example.client.R;
import com.login.LoginActivity;
import com.myapp.ExitApplication;
import com.user.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	Button singleTask, multiTask, friendTask, totalRank;
	
	TextView usernameTv;
	TextView commitNumTv, approvedNumTv;
	
	MyHandler handler = new MyHandler();
	
	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int status = msg.what;
			if(status == 1) {
				User user = MyApplication.getUser();
				commitNumTv.setText(user.getCommit_count()+"");
				approvedNumTv.setText(user.getApproved_count()+"");
			}
			else {
				Log.i("userinfo","failed to get userinfo");
			}
		}
	}
	
	class MyRunnable implements Runnable {
		public void run() {
			int status = MyApplication.getUser().setUser();
			handler.sendEmptyMessage(status);
		}
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		singleTask = (Button)findViewById(R.id.singleTask);
		multiTask = (Button)findViewById(R.id.multiTask);
		friendTask = (Button)findViewById(R.id.friendTask);
		totalRank = (Button)findViewById(R.id.totalRank);
		
		usernameTv = (TextView)findViewById(R.id.username);
		commitNumTv = (TextView)findViewById(R.id.commitNum);
		approvedNumTv = (TextView)findViewById(R.id.ApprovedNum);
		
		usernameTv.setText(MyApplication.getUser().getUsername());
		
		usernameTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, MyTaskActivity.class));
			}
		});
		
		singleTask.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent next = new Intent(MainActivity.this, SingleTaskActivity.class);
				startActivity(next);
			}
		});
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateContent();
	}
	
	private void updateContent() {
		Runnable setDataList = new MyRunnable();
		new Thread(setDataList).start();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_HOME) {
			showExitAlert();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void showExitAlert() {
		startActivity(new Intent(MainActivity.this, LoginActivity.class));
	}
	
}
