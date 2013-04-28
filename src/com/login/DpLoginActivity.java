//package com.login;
//
//import java.util.List;
//
//import com.example.client.MyApplication;
//import com.example.client.R;
//import com.main.MainActivity;
//import com.myapp.ExitApplication;
//import com.tool.db.DataHelper;
//import com.user.OAuth;
//import com.user.User;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.Window;
//import android.view.View.OnClickListener;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemSelectedListener;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class DpLoginActivity extends Activity {
//	
//	String usernameArray[];
//	List<User> userList;
//	
//	private Spinner userSpinner;
//	private static Button dpLoginBtn;
//	private static Button authorizeBtn;
//	
//	private User selected_user;
//	
//	MyHandler handler = new MyHandler();
//	
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		ExitApplication.getInstance().addActivity(this);
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.activity_dplogin);
//		
//		userList = MyApplication.getUserList();
//		usernameArray = new String[userList.size()];
//		
//		for(int i=0;i<userList.size();i++) {
//			User user = userList.get(i);
//			usernameArray[i] = user.getUsername();
//		}
//		
//		ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(this, 
//				android.R.layout.simple_spinner_item, usernameArray);
//		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		userSpinner = (Spinner)findViewById(R.id.spinner1);
//		userSpinner.setAdapter(adapter);
//		userSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//					selected_user = userList.get(arg2);
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//
//			}
//		});
//		
//		dpLoginBtn = (Button)findViewById(R.id.dpLoginBtn);
//		dpLoginBtn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				
//				dpLoginBtn.setEnabled(false);
//				dpLoginBtn.setText("登录中...");
//				
//				User user = new User();
//				user.setUsername(selected_user.getUsername());
//				user.setEmail(selected_user.getEmail());
//				MyApplication.setUser(user);
//				new Thread(login).start();
//			}
//		});
//		
//		authorizeBtn = (Button)findViewById(R.id.authorizeBtn);
//		authorizeBtn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				Uri nextUri = Uri.parse(OAuth.logout_url());
//				Intent next = new Intent(Intent.ACTION_VIEW, nextUri);
//				startActivity(next);
//			}
//		});
//	}
//	
//	class MyHandler extends Handler {
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			dpLoginBtn.setEnabled(true);
//			dpLoginBtn.setText("登录");
//			String info = msg.getData().getString("info");
//			Toast.makeText(getApplicationContext(), info,
//				     Toast.LENGTH_SHORT).show();
//		}
//	}
//	
//	Runnable login = new Runnable() {	
//		@Override
//		public void run() {
//			Looper.prepare();
//			Message msg = new Message();
//			Bundle bundle = new Bundle();
//			int status = MyApplication.getUser().dp_login();
//			if(status == 1) {
//				bundle.putString("info", "登录成功");
//				msg.setData(bundle);
//				Intent next = new Intent(DpLoginActivity.this, MainActivity.class);
//				startActivity(next);
//			} else {
//				bundle.putString("info", "授权已过期");
//				msg.setData(bundle);
//			}
//			handler.sendMessage(msg);
//		}
//	};
//	
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK
//				|| keyCode == KeyEvent.KEYCODE_HOME) {
//			Intent back = new Intent(DpLoginActivity.this, LoginActivity.class);
//			startActivity(back);
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//}
