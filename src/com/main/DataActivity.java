package com.main;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.client.MyApplication;
import com.example.client.R;
import com.tool.app.GetPosActivity;
import com.tool.client.HttpHelper;
import com.tool.client.displayHelper;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class DataActivity extends Activity {
	
	int task_id;
	int data_id;
	String title;
	
	Thread getData;
	
	displayHelper display;
	JSONObject output_column_list, input_column_list;
	
	RelativeLayout submitMask;
	
	WebView outputWebView;
	WebView inputWebView;
	private ValueCallback<Uri> mUploadMessage;
	
	public static Button gpsBtn, photoBtn;
	private static Button submitBtn;
	
//	private static ProgressBar outputBar, inputBar;

	LoadHandler loadHandler = new LoadHandler();
	SubmitHandler submitHandler = new SubmitHandler();
	public static ButtonHandler buttonHandler = new ButtonHandler();
	
	private static final int DISPLAY = 0;
	private static final int OUTPUT = 1;
	private static final int INPUT = 2;
	private static final int ERROR = 3;
	
	private static final int SUBMIT_FAILED = 0;
	private static final int SUBMIT_SUCCESS = 1;
	private static final int RESET = 2;
	
	String url_submit;
	
	class SubmitHandler extends Handler {
    	@Override
		public void handleMessage(Message msg) {
    		
    	}
    }
    
    class LoadHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			int progress = msg.what;
			switch(progress){
			case DISPLAY:
				break;
			case OUTPUT:
				break;
			case INPUT:
				break;
			case ERROR:
				Toast.makeText(getApplicationContext(), MyApplication.NO_NETWORK_ERROR, 
						Toast.LENGTH_SHORT).show();
				break;
			}
			
		}
	}
    
    public static class ButtonHandler extends Handler {
//    	@Override
		public void handleMessage(Message msg) {
    		int status = msg.what;
    		switch (status) {
			case MyApplication.GET_POS:
				gpsBtn.setEnabled(true);
				gpsBtn.setText("获取位置");
				break;
			case MyApplication.TAKE_PHOTO:
				photoBtn.setEnabled(true);
				photoBtn.setText("拍摄照片");
			default:
				break;
			}
    	}
    }
    
    
    class submit implements Runnable {
    	
    	private List<BasicNameValuePair> params;
    	private Map<String, File> files;
    	
    	public submit(List<BasicNameValuePair> p, Map<String, File> f) {
    		params = p;
    		files = f;
    	}
    	
    	public void run() {
    		String url = MyApplication.ANDROID_URL_BASE + "/submit_task/" + task_id + "/" + data_id ;
			String result = HttpHelper.post(url, params, files);
			try {
				JSONObject jo = new JSONObject(result);
				int status = jo.getInt("status");
				if(status != 0) {
					submitHandler.sendEmptyMessage(SUBMIT_SUCCESS);
					finish();
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			submitHandler.sendEmptyMessage(SUBMIT_FAILED);
    	}
    }
    
    
    class UpdateRunnable implements Runnable {
		public void run() {
			
			
		}
	}
    
    private void synCookies(String url) {
    	System.out.println("get cookie success");
    	Cookie sessionCookie = MyApplication.getCookie();
    	
        CookieSyncManager.createInstance(this);  
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);  
//        cookieManager.removeSessionCookie();
        String cookieString = sessionCookie.getName() + "=" 
        		+ sessionCookie.getValue() + ";domain="
        		+ sessionCookie.getDomain();
        System.out.println(cookieString);
        cookieManager.setCookie(url, cookieString);
        CookieSyncManager.getInstance().sync();  
    }  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_data);
		
