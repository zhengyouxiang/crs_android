package com.tool.client;

public class StrHelper {
	public static int RSHash(String str) {
		int b = 378551;
		int a = 63689;
		int hash = 0;
		
		for(int i=0;i<str.length();i++) {
			char c = str.charAt(i);
			hash = hash * a + (c++);
			a *= b;
		}
	 
		return (hash & 0x7FFFFFFF);
	}
	
	public static boolean checkAccount(String account, String password) {
		boolean flag = false;
		flag = checkAccount(account);
		flag = checkPassword(password);
		return flag;
	}
	
	private static boolean checkAccount(String str) {
		// TODO check account
		return str.length()!=0;
	}
	
	private static boolean checkPassword(String str) {
		// TODO check password
		return str.length()!=0;
	}
	
	public static boolean isEqual(String str1, String str2) {
		return str1.compareTo(str2) == 0;
	}
	
}
