package com.tool.db;

import java.util.ArrayList;
import java.util.List;

import com.tool.client.Base64Helper;
import com.user.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataHelper {
	private static String DB_NAME = "local.db";
	private static int DB_VERSION = 8;
	private static SQLiteDatabase db;
	private static SqliteHelper dbHelper;
	
	final static private float TOKEN_EXPIRED_SEC = 3600*24*7;
	
	public static void start(Context context) {
		dbHelper = new SqliteHelper(context, DB_NAME, null, DB_VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	public static int SaveUserInfo(User user, String password, int type)
    {
		String username = user.getUsername();
        ContentValues values = new ContentValues();
        values.put(User.ID, 1);
        values.put(User.USERNAME, Base64Helper.encode(username.getBytes()));
        values.put(User.PASSWORD, Base64Helper.encode(password.getBytes()));
        values.put(User.TYPE, type);
        values.put(User.EMAIL, user.getEmail());
        db.delete(SqliteHelper.TB_NAME, "_id = 1", null);
        int id = (int) db.insert(SqliteHelper.TB_NAME, User.ID, values);
        return id;
    }
	
	public static User GetUserInfo()
    {
		String[] params = {"username", "password", "type"};
		Cursor c = db.query(SqliteHelper.TB_NAME, params, null,null,null,null,null,null);
		c.moveToFirst();
		
		try {
			User user;
			user = new User();
			String username = c.getString(0);
			String password = c.getString(1);
			int type = c.getInt(2);
			user.setUsername(new String(Base64Helper.decode(username)));
			user.setPassword(new String(Base64Helper.decode(password)));
			user.setType(type);
			return user;
		} catch (Exception e) {
			return null;
		}
		
    }
	
}