//		outputBar = (ProgressBar)findViewById(R.id.outputProgress);
//		inputBar = (ProgressBar)findViewById(R.id.inputProgress);
//		
//		outputBar.setVisibility(View.VISIBLE);
//		inputBar.setVisibility(View.VISIBLE);
		
		View back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		gpsBtn = (Button) findViewById(R.id.gps);
		photoBtn = (Button) findViewById(R.id.photo);
		submitBtn = (Button) findViewById(R.id.submit);
		
		gpsBtn.setEnabled(false);
		photoBtn.setEnabled(false);
		
		submitMask = (RelativeLayout) findViewById(R.id.submitMask);
		
		Intent intent = this.getIntent();
		task_id = intent.getExtras().getInt("task_id");
		data_id = intent.getExtras().getInt("data_id");
		title = intent.getExtras().getString("title");
		
		outputWebView = (WebView)findViewById(R.id.outputWebView);
		inputWebView = (WebView)findViewById(R.id.inputWebView);
		
		inputWebView.getSettings().setJavaScriptEnabled(true);

		inputWebView.addJavascriptInterface(new Object() {
			public void setGpsEnabled() {
				buttonHandler.sendEmptyMessage(MyApplication.GET_POS);
			}
			public void setPhotoEnabled() {
				buttonHandler.sendEmptyMessage(MyApplication.TAKE_PHOTO);
			}
		}, "button");
		
		inputWebView.setWebChromeClient(new WebChromeClient() {
			@SuppressWarnings("unused")
			
//			For Android > 4.1.1
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
	               openFileChooser(uploadMsg, acceptType);
	       }
			
//			For Android < 4.1.1
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
				openFileChooser(uploadMsg);
				}
			
//			For Android < 3.0
			public void openFileChooser(ValueCallback<Uri> uploadFile) {
				
				System.out.println("123");
				
				if (mUploadMessage != null)
					return;
				mUploadMessage = uploadFile;
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, "Image Browser"),
						MyApplication.PICK_IMAGE);
			}
		});
		
		gpsBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent next = new Intent(DataActivity.this, GetPosActivity.class);
				startActivityForResult(next, MyApplication.GET_POS);
			}
		});
		
		photoBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, MyApplication.TAKE_PHOTO);
			}
		});
		
		submitBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				inputWebView.loadUrl("javascript:submitForm()");
				submitBtn.setEnabled(false);
				submitBtn.setText("提交中...");
				
			}
		});
		
		String url_output = MyApplication.ANDROID_URL_BASE + 
				"/output_view/" + task_id + "/" + data_id;
		String url_input = MyApplication.ANDROID_URL_BASE + 
				"/input_view/" + task_id + "/" + data_id;
		synCookies(url_input);
		outputWebView.loadUrl(url_output);
		inputWebView.loadUrl(url_input);

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		inputWebView.destroy();
//		outputWebView.destroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK) {
			Bundle bundle = data.getExtras();
			switch (requestCode) {
			case MyApplication.GET_POS:
				double lat = (double) Double.parseDouble(bundle.getString("latitude"));
				double lon = (double) Double.parseDouble(bundle.getString("longitude"));
				Toast.makeText(getApplicationContext(), "经度:"+lon+" | 纬度:"+lat,
					     Toast.LENGTH_SHORT).show();
				inputWebView.loadUrl(
						"javascript:setLonLat(" + lon + "," + lat + ")"
						);
				break;
			case MyApplication.TAKE_PHOTO:	//处理系统照相机事件
				String sdStatus = Environment.getExternalStorageState();
				if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
					Log.e("TestFile", "SD card is not avaiable/writeable right now.");  
	                return;  
				}
				String name = DateFormat.format("yyyyMMdd_hhmmss",Calendar.getInstance(Locale.CHINA)) + ".jpg";
				Toast.makeText(this, "照片已保存至系统相册", Toast.LENGTH_LONG).show();
				
				Bitmap bitmap = (Bitmap) bundle.get("data");
				MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, name, "dpCrowdsorceImg");
				sendBroadcast(
						new Intent(Intent.ACTION_MEDIA_MOUNTED,
								Uri.parse("file://"+ Environment.getExternalStorageDirectory()))
						);
				break;
			case MyApplication.PICK_IMAGE:
				if (null == mUploadMessage)
					break;
				Uri result = data == null || resultCode != RESULT_OK ? null
						: data.getData();
				mUploadMessage.onReceiveValue(result);
				mUploadMessage = null;
				Log.i("onActivityResult", "onActivityResult");
				break;
			}
		}
	}

}
