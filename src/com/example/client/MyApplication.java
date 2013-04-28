package com.example.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.SetCookie;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import com.baidu.location.LocationClientOption;
import com.tool.client.HttpHelper;
import com.tool.db.DataHelper;
import com.user.User;

public class MyApplication extends Application {

	public static Handler handler = new Handler();

	public static final String ANDROID_URL_BASE = "http://59.78.23.16:8000/android";

	public static final String OAUTH_URL_BASE = "http://api.dianping.com/oauth";

	public static final String BAIDU_MAP_API_KEY = "684A22A6D8B8E3E4A1E6B490E42974C136B6EDA2";

	public static int SCREEN_WIDTH = 0;
	public static int SCREEN_HEIGHT = 0;

	private static double latitude = 0.0;
	private static double longitude = 0.0;

	private static List<Cookie> cookies;
	private static Cookie cookie = null;

	public static final int TEXT = 0;
	public static final int MULTI_TEXT = 1;
	public static final int BOOLEAN = 2;
	public static final int MULTI_CHOICE = 3;
	public static final int INTEGER = 4;
	public static final int DATETIME = 5;
	public static final int PICTURE = 6;
	public static final int FLOAT = 7;

	public static final int TEXT_SIZE_LARGE = 25;
	public static final int TEXT_SIZE_MIDIUM = 20;
	public static final int TEXT_SIZE_SMALL = 16;

	public static final String TASK_PICTURE = "图片任务";
	public static final String TASK_VERIFY = "信息任务";
	public static final String TASK_ADD = "搜索任务";
	public static final String TASK_REVIEW = "点评评审任务";

	public static final String NO_NETWORK_ERROR = "没有有效的网络连接, 请连接网络后刷新页面重试";

	public static final int GET_POS = 1000;
	public static final int TAKE_PHOTO = 1001;
	public static final int PICK_IMAGE = 1002;

	private static final int RANDOM_SEED = 1351252;

	private static List<User> userList;
	private static User user;

	public static Map<String, Integer> inputTypeMap = new HashMap<String, Integer>();
	public static Map<String, String> taskTypeMap = new HashMap<String, String>();

	public MyApplication() {
	}

	public static LocationClientOption option = new LocationClientOption();

	public static void start(Context c) {

		inputTypeMap.put("text", TEXT);
		inputTypeMap.put("multi_text", MULTI_TEXT);
		inputTypeMap.put("boolean", BOOLEAN);
		inputTypeMap.put("multi_choice", MULTI_CHOICE);
		inputTypeMap.put("integer", INTEGER);
		inputTypeMap.put("datetime", DATETIME);
		inputTypeMap.put("picture", PICTURE);
		inputTypeMap.put("float", FLOAT);

		taskTypeMap.put("picture", TASK_PICTURE);
		taskTypeMap.put("verify", TASK_VERIFY);
		taskTypeMap.put("add", TASK_ADD);
		taskTypeMap.put("review", TASK_REVIEW);

		// Baidu Location Service Options
		option.setOpenGps(true);
		option.setAddrType("all"); // 返回的定位结果包含地址信息
		option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
		option.disableCache(true); // 禁止启用缓存定位
		option.setPoiNumber(5); // 最多返回POI个数
		option.setPoiDistance(1000); // poi查询距离
		option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息

		DataHelper.start(c);

	}

	public static void setScreen(int width, int height) {
		SCREEN_WIDTH = width;
		SCREEN_HEIGHT = height;
	}

	public static int generateId() {
		Random r = new Random();
		return r.nextInt(RANDOM_SEED) + RANDOM_SEED;
	}

	public static List<User> getUserList() {
		return userList;
	}

	public static void setUserList(List<User> userList) {
		MyApplication.userList = userList;
	}

	public static User getUser() {
		return user;
	}

	public static void setUser(User user) {
		MyApplication.user = user;
	}

	public static int getHeight(int part, int all) {
		return SCREEN_HEIGHT * part / all;
	}

	public static double getLatitude() {
		return latitude;
	}

	public static double getLongitude() {
		return longitude;
	}

	public static void setLatitude(double latitude) {
		MyApplication.latitude = latitude;
	}

	public static void setLongitude(double longitude) {
		MyApplication.longitude = longitude;
	}
	
	public static Cookie getCookie() {
		return cookie;
	}

	public static void SetCookie() {
		cookies = HttpHelper.httpClient.getCookieStore().getCookies();
		if (!cookies.isEmpty()) {
			for (int i = 0; i < cookies.size(); i++) {
				cookie = cookies.get(i);
				System.out.println(cookie.toString());
			}
		}
		System.out.println("set cookie success");
	}

}
