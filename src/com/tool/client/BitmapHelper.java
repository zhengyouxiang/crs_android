package com.tool.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.util.Base64;

public class BitmapHelper {
	public static String bitmapToBase64(Bitmap bitmap) {  
		  
	    String result = null;  
	    ByteArrayOutputStream baos = null;  
	    try {  
	        if (bitmap != null) {  
	            baos = new ByteArrayOutputStream();  
	            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
	  
	            baos.flush();  
	            baos.close();  
	  
	            byte[] bitmapBytes = baos.toByteArray();  
	            result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
	        }  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally {  
	        try {  
	            if (baos != null) {  
	                baos.flush();  
	                baos.close();  
	            }  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }  
	    return result;  
	}  
	
	public static Bitmap fitTo(Bitmap bitmap, int dstWidth, int dstHeight) {
		Bitmap dstBitmap;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		double scale, scaleX, scaleY;
		scaleX = (double)dstWidth/(double)width;
		scaleY = (double)dstHeight/(double)height;
		scale = scaleX>scaleY?scaleX:scaleY;
		dstBitmap = Bitmap.createScaledBitmap(bitmap, (int)(width*scale), (int)(height*scale), false);
		bitmap.recycle();
		int dx,dy;
		dx = dstBitmap.getWidth()/2 - dstWidth/2;
		dy = dstBitmap.getHeight()/2 -dstHeight/2;
		dstBitmap = Bitmap.createBitmap(dstBitmap, dx, dy, dstWidth, dstHeight);
		return dstBitmap;
	}
	
	public static Bitmap getGrayBitmap(Bitmap src) {
		Bitmap dst = Bitmap.createBitmap(src);
		for(int i=0;i<dst.getWidth();i++)
			for(int j=0;j<dst.getHeight();j++) {
				int pixel = dst.getPixel(i, j);
				int r = (pixel >> 16) & 0xff;
				int g = (pixel >> 8) & 0xff;
				int b = pixel & 0xff;
				int graynum = (r*30+g*59+b*11)/100;
				dst.setPixel(i, j, Color.argb(255, graynum, graynum, graynum));
			}
		return dst;
	}
	
	public static Bitmap setBitmapWidth(Bitmap src, int dstWidth) {
		int height = src.getHeight();
		int width = src.getWidth();
		
		double scale = (double)dstWidth/(double)width;
		int dstHeight = (int) (height * scale);
		
		Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
		src.recycle();
		
		return dst;
	}
	
	public static Bitmap setBitmapScale(Bitmap src, int dstWidth, int dstHeight) {
		return Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
	}
	
	public static Bitmap getBitmap(String imagePath) {
		if(!(imagePath.length()>5))
			return null;
		File cache_file = new File(new 
				File(Environment.getExternalStorageDirectory(), "xxxx"),"cachebitmap");
		cache_file = new File(cache_file, getMD5(imagePath));
		if(cache_file.exists()) {
			return BitmapFactory.decodeFile(getBitmapCache(imagePath));
		} else {
			URL url;
			try {
				url = new URL(imagePath);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				if(conn.getResponseCode() == 200) {
					InputStream inStream = conn.getInputStream();
					File file = new File(new
							File(Environment.getExternalStorageDirectory(),"xxxx"),"cachebitmap");
					if(!file.exists())
						file.mkdirs();
					file = new File(file,getMD5(imagePath));
					FileOutputStream out = new FileOutputStream(file);
					byte buff[] = new byte[1024];
					int len = 0;
					while((len = inStream.read(buff))!=-1) {
						out.write(buff,0,len);
					}
					out.close();
					inStream.close();
					return BitmapFactory.decodeFile(getBitmapCache(imagePath));
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	public static String getBitmapCache(String url) {
		File file = new File(new
				File(Environment.getExternalStorageDirectory(),"xxxx"),"cachebitmap");
		file = new File(file,getMD5(url));
		if(file.exists()) {
			return file.getAbsolutePath();
		}
		return null;
	}
	
	public static String getMD5(String content) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.update(content.getBytes());
			return getHashString(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private static String getHashString(MessageDigest digest) {
		StringBuilder builder = new StringBuilder();
		for(byte b:digest.digest()) {
			builder.append(Integer.toHexString((b>>4) & 0xf));
			builder.append(Integer.toHexString(b & 0xf));
		}
		return builder.toString().toLowerCase();
	}
	
	
}
