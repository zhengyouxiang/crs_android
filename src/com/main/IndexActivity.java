package com.main;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.client.R;
import com.myapp.MyApp;
import com.tool.app.CameraActivity;
import com.tool.client.BitmapHelper;
import com.tool.client.HttpHelper;

public class IndexActivity extends Activity {
	
	private Button sendBtn;
	private Button postBtn;
	private TextView resultTv;
	private LinearLayout layout;
	private Bitmap bitmap;
	private ImageView imageView;
	
	private static final int PID = 992384382;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);
		
		sendBtn = (Button) findViewById(R.id.send);
		sendBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent next = new Intent(IndexActivity.this, CameraActivity.class);
				startActivityForResult(next, 0);
			}
		});
		
		resultTv = (TextView) findViewById(R.id.result);
		layout = (LinearLayout) findViewById(R.id.layout);
		imageView = new ImageView(this);
		layout.addView(imageView);
		
		postBtn = new Button(this);
		postBtn.setText("post");
		postBtn.setId(PID);
		postBtn.setOnClickListener(new OnClickListener() {
			
			String code;
			String url;
			List<BasicNameValuePair> params;
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				code = BitmapHelper.bitmapToBase64(bitmap);
				url = MyApp.ANDROID_URL_BASE + "/bitmap";
		    	params = new LinkedList<BasicNameValuePair>();
		    	params.add(new BasicNameValuePair("pic", code));
		    	new Thread() {
		    		public void run() {
		    			System.out.println(code.length());
		    			System.out.println(code);
		    			String result = HttpHelper.post(url, params);
		    			myHandler.sendEmptyMessage(0);
		    		}
		    	}.start();
//		    	imageView.setImageBitmap(bitmap);
			}
				
		});
		
	}
	
	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			System.out.println("success");
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_index, menu);
		return true;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case MyApp.TAKE_PHOTO:
			bitmap = MyApp.getCameraBitmap();
			imageView.setImageBitmap(bitmap);
			layout.removeView(findViewById(PID));
			layout.addView(postBtn);
			break;
		default:
			break;
		}
	}

}
