package com.example.stompbox.utils;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

public class DbUtil extends SQLiteOpenHelper {
	private static final String TAG = "DbUtil";
    private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "stompbox.db";
	private static final String[] TABLE_NAME = {"TABLE1", "TABLE2"};	// 下のCREATE_SQLと要素番号を合わせる事。
	private static final String[] CREATE_SQL = {
		"CREATE TABLE TABLE1 ("
			+"_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+"name TEXT NOT NULL, "
			+"tel TEXT NOT NULL, "
			+"email TEXT NOT NULL, "
			+"sex INTEGER, "
			+"address TEXT, "
			+"photo BLOB, "
			+"update_date TEXT NOT NULL"
		+"); ",
		"CREATE TABLE TABLE2 ("
			+ "_id INTEGER PRIMARY KEY, "
			+ "update_date TEXT NOT NULL, "
			+ "nid INTEGER NOT NULL, "
			+ "n_update_date TEXT NOT NULL"
		+ "); " 
	};

    public static SQLiteDatabase db = null;	// どのActivityからでも呼びやすいようにここで定義。

    // コンストラクタ。
    public DbUtil(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.v(TAG, "db ready");
        if(db != null) db.close();
        db = this.getWritableDatabase();	// Activity単位で開いて閉じる（cleanup()）。
    }
    // 初回DB構築。
    public void onCreate(SQLiteDatabase db) {
        Log.v(TAG, "create table");
    	for(int i = 0; i < CREATE_SQL.length; i++) {
    		db.execSQL(CREATE_SQL[i]);
    	}
    }
    // DBバージョンが違っていた場合にDBを再構築。今回は恐らく意識する必要無し。
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		Log.v(TAG, "Upgrade db from version " + oldVer + " to " + newVer + ", which will destroy all old data");
		for(int i = 0; i < TABLE_NAME.length; i++){
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME[i] + ";");
		}
		onCreate(db);
	}
	public void cleanup(){
		if(db != null) db.close();
		db = null;
		this.close();
	}
	// プロフィール有無（件数を返す）。
	public static int getProfileCount(){
		int recNo = 0;
		Cursor c = db.rawQuery("select count(*) from USER_PROFILE", null);
		if(c != null){
			c.moveToFirst();	// １レコードしか無い前提。
			recNo = c.getInt(0);
			c.close();
		}
		return recNo;
	}
	public static HashMap<String, String> getProfile(){
		HashMap<String, String> profile = null;
		Cursor c = db.rawQuery("select name,tel,email,sex,address,photo,update_date,ext_update_date,ext_n_update_date from USER_PROFILE", null);
		if(c != null){
			if(c.getCount() > 0){
				c.moveToFirst();	// １レコードしか無い前提。
				profile = new HashMap<String, String>();
				profile.put("name", c.getString(0));
				profile.put("tel", c.getString(1));
				profile.put("email", c.getString(2));
				profile.put("sex", (c.getLong(3)==0)?"male":"female");
				profile.put("address", c.getString(4));
			}
			c.close();
		}
		return profile;
	}
	public static boolean delProfile(){
		boolean isProcessOk = false;
		try{
			db.execSQL("delete from USER_PROFILE");
			isProcessOk = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return isProcessOk;
	}
	// プロフィールのEXT情報更新。レコードは存在する前提で実行する。
	public static boolean updProfileExt(String extUpdateDate, String extNupdateDate){
		if(getProfileCount() > 0){	// レコードが存在する→update。
			Log.v(TAG, "EXTUPDATE, upd:"+extUpdateDate+", n_upd:"+extNupdateDate);
			try {
				SQLiteStatement stmt = db.compileStatement("UPDATE USER_PROFILE set ext_update_date=?, ext_n_update_date=?");
				int idx = 0;
				stmt.bindString(++idx, extUpdateDate);
				stmt.bindString(++idx, extNupdateDate);
				stmt.execute();
				stmt.close();
				return true;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return false;
	}
	// サーバから受け取った更新IDを追加。
	public static boolean insExtData(int id, String update_date, int nid, String n_update_date){
		boolean isProcessOk = false;
		try {
			Log.v(TAG, "insert ext, _id:" + id + ", upd:" + update_date + ", nid:" + nid + ", n_upd:" + n_update_date);
			SQLiteStatement stmt = db.compileStatement("INSERT INTO EXT_PHONE_BOOK (_id, update_date, nid, n_update_date) values (?,?,?,?)");
			int idx = 0;
			stmt.bindLong(++idx, id);
			stmt.bindString(++idx, update_date);
			stmt.bindLong(++idx, nid);
			stmt.bindString(++idx, n_update_date);
			stmt.execute();
			stmt.close();
			isProcessOk = true;
		}catch(Exception e){
			Log.v(TAG, "err.");
			e.printStackTrace();
		}
		Log.v(TAG, "inserted ext record");
		return isProcessOk;
	}
	// サーバから受け取ったn_update_dateを更新。
	public static boolean updExtData(int id, String update_date, int nid, String n_update_date){
		boolean isProcessOk = false;
		try {
			SQLiteStatement stmt = db.compileStatement("UPDATE EXT_PHONE_BOOK set update_date=?, nid=?, n_update_date=? where _id=?");
			int idx = 0;
			stmt.bindString(++idx, update_date);	// PHONE_BOOKの値。
			stmt.bindLong(++idx, nid);				// サーバから取得した値。
			stmt.bindString(++idx, n_update_date);	// サーバから取得した値。
			stmt.bindLong(++idx, id);	// キーとなる_id値。
			stmt.execute();
			stmt.close();
			isProcessOk = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return isProcessOk;
	}
	// ■■■■■ 1レコード削除。
	public static boolean delTelData(int targetId){
		boolean isProcessOk = false;
		try{
			db.execSQL("delete from PHONE_BOOK where _id = '" + targetId + "'");
			isProcessOk = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return isProcessOk;
	}
	public static boolean delAllPhBookData(){
		boolean isProcessOk = false;
		try{
			db.execSQL("delete from PHONE_BOOK");
			isProcessOk = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return isProcessOk;
	}
	public static boolean delAllExtData(){
		boolean isProcessOk = false;
		try{
			db.execSQL("delete from EXT_PHONE_BOOK");
			isProcessOk = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return isProcessOk;
	}
	// EXT_PHONE_BOOKレコード削除：_idをキー。
	public static boolean delExtDataById(int targetId){
		boolean isProcessOk = false;
		try{
			db.execSQL("delete from EXT_PHONE_BOOK where _id = '" + targetId + "'");
			isProcessOk = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return isProcessOk;
	}
	// EXT_PHONE_BOOKレコード削除：nidをキー。
	public static boolean delExtDataByNid(int targetId){
		boolean isProcessOk = false;
		try{
			db.execSQL("delete from EXT_PHONE_BOOK where nid = '" + targetId + "'");
			isProcessOk = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return isProcessOk;
	}
	// EXT_PHONE_BOOK＆PHONE_BOOKレコード削除：nidをキー。
	public static boolean delPhoneAndExtDataByNid(int targetId){
		boolean isProcessOk = false;
		try{
			Cursor c = db.rawQuery("select _id from EXT_PHONE_BOOK where nid = ?", new String[]{Integer.toString(targetId)});
			c.moveToFirst();
			if(c.getCount() > 0){	// 1レコードしか無い前提→ループせず。
				int delId = c.getInt(0);	// PHONE_BOOK側に該当レコードがあったら削除する。
				db.execSQL("delete from PHONE_BOOK where _id = '" + delId + "'");
			}
			c.close();
			db.execSQL("delete from EXT_PHONE_BOOK where nid = '" + targetId + "'");
			isProcessOk = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return isProcessOk;
	}
	// コンタクトデータを取得し、リスト返却。
	public static ArrayList<HashMap<String, String>> getContacts(Activity activity){
		ArrayList<HashMap<String, String>> contactsList = new ArrayList<HashMap<String, String>>();
		// コンタクトデータ（トップ階層）取得用のカーソル。
    	Cursor c = activity.getContentResolver().query(
    		Contacts.CONTENT_URI,
    		new String[]{
				Contacts._ID, 
				ContactsContract.Contacts.DISPLAY_NAME, 
				ContactsContract.Contacts.HAS_PHONE_NUMBER, 
				ContactsContract.Contacts.PHOTO_ID
    		},
    		null, //selection(String): column = ?のような指定。
            null, //selectionArgs(String[]): 上記?に当てはめる値。
            Contacts._ID  //sortOrder(String)
    	);
//    	c.moveToFirst();	// 一応巻き戻し。→これやるとmoveToNext()が2レコード目からになってしまう（do～whileならok）。
    	if(c != null){
    		if(c.getCount() > 0){
	        	// コンタクトの数だけループ。
	            while (c.moveToNext()) {
	            	int clmIdx = 0;
	            	long contactsId = c.getLong(clmIdx++);
	            	String fullName = c.getString(clmIdx++);
	            	int hasPhone = c.getInt(clmIdx++);
	            	String phoneNo = null;
	            	String photoId = c.getString(clmIdx++);
	            	byte[] photoBytes = null;
	            	if(hasPhone > 0){
	            		phoneNo = getPhoneNumber(activity, contactsId);
	            	}
	            	String email = getEmailAddress(activity, contactsId);
	            	String address = getAddress(activity, contactsId);
	            	if(photoId != null){
	            		photoBytes = getPhoto(activity, contactsId, photoId);
	            	}
	            	HashMap<String, String> data = new HashMap<String, String>();
	            	data.put("contactsId", Long.toString(contactsId));
	            	data.put("fullName", fullName);
	            	data.put("phoneNo", phoneNo);
	            	data.put("email", email);
	            	data.put("address", address);
	            	contactsList.add(data);
	            }
    		}
    		c.close();
    	}
    	return contactsList;
	}
	// コンタクトIDと写真IDから写真データを取得。
    public static byte[] getPhoto(final Activity activity, long contactsId, String photoId){
		byte[] byteData = null;
    	Cursor c = activity.getContentResolver().query(
    		ContactsContract.Data.CONTENT_URI,
    		new String[]{
    			ContactsContract.CommonDataKinds.Photo.PHOTO
    		},
    		ContactsContract.CommonDataKinds.Photo.PHOTO_ID + "=?",
    		new String[]{photoId}, 
    		null
    	);
    	if(c != null){
    		if(c.getCount() > 0){
        		c.moveToFirst();	// 複数紐付いてはいない前提。いずれにせよ先頭レコードを使う。
        		byteData = c.getBlob(0);
    		}
    		c.close();
    	}
   		return byteData;
    }
	// コンタクトIDから電話番号データを取得。
    public static String getPhoneNumber(final Activity activity, long contactsId){
		String str = "";
    	Cursor c = activity.getContentResolver().query(
    		ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
    		new String[]{
    			ContactsContract.CommonDataKinds.Phone.NUMBER
    		},
    		ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
    		new String[]{Long.toString(contactsId)}, 
    		null
    	);
    	if(c != null){
    		if(c.getCount() > 0){
        		c.moveToFirst();	// 複数紐付いてはいない前提。いずれにせよ先頭レコードを使う。
        		str = c.getString(0);
    		}
    		c.close();
    	}
   		return str;
    }
	// コンタクトIDからメールアドレスを取得。
    public static String getEmailAddress(final Activity activity, long contactsId){
		String str = "";
    	Cursor c = activity.getContentResolver().query(
    		ContactsContract.CommonDataKinds.Email.CONTENT_URI,
    		new String[]{
    			ContactsContract.CommonDataKinds.Email.DATA, 
    		},
    		ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?",
    		new String[]{Long.toString(contactsId)}, 
    		null
    	);
    	if(c != null){
    		if(c.getCount() > 0){
        		c.moveToFirst();	// 複数紐付いてはいない前提。いずれにせよ先頭レコードを使う。
        		str = c.getString(0);
    		}
    		c.close();
    	}
		return str;
    }
	// コンタクトIDから住所データを取得。
    public static String getAddress(final Activity activity, long contactsId){
		String str = "";
    	Cursor c = activity.getContentResolver().query(
    		ContactsContract.Data.CONTENT_URI,
    		new String[]{
    			ContactsContract.CommonDataKinds.StructuredPostal.POBOX, 
    			ContactsContract.CommonDataKinds.StructuredPostal.STREET, 
    			ContactsContract.CommonDataKinds.StructuredPostal.CITY, 
    			ContactsContract.CommonDataKinds.StructuredPostal.REGION, 
    			ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, 
    			ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, 
    			ContactsContract.CommonDataKinds.StructuredPostal.TYPE
    		},
    		ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=? and " + ContactsContract.Data.MIMETYPE + "=?",
    		new String[]{
    			Long.toString(contactsId), 
    			ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE
    		}, 
    		null
    	);
    	if(c != null){
    		if(c.getCount() > 0){
        		c.moveToFirst();	// 複数紐付いてはいない前提。いずれにせよ先頭レコードを使う。
        		int clmIdx = 0;
        		String pobox = c.getString(clmIdx++);
        		String street = c.getString(clmIdx++);
        		String city = c.getString(clmIdx++);
        		String region = c.getString(clmIdx++);
        		String postcode = c.getString(clmIdx++);
        		String country = c.getString(clmIdx++);
        		String type = c.getString(clmIdx++);
//        		return pobox+","+street+","+city+","+region+","+postcode+","+country+","+type;
        		str = 
    				 ((postcode==null)?"":postcode)
        			+((country==null)?"":country)
        			+((region==null)?"":region)
        			+((city==null)?"":city)
        			+((street==null)?"":street)
        		;
    		}
    		c.close();
    	}
   		return str;
    }
}
