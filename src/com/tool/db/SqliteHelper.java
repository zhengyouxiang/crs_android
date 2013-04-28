package com.tool.db;

import com.user.User;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqliteHelper extends SQLiteOpenHelper {
	
	public static final String TB_NAME = "user";

	public SqliteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+
                TB_NAME + "("+
                User.ID + " integer primary key,"+
                User.USERNAME+" varchar,"+
                User.PASSWORD+" varchar,"+
                User.EMAIL+" varchar,"+
                User.TYPE+" integer,"+
                User.UPDATE_TIME+" datetime not null default current_timestamp"+
                ")"
                );
        Log.i("Database","onCreate");
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(db);
        Log.i("Database","onUpgrade");
	}
	
	public void updateColumn(SQLiteDatabase db, String oldColumn,
			String newColumn, String typeColumn) {
		try {
		db.execSQL("ALTER TABLE " + TB_NAME + " CHANGE " + oldColumn + " "
				+ newColumn + " " + typeColumn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
