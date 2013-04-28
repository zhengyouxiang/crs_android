package com.user;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.client.MyApplication;
import com.login.AuthorizeActivity;
import com.tool.client.HttpHelper;

import android.content.Intent;
import android.database.SQLException;
import android.graphics.Paint.Join;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class OAuth {
	//openplatform address
	final static String baseUrl = MyApplication.OAUTH_URL_BASE;
	//secret&id
	final static String client_secret = "4be5ad4c8f85b06ab6a27981ba3d9938";
	final static String client_id = "dp_crowdsourcing";
	//DO NOT MODIFY
	final static String redirect_uri = "android://oauth_code";
	final static String logout_redirect_uri = "android://logout_success";
	//parameters..
	final static String response_type = "code";
	final static String grant_type = "authorization_code";
	final static String scope = "read";
	
	public static String logout_url() {
		String url = baseUrl + "/logout";
		url += "?redirect_uri=" + logout_redirect_uri;
		return url;
	}
	
	public static String get_code_url() {
		String url = baseUrl + "/authorize?";
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("client_id", client_id));
		params.add(new BasicNameValuePair("redirect_uri", redirect_uri));
		params.add(new BasicNameValuePair("response_type", response_type));
		params.add(new BasicNameValuePair("scope", scope));
		url += URLEncodedUtils.format(params, "UTF-8");
		return url;
	}
	
	public static String get_access_token_url(String code) {
		String url = baseUrl + "/token?";
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("client_secret", client_secret));
		params.add(new BasicNameValuePair("client_id", client_id));
		params.add(new BasicNameValuePair("grant_type", grant_type));
		params.add(new BasicNameValuePair("redirect_uri", redirect_uri));
		params.add(new BasicNameValuePair("code", code));
		url += URLEncodedUtils.format(params, "UTF-8");
		return url;
	}
	
	public static String get_details(String access_token) {
		String url = baseUrl + "/details?";
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("access_token", access_token));
		url += URLEncodedUtils.format(params, "UTF-8");
		return url;
	}

	public static String getRedirectUri() {
		return redirect_uri;
	}

	public static String getLogoutRedirectUri() {
		return logout_redirect_uri;
	}
}
