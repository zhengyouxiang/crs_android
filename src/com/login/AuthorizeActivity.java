package com.login;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.client.MyApplication;
import com.example.client.R;
import com.main.MainActivity;
import com.myapp.ExitApplication;
import com.tool.client.HttpHelper;
import com.tool.db.DataHelper;
import com.user.OAuth;
import com.user.User;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class AuthorizeActivity extends Activity {
	
	private String code = "";
	
	private boolean isLogout = false, isSendCode = false;
	
	MyHandler handler = new MyHandler();
	
	private static WebView webView;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_authorize);
		
		webView = (WebView)findViewById(R.id.webView);
		webView.clearCache(true);
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		
		webView.setWebViewClient(new MyClient());
		
		webView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
		
		webView.loadUrl(OAuth.logout_url());

	}
    
    class MyHandler extends Handler {
    	@Override
		public void handleMessage(Message msg) {
    		AuthorizeFailed();
    	}
    }
    
    class MyRunnable implements Runnable {
    	
    	String url;
    	
    	public MyRunnable(String str) {
    		url = str;
		}

		@Override
		public void run() {
			String result = HttpHelper.get(url);
			JSONObject jo;
			try {
				jo = new JSONObject(result);
				String account = jo.getString("Username");
				User user = new User(account);
				MyApplication.setUser(user);
				int status = MyApplication.getUser().dp_login();
				if(status == 1) {
					startActivity(new Intent(AuthorizeActivity.this, MainActivity.class));
					return;
				}
				handler.sendEmptyMessage(0);
			} catch (JSONException e) {
				e.printStackTrace();
				handler.sendEmptyMessage(0);
			}
		}
    	
    }
    
    class InJavaScriptLocalObj {
        public void showSource(String html) {
        	System.out.println(html);
            Pattern p = Pattern.compile("[{].*[}]");
            Matcher m = p.matcher(html);
            m.find();
            String result = "";
            if(m.group() != null)
            	result = m.group();
            try {
				JSONObject jo = new JSONObject(result);
				String access_token = jo.getString("access_token");
				String url = OAuth.get_details(access_token);
				new Thread(new MyRunnable(url)).start();
			} catch (JSONException e) {
				AuthorizeFailed();
			}
            
        }
    }
    
    class MyClient extends WebViewClient {
    	
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			
			Log.d("client", "onPageStarted url = " + url);
			
			Uri uri = Uri.parse(url);
			String code = uri.getQueryParameter("code");
			
			// 截获取消授权的错误
			if(url.contains("error=access_denied"))
				finish();
			
			// get_access_token
			if(url.contains(OAuth.getRedirectUri()) && code != null) {
				if(code == null || code.length() == 0 || isSendCode)
					return;
				isSendCode = true;
				view.loadUrl(OAuth.get_access_token_url(code));
				view.setVisibility(View.INVISIBLE);
			}
			
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			// get_code
			if(url.equals(OAuth.getLogoutRedirectUri()) && !isLogout) {
				isLogout = true;
				view.loadUrl(OAuth.get_code_url());
			}
			
			// login
			if(url.contains(MyApplication.OAUTH_URL_BASE+"/token?")) {
				view.loadUrl("javascript:window.local_obj" +
						".showSource(document.body.innerHTML);");
			}
			
		}
    	
    }
    
    private void AuthorizeFailed() {
    	Toast.makeText(getApplicationContext(), "授权失败，请重新尝试", Toast.LENGTH_SHORT).show();
    	finish();
    }
	
}
