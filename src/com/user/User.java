package com.user;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.client.MyApplication;
import com.tool.client.Base64Helper;
import com.tool.client.HttpHelper;
import com.tool.client.RsaHelper;
import com.tool.db.DataHelper;

import android.R.integer;
import android.util.Base64;

public class User {
	public static final String ID = "_id";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String EMAIL = "email";
	public static final String CODE = "code";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String UPDATE_TIME = "update_time";
	public static final String TYPE = "type";

	private static final String DEFAULT_PASSWORD = "123456";

	private static final int DEFAULT = 0;
	private static final int DP_LOGIN = 1;
	private static final int DOMAIN_LOGIN = 2;

	private int id = 1;
	private String username;
	private String password;
	private String email;
	private String first_name;
	private String last_name;
	private int commit_count = 0;
	private int approved_count = 0;
	private int type = 0;
	
	public User() {
	}

	public User(String str) {
		username = str;
	}
	
	public int setUser() {
		String url = MyApplication.ANDROID_URL_BASE + "/get_user";
		String result = HttpHelper.post(url);
		JSONObject jo;
		try {
			jo = new JSONObject(result);
			setUsername(jo.getString("username"));
			setFirst_name(jo.getString("first_name"));
			setLast_name(jo.getString("last_name"));
			setEmail(jo.getString("email"));
			setCommit_count(jo.getInt("commit_count"));
			setApproved_count(jo.getInt("approved_count"));
			return 1;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println("get userinfo failed");
		return 0;
	}

	public int login(String password, int type) {
		
		String _password = password;
		
		String url = MyApplication.ANDROID_URL_BASE + "/login";
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		
		params.add(new BasicNameValuePair("type", type + ""));
		String account = RsaHelper.encode(username);
		params.add(new BasicNameValuePair("username", account));
		password = RsaHelper.encode(password);
		params.add(new BasicNameValuePair("password", password));
		
		String result = HttpHelper.post(url, params);
		
		try {
			JSONObject jo = new JSONObject(result);
			int login_status = jo.getInt("login_status");
			if(login_status == 1) {
				setUsername(jo.getString("username"));
				saveUser(_password, type);
				MyApplication.SetCookie();
				return 1;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println("login failed");
		return 0;
	}

	public int login(String password) {
		return login(password, DEFAULT);
	}

	public int dp_login() {
		return login(DEFAULT_PASSWORD, DP_LOGIN);
	}

	public int domain_login(String password) {
		return login(password, DOMAIN_LOGIN);
	}
	
	public void saveUser(String password, int type) {
		int status;
		status = DataHelper.SaveUserInfo(this, password, type);
	}
	
	public int loadUser() {
		int status = 0;
		return status;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirst_name() {
		return first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public int getCommit_count() {
		return commit_count;
	}

	public int getApproved_count() {
		return approved_count;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public void setCommit_count(int commit_count) {
		this.commit_count = commit_count;
	}

	public void setApproved_count(int approved_count) {
		this.approved_count = approved_count;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
