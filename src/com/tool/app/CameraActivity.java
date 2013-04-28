package com.tool.app;

import java.io.IOException;

import com.example.client.MyApplication;
import com.example.client.R;
import com.tool.client.BitmapHelper;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CameraActivity extends Activity {

	private CameraView cv;
	private Button takeBtn;
	private Camera mCamera;
	
	private TextView info;

	class CameraView extends SurfaceView {

		private SurfaceHolder holder = null;

		public CameraView(Context context) {
			super(context);
			holder = this.getHolder();
			holder.addCallback(new SurfaceHolder.Callback() {
				@Override
				public void surfaceDestroyed(SurfaceHolder holder) {
					mCamera.stopPreview();
					mCamera.release();
					mCamera = null;
				}
				
				@Override
				public void surfaceCreated(SurfaceHolder holder) {
					mCamera = Camera.open();
					try {
						mCamera.setPreviewDisplay(holder);
					} catch (IOException e) {
						mCamera.release();
						mCamera = null;
					}
				}
				
				@Override
				public void surfaceChanged(SurfaceHolder holder, int format,
						int width, int height) {
					Camera.Parameters parameters = mCamera.getParameters();
					parameters.setPictureFormat(PixelFormat.JPEG);
					
					if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
						parameters.setRotation(90);
					} else {
						parameters.setRotation(0);
					}
					mCamera.setParameters(parameters);
					mCamera.startPreview();
					mCamera.autoFocus(null);
				}
			});
			holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

	}

	public Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			
			int width = getWidth();
			int height = getHeight();
			
			bitmap = BitmapHelper.fitTo(bitmap, width, height);
			
//			MyApplication.setCameraBitmap(bitmap);
			
			Intent next = new Intent();
			
			CameraActivity.this.setResult(MyApplication.TAKE_PHOTO, next);
			CameraActivity.this.finish();
		}
	};
	
	private boolean is_alpha_up = true;
	
	Handler handler = new Handler();
	
	Runnable flick = new Runnable() {
		
		@Override
		public void run() {
			int clock = 2000;
			Animation alpha_up = new AlphaAnimation(0.2f,0.8f);
			Animation alpha_down = new AlphaAnimation(0.8f,0.2f);
			alpha_up.setDuration(clock);
			alpha_down.setDuration(clock);
			
			if(is_alpha_up) {
				info.setAnimation(alpha_up);
				is_alpha_up = false;
			}
			else {
				info.setAnimation(alpha_down);
				is_alpha_up = true;
			}
			handler.postDelayed(flick, clock);
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.activity_camera);
		
		info = (TextView)findViewById(R.id.info);
		handler.post(flick);
		
		RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout);
		
		cv = new CameraView(this);
		cv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mCamera.autoFocus(null);
			}
		});
		layout.addView(cv);
		
		takeBtn = (Button)findViewById(R.id.takePhotoBtn);
		takeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mCamera.takePicture(null, null, pictureCallback);
			}
		});
		
		LinearLayout front = (LinearLayout)findViewById(R.id.front);
		front.bringToFront();
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("yao", "MainActivity.onKeyDown");
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return cv.onKeyDown(keyCode, event);
	}
	
	private int getWidth() {
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		System.out.println(width);
		return width;
	}
	
	private int getHeight() {
		Display display = getWindowManager().getDefaultDisplay();
		int height = display.getHeight();
		System.out.println(height);
		return height;
	}

}
