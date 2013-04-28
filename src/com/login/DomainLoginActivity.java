package com.login;

import com.example.client.MyApplication;
import com.example.client.R;
import com.main.MainActivity;
import com.myapp.ExitApplication;
import com.tool.client.StrHelper;
import com.user.User;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DomainLoginActivity extends Activity {
	
	Button loginBtn;
	EditText accountEt;
	EditText passwordEt;
	
	MyHandler handler = new MyHandler();
	
	private String account;
	private String password;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_domain_login);
		
		loginBtn = (Button)findViewById(R.id.login);
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
			int status = MyApplication.getUser().domain_login(password);
			if(status == 1) {
				bundle.putString("info", "登录成功");
				msg.setData(bundle);
				Intent next = new Intent(DomainLoginActivity.this, MainActivity.class);
				startActivity(next);
			} else {
				bundle.putString("info", "用户名或密码错误");
				msg.setData(bundle);
			}
			handler.sendMessage(msg);
		}
	};
}
