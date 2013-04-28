package com.example.client;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.login.LoginActivity;
import com.main.MainActivity;
import com.myapp.ExitApplication;
import com.myapp.MyLocationListener;
import com.tool.client.HttpHelper;
import com.tool.client.RsaHelper;
import com.tool.db.DataHelper;
import com.user.User;

import android.os.Bundle;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.text.LoginFilter.UsernameFilterGeneric;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Window;
import android.widget.Toast;

public class LoadActivity extends Activity {
	
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Toast.makeText(getApplicationContext(), "没有有效的网络连接，请检查网络后重试",
					Toast.LENGTH_SHORT).show();
    	}
	};
	
	class LoadRunnable implements Runnable {
		@Override
		public void run() {
			try {
				RsaHelper.genKey();
				String url = MyApplication.ANDROID_URL_BASE + "/setPublicKey";
				String publicKey = HttpHelper.post(url);
				RsaHelper.setPublicKey(publicKey);
			} catch (Exception e) {
				e.printStackTrace();
				handler.sendEmptyMessage(0);
				finish();
			}
			
			try {
				User user = DataHelper.GetUserInfo();
				int type = user.getType();
				String password = user.getPassword();
				MyApplication.setUser(user);
				
				int status = MyApplication.getUser().login(password, type);
				if(status == 1) {
					startActivity(new Intent(LoadActivity.this, MainActivity.class));
					return;
				}
				
			} catch (Exception e) {
				MyApplication.setUser(null);	
			}
			
			Intent next = new Intent(LoadActivity.this, LoginActivity.class);
			startActivity(next);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_load);
		
		DisplayMetrics dm = new DisplayMetrics();
		MyApplication.setScreen(dm.widthPixels, dm.heightPixels);
		
		//设置系统变量
		MyApplication.start(this);
		
		new Thread(new LoadRunnable()).start();
		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_HOME) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
