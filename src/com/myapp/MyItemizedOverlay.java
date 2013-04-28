package com.myapp;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	
	private MapView mMapView;
	public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
	
	Drawable markA;

	public MyItemizedOverlay(Drawable arg0, MapView mapview) {
		super(arg0);
		markA = arg0;
		mMapView = mapview;
	}

	@Override
	protected OverlayItem createItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean onTap(GeoPoint pt, MapView mapView) {
		int x = pt.getLatitudeE6();
		int y = pt.getLongitudeE6();
		GeoPoint p = mMapView.getProjection().fromPixels(x, y);
		System.out.println(p.getLatitudeE6() / 1E6);
		System.out.println(p.getLongitudeE6() / 1E6);
		
		return super.onTap(pt, mapView);
	}

}