package com.myapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tool.app.GetPosActivity;

public class MyMapView extends MapView {

	public MyMapView(Context arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	public MyMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		// 将像素坐标转为地址坐标
		GeoPoint pt = this.getProjection().fromPixels(x, y);
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putDouble("latitude", pt.getLatitudeE6() / 1E6);
		bundle.putDouble("longitude", pt.getLongitudeE6() / 1E6);
		msg.setData(bundle);
		GetPosActivity.handler.sendMessage(msg);
		return super.onTouchEvent(event);
	}
	
}