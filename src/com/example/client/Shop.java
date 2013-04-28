package com.example.client;

public class Shop {
	
	public Shop(int shop_id, String shop_name) {
		this.shop_id = shop_id;
		this.shop_name = shop_name;
	}
	
	private int shop_id;
	private String shop_name;
	private double latitude, longitude;
	
	public int getShop_id() {
		return shop_id;
	}
	public void setShop_id(int shop_id) {
		this.shop_id = shop_id;
	}
	public String getShop_name() {
		return shop_name;
	}
	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}
