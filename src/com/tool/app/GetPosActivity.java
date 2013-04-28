package com.tool.app;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.client.MyApplication;
import com.example.client.R;
import com.myapp.MyItemizedOverlay;
import com.myapp.MyLocationListener;
import com.myapp.MyMapView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class GetPosActivity extends Activity {

	static BMapManager mBMapMan = null;
	static MyMapView mMapView;
	static MapController mMapController;
	static MyItemizedOverlay myOverlay;
	static MyLocationOverlay myLocationOverlay;
	
	static boolean flag = true;
	
	static EditText latitudeEt, longitudeEt;
	Button currentBtn,finishBtn;
	
	public static LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	
	public static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			double latitude = msg.getData().getDouble("latitude");
			double longitude = msg.getData().getDouble("longitude");
			latitudeEt.setText(latitude+"");
			longitudeEt.setText(longitude+"");
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init(MyApplication.BAIDU_MAP_API_KEY, null);
		setContentView(R.layout.activity_getpos);
		
		System.out.println("activity started");
		
		latitudeEt = (EditText)findViewById(R.id.latitude);
		longitudeEt = (EditText)findViewById(R.id.longitude);
		
		currentBtn = (Button)findViewById(R.id.current);
		currentBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mLocationClient.start();
				if (mLocationClient != null && mLocationClient.isStarted())
					mLocationClient.requestLocation();
				else 
					Log.d("LocSDK3", "locClient is null or not started");
			}
		});
		
		finishBtn = (Button)findViewById(R.id.finish);
		finishBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent next = new Intent();
				next.putExtra("latitude", latitudeEt.getText().toString());
				next.putExtra("longitude", longitudeEt.getText().toString());
				GetPosActivity.this.setResult(Activity.RESULT_OK, next);
				GetPosActivity.this.finish();
			}
		});
		
		System.out.println("init started");
		
		initMap();
		
		
	}
	
	public void initMap() {
		
		mMapView = (MyMapView)findViewById(R.id.bMapView);
		mMapView.setBuiltInZoomControls(true);
		mMapController = mMapView.getController();
		mMapController.setZoom(12);
		
		latitudeEt.setText(MyApplication.getLatitude() + "");
		longitudeEt.setText(MyApplication.getLongitude() + "");

		myLocationOverlay = new MyLocationOverlay(mMapView);
		mMapView.getOverlays().add(myLocationOverlay);
		
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		mLocationClient.setLocOption(MyApplication.option);
		
		System.out.println("init middle");
		
		mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();
		else 
			Log.d("LocSDK3", "locClient is null or not started");

	}
	
	public static void showMyPosition() {
		
		System.out.println("showMyPosition");
		
		latitudeEt.setText(MyApplication.getLatitude()+"");
		longitudeEt.setText(MyApplication.getLongitude()+"");	
		
		// 设置地图中心点
		GeoPoint point = new GeoPoint((int) (MyApplication.getLatitude() * 1e6),
				(int) (MyApplication.getLongitude() * 1e6));
		mMapController.setCenter(point);

		//显示自己的位置
		LocationData locData = new LocationData();
		locData.latitude = MyApplication.getLatitude();
		locData.longitude = MyApplication.getLongitude();
		locData.direction = 20.0f;
		
		myLocationOverlay.setData(locData);
		mMapView.refresh();
	}
	
	
	@Override
	protected void onDestroy(){
	        mMapView.destroy();
	        if(mBMapMan!=null){
	                mBMapMan.destroy();
	                mBMapMan=null;
	        }
	        super.onDestroy();
	}
	@Override
	protected void onPause(){
	        mMapView.onPause();
	        if(mBMapMan!=null){
	                mBMapMan.stop();
	        }
	        super.onPause();
	}
	@Override
	protected void onResume(){
	        mMapView.onResume();
	        if(mBMapMan!=null){
	                mBMapMan.start();
	        }
	        super.onResume();
	}

}
