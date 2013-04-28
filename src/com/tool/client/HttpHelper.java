package com.tool.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpHelper {
	
	public final static DefaultHttpClient httpClient = new DefaultHttpClient();
	
	public final static String jsonError = "Failed to connect to server" +
			", please check your network connection";
	
	public static String get(String url) {
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		return get(url,params);
	}
	
	public static String get(String url, List<BasicNameValuePair> params) {
		String output = new String();
		StringBuilder str = new StringBuilder();
		
		String param = URLEncodedUtils.format(params, "UTF-8");
		
		try {
			String getMethodStr = url;
			if(param.length() != 0)
				getMethodStr += "?" + param;
			HttpGet getMethod = new HttpGet(getMethodStr);
			HttpResponse response = httpClient.execute(getMethod);
			int res = response.getStatusLine().getStatusCode();
			if(res == 200) {
				BufferedReader buffer = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	            for(String s = buffer.readLine(); s != null ; s = buffer.readLine())
	            	str.append(s);
	            output = str.toString();
			}
			else {
				output = "HttpGet Error";
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			output = "ClientProtocolException";
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			output = "IOException";
			e.printStackTrace();
		}
		return output;
	}
	
	public static String post(String url) {
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		return post(url,params);
	}
	
	public static String post(String url, List<BasicNameValuePair> params) {
		return post(url,params,null);
	}
	
	public static String post(String url, List<BasicNameValuePair> params, Map<String, File> files) {

		String output = new String();
		StringBuilder str = new StringBuilder();
		
		try {
			MultipartEntity mpEntity = new MultipartEntity();
			if(params != null && !params.isEmpty()) {
				for(int i=0;i<params.size();i++) {
					BasicNameValuePair b = params.get(i);
					String key = b.getName();
					String value = b.getValue();
					StringBody par = new StringBody(value);
					mpEntity.addPart(key, par);
				}
			}
			if(files != null && !files.isEmpty()) {
				for(Map.Entry<String, File> entry: files.entrySet()) {
					String key = entry.getKey();
					File file = entry.getValue();
					FileBody par = new FileBody(file);
					mpEntity.addPart(key, par);
				}
			}
			HttpPost post = new HttpPost(url);
			post.setEntity(mpEntity);
			HttpResponse response = httpClient.execute(post);
			
			int res = response.getStatusLine().getStatusCode();
			if(res == 200) {
				BufferedReader buffer = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	            for(String s = buffer.readLine(); s != null ; s = buffer.readLine())
	            	str.append(s);
	            output = str.toString();
			}
			else
				output += res;
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
		
		return output;
	}
}
