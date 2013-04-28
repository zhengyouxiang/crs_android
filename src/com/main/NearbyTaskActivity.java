package com.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.security.auth.PrivateCredentialPermission;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.client.MyApplication;
import com.example.client.R;
import com.example.client.Shop;
import com.tool.client.HttpHelper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NearbyTaskActivity extends Activity {
	
	static BMapManager mBMapMan = null;
	static MapView mMapView;
	static MapController mMapController;
	static LocationClient mLocationClient = null;
	static MyLocationOverlay myLocationOverlay;
	static MyItemizedOverlay myItemizedOverlay;
	static ListView taskListView;
	
	static TextView shopNameTv, shopIdTv;
	static Spinner radiusSpinner, typeSpinner;
	static int radius = 500;
	static String category = "美食";
	
	UpdateHandler updateHandler = new UpdateHandler();
	ReceiveHandler receiveHandler = new ReceiveHandler();
	
	private static List<OverlayItem> mGeoList;
	private static List<Shop> shopList;
	private static String[] titleList;
	private static String[] taskNameList;
	private static int[] taskIdList;
	private static int[] dataIdList;
	private static Drawable[] drawableList;
	
	BDLocationListener myListener = new MyLocationListener();
	
	class UpdateHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				showItems();
				break;
			default:
				ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
				System.out.println("titleList.length:" + titleList.length);
				for (int i = 0; i < titleList.length; i++) {
					Map<String, Object> item = new HashMap<String, Object>();
					item.put("title", titleList[i]);
					item.put("taskName", taskNameList[i]);
					data.add(item);
				}
				SimpleAdapter adapter = new SimpleAdapter(
						NearbyTaskActivity.this, data,
						android.R.layout.simple_list_item_2, new String[] {
								"title", "taskName" }, new int[] {
								android.R.id.text1, android.R.id.text2 });
				taskListView.setAdapter(adapter);
				taskListView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int pos, long id) {
						if(dataIdList[pos]== 0)
							return;
//						receive.setVisibility(View.VISIBLE);
						taskListView.setEnabled(false);
						Runnable receiveTask = new receive(taskIdList[pos], dataIdList[pos]);
						new Thread(receiveTask).start();
					}
				});
				break;
			}

		}
	}
	
	class ReceiveHandler extends Handler {
    	@Override
		public void handleMessage(Message msg) {
    		int status = msg.what;
    		switch(status) {
    		case 0:
    			Toast.makeText(getApplicationContext(), MyApplication.NO_NETWORK_ERROR, 
						Toast.LENGTH_SHORT).show();
    			break;
    		}
//    		receive.setVisibility(View.GONE);
    		taskListView.setEnabled(true);
    	}
    }
	
	class receive implements Runnable {

		private int task_id = 0;
		private int data_id = 0;
		String title = "";

		public receive(int a, int b) {
			task_id = a;
			data_id = b;
		}

		public void run() {
			String url = MyApplication.ANDROID_URL_BASE + "/receive_task/"
					+ task_id + "/" + data_id;
			String result = HttpHelper.post(url);
			int status = 0;
			try {
				JSONObject jo = new JSONObject(result);
				status = jo.getInt("status");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (status == 0) {
				receiveHandler.sendEmptyMessage(0);
				return;
			}
			receiveHandler.sendEmptyMessage(1);
			Intent next = new Intent(NearbyTaskActivity.this, DataActivity.class);
			next.putExtra("task_id", task_id);
			next.putExtra("data_id", data_id);
			next.putExtra("title", title);
			startActivity(next);
		}
	}
	
	class dataRunnable implements Runnable {
		int shop_id;
		
		public dataRunnable(int shop_id) {
			this.shop_id = shop_id;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String url = MyApplication.ANDROID_URL_BASE + "/get_data_list_by_shopid/";
			url += shop_id;
			String result = HttpHelper.post(url);
			
			try {
				JSONArray ja = new JSONArray(result);
				int length = ja.length();
				titleList = new String[length];
				taskNameList = new String[length];
				taskIdList = new int[length];
				dataIdList = new int[length];
				
				for(int i=0;i<length;i++) {
					JSONObject jo = ja.getJSONObject(i);
					String title = jo.getString("title");
					String taskName = jo.getString("task_name");
					int task_id = jo.getInt("task_id");
					int data_id = jo.getInt("data_id");
					titleList[i] = title;
					taskNameList[i] = taskName;
					taskIdList[i] = task_id;
					dataIdList[i] = data_id;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			updateHandler.sendEmptyMessage(shop_id);
		}
		
	}
	
	class updateRunnable implements Runnable {
		
		double latitude;
		double longitude;
		int page;
		
		private static final int DEFAULT_RADIUS = 500;
		
		public updateRunnable(double latitude, double longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
			this.page = 1;
		}
		
		public updateRunnable(double latitude, double longitude, int page) {
			this.latitude = latitude;
			this.longitude = longitude;
			this.page = page;
		}

		@Override
		public void run() {
			
			String url;
			List<BasicNameValuePair> params;
			
			url = MyApplication.ANDROID_URL_BASE + "/get_nearby_shops";
			params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("latitude", latitude+""));
			params.add(new BasicNameValuePair("longitude", longitude+""));
			params.add(new BasicNameValuePair("radius", radius+""));
			params.add(new BasicNameValuePair("category", category));
			String result = HttpHelper.post(url, params);
			
			try {
				JSONArray ja = new JSONArray(result);
				int length = ja.length();
				if(length == 0)
					return;
				shopList = new ArrayList<Shop>();
				for(int i=0;i<ja.length();i++) {
					JSONObject jo = ja.getJSONObject(i);
					int shop_id;
					String shop_name;
					double latitude, longitude;
					
					shop_id = jo.getInt("shop_id");
					shop_name = jo.getString("shop_name");
					latitude = jo.getDouble("lat");
					longitude = jo.getDouble("lon");
					
					Shop shop = new Shop(shop_id, shop_name);
					shop.setLatitude(latitude);
					shop.setLongitude(longitude);
					
					shopList.add(shop);
					
					int lat = (int)(latitude*1e6);
					int lon = (int)(longitude*1e6);
					
					OverlayItem item = new OverlayItem(
							new GeoPoint(lat, lon), 
							shop_name, 
							shop_name
						);
					item.setMarker(drawableList[i]);
					mGeoList.add(item);
					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(mGeoList.size() != 0)
				updateHandler.sendEmptyMessage(0);
		}
		
	}
	
	class MyLocationListener implements BDLocationListener {
		
		@Override
		public void onReceiveLocation(BDLocation location) {
			
			mLocationClient.stop();
			
			double lat = location.getLatitude();
			double lon = location.getLongitude();
			float direct = location.getDerect();
			GeoPoint point = new GeoPoint((int) (lat * 1e6), (int) (lon * 1e6));
			mMapController.setCenter(point);
			
			//显示自己的位置
			LocationData locData = new LocationData();
			locData.latitude = lat;
			locData.longitude = lon;
			locData.direction = direct;
			
			myLocationOverlay.setData(locData);
			mMapView.refresh();
			
			new Thread(new updateRunnable(lat, lon)).start();
			
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub
			
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init(MyApplication.BAIDU_MAP_API_KEY, null);
		setContentView(R.layout.activity_nearby_task);
		
		mMapView = (MapView) findViewById(R.id.mapView);
		taskListView = (ListView) findViewById(R.id.taskList);
		
		mMapView.setBuiltInZoomControls(true);
		mMapController = mMapView.getController();
		mMapController.setZoom(16);
		mMapController.enableClick(true);
		
		drawableList = new Drawable[8];
		drawableList[0] = getResources().getDrawable(R.drawable.icon_marka);
		drawableList[1] = getResources().getDrawable(R.drawable.icon_markb);
		drawableList[2] = getResources().getDrawable(R.drawable.icon_markc);
		drawableList[3] = getResources().getDrawable(R.drawable.icon_markd);
		drawableList[4] = getResources().getDrawable(R.drawable.icon_marke);
		drawableList[5] = getResources().getDrawable(R.drawable.icon_markf);
		drawableList[6] = getResources().getDrawable(R.drawable.icon_markg);
		drawableList[7] = getResources().getDrawable(R.drawable.icon_markh);
		
		View back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		radiusSpinner = (Spinner) findViewById(R.id.radius);
		typeSpinner = (Spinner) findViewById(R.id.type);
		
		shopNameTv = (TextView) findViewById(R.id.shop_name);
		shopIdTv = (TextView) findViewById(R.id.shop_id);
		
	}
	
	public void showItems() {
		for(int i=0;i<myItemizedOverlay.size();i++)
			myItemizedOverlay.removeItem(i);
	    for(OverlayItem item : mGeoList){
	    	myItemizedOverlay.addItem(item);
	    }
	    mMapView.refresh();
   }
	
	public void initMap() {
		
		mMapView.getOverlays().clear();
		mGeoList = new ArrayList<OverlayItem>();
		
		myLocationOverlay = new MyLocationOverlay(mMapView);
		mMapView.getOverlays().add(myLocationOverlay);
		Drawable marker = getResources().getDrawable(R.drawable.pop);
		myItemizedOverlay = new MyItemizedOverlay(marker, this);
		mMapView.getOverlays().add(myItemizedOverlay);

		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		mLocationClient.setLocOption(MyApplication.option);

		mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();
		else
			Log.d("LocSDK3", "locClient is null or not started");
		
	}
	
	@Override
	protected void onDestroy() {
		mMapView.destroy();
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		if (mBMapMan != null) {
			mBMapMan.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		if (mBMapMan != null) {
			mBMapMan.start();
		}
		initMap();
		super.onResume();
	}
	
	class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {
		public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
		private Context mContext = null;
		PopupOverlay pop = null;

		public MyItemizedOverlay(Drawable marker, Context context) {
			super(marker);
			this.mContext = context;
			pop = new PopupOverlay(NearbyTaskActivity.mMapView,
					new PopupClickListener() {

						@Override
						public void onClickedPopup() {
							Log.d("hjtest  ", "clickpop");
						}
					});
			populate();

		}

		protected boolean onTap(int index) {
			Drawable marker = this.mContext.getResources().getDrawable(
					R.drawable.pop); // 得到需要标在地图上的资源
			BitmapDrawable bd = (BitmapDrawable) marker;
			Bitmap popbitmap = bd.getBitmap();
			pop.showPopup(popbitmap, mGeoList.get(index).getPoint(), 32);
			Shop shop = shopList.get(index);
			shopNameTv.setText(shop.getShop_name());
			shopIdTv.setText(shop.getShop_id()+"");
			new Thread(new dataRunnable(shop.getShop_id())).start();
			super.onTap(index);
			return false;
		}

		public boolean onTap(GeoPoint pt, MapView mapView) {
			if (pop != null) {
				pop.hidePop();
			}
			super.onTap(pt, mapView);
			return false;
		}

		@Override
		protected OverlayItem createItem(int i) {
			return mGeoList.get(i);
		}

		@Override
		public int size() {
			return mGeoList.size();
		}

		public void addItem(OverlayItem item) {
			mGeoList.add(item);
			populate();
		}

		public void removeItem(int index) {
			mGeoList.remove(index);
			populate();
		}

	}

}


