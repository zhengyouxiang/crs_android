package com.login;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent.OnFinished;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.client.MyApplication;
import com.example.client.R;
import com.main.MainActivity;
import com.myapp.ExitApplication;
import com.tool.client.StrHelper;
import com.tool.db.DataHelper;
import com.user.User;

public class LoginActivity extends Activity {
	
	private static Button loginBtn;
	private static Button dpLoginBtn;
	private static Button domainLoginBtn;
	
	EditText accountEt;
	EditText passwordEt;
	
	MyHandler handler = new MyHandler();
	
	private String account;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		
		loginBtn = (Button)findViewById(R.id.login);
		dpLoginBtn = (Button)findViewById(R.id.dpLogin);
		domainLoginBtn = (Button)findViewById(R.id.domainLogin);
		
		accountEt = (EditText)findViewById(R.id.account);
		passwordEt = (EditText)findViewById(R.id.password);
		
		loginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public synchronized void onClick(View arg0) {
				
				loginBtn.setEnabled(false);
				loginBtn.setText("登录中...");
				
				account = accountEt.getText().toString();
				password = passwordEt.getText().toString();
				if(!StrHelper.checkAccount(account, password)) {
					Toast.makeText(getApplicationContext(), "用户名或密码格式不合法",
						     Toast.LENGTH_SHORT).show();
				}
				
				User user = new User(account);
				MyApplication.setUser(user);
				new Thread(login).start();
			}
		});
		
		domainLoginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent next = new Intent(LoginActivity.this, DomainLoginActivity.class);
	        	startActivity(next);
			}
		});
		
		
		dpLoginBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
		        Intent next = new Intent(LoginActivity.this, AuthorizeActivity.class);
		        startActivity(next);
			}
		});
		
	}
	
	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			loginBtn.setEnabled(true);
			loginBtn.setText("登录");
			String info = msg.getData().getString("info");
			Toast.makeText(getApplicationContext(), info,
				     Toast.LENGTH_SHORT).show();
		}
	}
	
	Runnable login = new Runnable() {	
		@Override
		public void run() {
			Looper.prepare();
			Message msg = new Message();
			Bundle bundle = new Bundle();
			int status = MyApplication.getUser().login(password);
			if(status == 1) {
				bundle.putString("info", "登录成功");
				msg.setData(bundle);
				Intent next = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(next);
			} else {
				bundle.putString("info", "用户名或密码错误");
				msg.setData(bundle);
			}
			handler.sendMessage(msg);
		}
	};
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_HOME) {
			showExitAlert();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void showExitAlert() {
		ExitApplication.getInstance().exit();
	}
	
}
