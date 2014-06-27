package com.example.stompbox.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	// コンストラクタ。
	public DatabaseHelper(Context context){
		super(context, "stompbox.db", null, 1);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// テーブル生成。
		db.execSQL(
			"create table table1 (" 
				+ "_id integer primary key autoincrement, " 
				+ "strValue text, "
				+ "binData blob"
			+ ");"
		);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
